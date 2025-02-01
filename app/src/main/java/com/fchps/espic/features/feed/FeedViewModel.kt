package com.fchps.espic.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fchps.domain.model.Feed
import com.fchps.domain.usecase.AddFeedUseCase
import com.fchps.domain.usecase.GetFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val addFeedUseCase: AddFeedUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        getFeed()
    }

    fun getFeed() {
        viewModelScope.launch {
            getFeedUseCase()
                .onSuccess { feedList ->
                    _uiState.value = FeedUiState.Success(feedList.sortedByDescending {
                        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        LocalDateTime.parse(it.timeStamp, dateFormatter)
                    })
                }
                .onFailure {
                    _uiState.value = FeedUiState.Error()
                }
        }
    }

    fun addNewFeed(newFeed: Feed) {
        viewModelScope.launch {
            addFeedUseCase(newFeed)
                .onSuccess {
                    val updatedFeedList =
                        (_uiState.value as? FeedUiState.Success)?.feeds.orEmpty() + newFeed

                    _uiState.value =
                        FeedUiState.Success(updatedFeedList.sortedByDescending {
                            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                            LocalDateTime.parse(it.timeStamp, dateFormatter)
                        })
                }.onFailure {
                    _uiState.value = FeedUiState.Error()
                }
        }
    }
}

sealed class FeedUiState {
    object Loading : FeedUiState()
    data class Success(val feeds: List<Feed>) : FeedUiState()
    data class Error(val message: String = "Une erreur est survenue, veuillez réessayez.") :
        FeedUiState()
}