package com.fchps.domain.usecase

import com.fchps.domain.model.Feed
import com.fchps.domain.repository.FeedRepository

class AddFeedUseCase(
    private val repository: FeedRepository
) {
    suspend operator fun invoke(feed: Feed): Result<Unit> = repository.addNewPost(feed)
}