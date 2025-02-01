package com.fchps.espic.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fchps.domain.usecase.SetPseudoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val setPseudoUseCase: SetPseudoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<LoginUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onLoginClick(pseudo: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = setPseudoUseCase(pseudo)
                _uiState.value = _uiState.value.copy(isLoading = false)

                if (result.isSuccess) {
                    _eventFlow.emit(LoginUiEvent.RedirectToHome)
                } else {
                    _eventFlow.emit(LoginUiEvent.ShowError("Failed to store pseudo"))
                }
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _eventFlow.emit(LoginUiEvent.ShowError(exception.message ?: "Unknown error"))
            }
        }
    }
}

data class LoginUiState(
    val isLoading: Boolean = false
)

sealed class LoginUiEvent {
    object RedirectToHome : LoginUiEvent()
    data class ShowError(val message: String) : LoginUiEvent()
}