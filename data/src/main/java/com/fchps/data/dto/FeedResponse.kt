package com.fchps.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class FeedsResponse(
    @SerialName("feeds")
    val feeds: List<FeedResponse>
)

@Serializable
data class FeedResponse(
    @SerialName("author")
    val author: String,
    @SerialName("filter")
    val filter: String,
    @SerialName("id")
    val id: Int,
    @SerialName("likes")
    val likes: Int,
    @SerialName("type")
    val type: String,
    @SerialName("uri")
    val uri: String,
    @SerialName("timestamp")
    val timeStamp: Long?
)


