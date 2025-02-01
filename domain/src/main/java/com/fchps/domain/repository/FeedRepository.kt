package com.fchps.domain.repository

import com.fchps.domain.model.Feed

interface FeedRepository {
    suspend fun getFeed(): Result<List<Feed>>
    suspend fun addNewPost(feed: Feed): Result<Unit>
}