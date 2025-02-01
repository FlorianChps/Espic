package com.fchps.data.datasource.remote

import com.fchps.data.dto.FeedsResponse

interface FeedRemoteDataSource {
    suspend fun fetchFeed(): FeedsResponse
}