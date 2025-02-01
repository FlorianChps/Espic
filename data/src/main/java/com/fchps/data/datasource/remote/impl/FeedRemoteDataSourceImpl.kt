package com.fchps.data.datasource.remote.impl

import com.fchps.data.api.FakeEsportApi
import com.fchps.data.datasource.remote.FeedRemoteDataSource
import com.fchps.data.dto.FeedsResponse

class FeedRemoteDataSourceImpl(private val api: FakeEsportApi) : FeedRemoteDataSource {

    override suspend fun fetchFeed(): FeedsResponse = api.getFeed()
}