package com.fchps.data.datasource.local

import com.fchps.domain.model.Feed

interface FeedLocalDataSource {
    suspend fun fetchFeed(): Result<List<Feed>>
    suspend fun storeFeed(feeds: List<Feed>): Result<Unit>
    suspend fun addNewFeed(feed: Feed): Result<Unit>
}