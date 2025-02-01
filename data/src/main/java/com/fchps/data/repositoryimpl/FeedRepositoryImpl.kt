package com.fchps.data.repositoryimpl

import com.fchps.data.datasource.local.FeedLocalDataSource
import com.fchps.data.datasource.remote.FeedRemoteDataSource
import com.fchps.data.mapper.toFeed
import com.fchps.domain.model.Feed
import com.fchps.domain.repository.FeedRepository

class FeedRepositoryImpl(
    private val remoteDataSource: FeedRemoteDataSource,
    private val localDataSource: FeedLocalDataSource
) : FeedRepository {

    override suspend fun getFeed(): Result<List<Feed>> {
        return try {
            // Attempt to fetch from local
            val localResult = localDataSource.fetchFeed().getOrNull().orEmpty()

            // Attempt to fetch from remote
            val response = remoteDataSource.fetchFeed()
            val remoteFeeds = response.feeds.map { it.toFeed() }

            // Merge local and remote feeds
            val mergedFeeds = (localResult + remoteFeeds).distinctBy { it.id }

            // Store all data locally
            localDataSource.storeFeed(mergedFeeds)

            // Return merged list
            Result.success(mergedFeeds)
        } catch (exception: Exception) {
            // If remote fetch fails, attempt to fetch from local
            val localResult = localDataSource.fetchFeed()
            localResult.onSuccess {
                Result.success(localResult.getOrNull() ?: emptyList())
            }.onFailure {
                Result.failure<Exception>(exception)
            }
        }
    }

    override suspend fun addNewPost(feed: Feed): Result<Unit> {
        return localDataSource.addNewFeed(feed)
    }
}