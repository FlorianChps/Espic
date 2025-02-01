package com.fchps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fchps.domain.usecase.GetPseudoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPseudoUseCase: GetPseudoUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<String?>(null)
    val uiState: StateFlow<String?> = _uiState.asStateFlow()

    init {
        getPseudo()
    }

    private fun getPseudo() {
        viewModelScope.launch {
            try {
                val pseudo = getPseudoUseCase()
                pseudo.onSuccess {
                    _uiState.value = it
                }.onFailure {
                    _uiState.value = null
                }
            } catch (exception: Exception) {
                _uiState.value = null

            }
        }
    }
}