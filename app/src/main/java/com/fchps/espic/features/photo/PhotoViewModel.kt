package com.fchps.espic.features.photo

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fchps.domain.model.Feed
import com.fchps.domain.usecase.GetPseudoUseCase
import com.fchps.espic.features.photo.permission.PermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val getPseudoUseCase: GetPseudoUseCase,
) : ViewModel() {

    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.NotRequested)
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<PhotoEvent>()
    val eventFlow: SharedFlow<PhotoEvent> = _eventFlow.asSharedFlow()

    private var hasRequestedPermission = false

    fun addFeed(uri: Uri?, filter: String) {
        var pseudo: String
        viewModelScope.launch {
            getPseudoUseCase().onSuccess {
                pseudo = it
                val newFeed = Feed(
                    pseudo,
                    filter = filter,
                    id = UUID.randomUUID().hashCode(),
                    likes = 0,
                    type = "IMAGE",
                    uri = uri.toString(),
                    timeStamp = getCurrentFormattedDate()
                )
                _eventFlow.emit(PhotoEvent.NewFeedAdded(newFeed))
            }.onFailure {
                pseudo = ""
            }
        }
    }

    private fun getCurrentFormattedDate(): String {
        val now =
            ZonedDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
        return now.format(formatter)
    }


    fun requestCameraPermission() {
        if (!hasRequestedPermission) {
            hasRequestedPermission = true
            // Emit NotRequested to trigger the permission request
            viewModelScope.launch {
                _permissionState.emit(PermissionState.NotRequested)
            }
        } else {
            viewModelScope.launch {
                _eventFlow.emit(PhotoEvent.NavigateToSettings)
            }
        }
    }

    fun updatePermissionState(isGranted: Boolean = false, shouldShowRationale: Boolean = false) {
        viewModelScope.launch {
            _permissionState.value = when {
                isGranted -> PermissionState.Granted
                !isGranted && shouldShowRationale -> PermissionState.Denied
                !isGranted && hasRequestedPermission -> PermissionState.PermanentlyDenied
                else -> PermissionState.NotRequested
            }
        }
    }

    fun navigateToAppSettings() {
        viewModelScope.launch {
            _eventFlow.emit(PhotoEvent.NavigateToSettings)
        }
    }

    sealed class PhotoEvent {
        data object NavigateToSettings : PhotoEvent()
        data class NewFeedAdded(val feed: Feed) : PhotoEvent()
    }
}