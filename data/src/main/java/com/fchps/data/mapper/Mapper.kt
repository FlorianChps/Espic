package com.fchps.data.mapper

import com.fchps.data.dto.FeedResponse
import com.fchps.domain.model.Feed
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun FeedResponse.toFeed(): Feed {
    return Feed(
        author = author,
        filter = filter,
        id = id,
        likes = likes,
        type = type,
        uri = uri,
        timeStamp = mapTimeStampToString(timeStamp ?: 0L)
    )
}

fun Feed.toFeedResponse(): FeedResponse {
    return FeedResponse(
        author = author,
        filter = filter,
        id = id,
        likes = likes,
        type = type,
        uri = uri,
        timeStamp = mapStringToTimeStamp(timeStamp)
    )
}

const val DATE_FORMAT = "dd/MM/yyyy HH:mm"

fun mapStringToTimeStamp(dateString: String): Long {
    val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
    val localDate = LocalDateTime.parse(dateString, formatter)
    val zonedDateTime = localDate.atZone(ZoneId.systemDefault())

    return zonedDateTime.toInstant().toEpochMilli()
}

fun mapTimeStampToString(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val zonedDateTime = instant.atZone(ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
    return zonedDateTime.format(formatter)
}