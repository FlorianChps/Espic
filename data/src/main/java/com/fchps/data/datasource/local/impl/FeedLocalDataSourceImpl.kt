package com.fchps.data.datasource.local.impl

import android.content.Context
import com.fchps.data.datasource.local.FeedLocalDataSource
import com.fchps.data.dto.FeedsResponse
import com.fchps.data.mapper.toFeed
import com.fchps.data.mapper.toFeedResponse
import com.fchps.domain.model.Feed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class FeedLocalDataSourceImpl(
    private val context: Context
) : FeedLocalDataSource {

    private val fileName = "feeds.json"
    private val json = Json { prettyPrint = true }

    override suspend fun fetchFeed(): Result<List<Feed>> {
        return try {
            val jsonString = withContext(Dispatchers.IO) {
                context.openFileInput(fileName).bufferedReader().use { it.readText() }
            }
            val feeds = json.decodeFromString<FeedsResponse>(jsonString)
            val feedList = feeds.feeds.map { it.toFeed() }
            Result.success(feedList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun storeFeed(feeds: List<Feed>): Result<Unit> {
        return try {
            val feedsResponse = FeedsResponse(feeds.map { it.toFeedResponse() })
            val jsonString = json.encodeToString(feedsResponse)
            withContext(Dispatchers.IO) {
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(jsonString.toByteArray())
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addNewFeed(feed: Feed): Result<Unit> {
        return try {
            // Fetch current feeds
            val currentFeeds = fetchFeed().getOrDefault(null)
            // Add the new feed
            currentFeeds?.let {
                val updatedFeeds: List<Feed> = currentFeeds.plus(feed)
                storeFeed(updatedFeeds)
                Result.success(Unit)
            } ?: throw Exception(Throwable("Erreur lors de l'ajout d'un feed"))
            // Store the updated feeds
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}