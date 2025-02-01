package com.fchps.espic.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fchps.domain.usecase.DeletePseudoUseCase
import com.fchps.domain.usecase.GetPseudoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val deletePseudoUseCase: DeletePseudoUseCase,
    private val getPseudoUseCase: GetPseudoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<ProfileUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getPseudo()
    }

    private fun getPseudo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val pseudo = getPseudoUseCase()
                pseudo.onSuccess {
                    _uiState.value = _uiState.value.copy(pseudo = it, isLoading = false)
                }.onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _eventFlow.emit(ProfileUiEvent.ShowError("Failed to load pseudo"))
                }
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _eventFlow.emit(ProfileUiEvent.ShowError("Failed to load pseudo"))
            }
        }
    }

    fun onDisconnectClick() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                deletePseudoUseCase()
                _uiState.value = _uiState.value.copy(isLoading = false, pseudo = null)
                _eventFlow.emit(ProfileUiEvent.PseudoDeleted)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _eventFlow.emit(ProfileUiEvent.ShowError("Failed to delete pseudo"))
            }
        }
    }
}

data class ProfileUiState(
    val pseudo: String? = null, val isLoading: Boolean = false
)

sealed class ProfileUiEvent {
    object PseudoDeleted : ProfileUiEvent()
    data class ShowError(val message: String) : ProfileUiEvent()
}