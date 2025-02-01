package com.fchps.domain.usecase

import com.fchps.domain.model.Feed
import com.fchps.domain.repository.FeedRepository

class GetFeedUseCase(
    private val repository: FeedRepository
) {
    suspend operator fun invoke(): Result<List<Feed>> = repository.getFeed()
}