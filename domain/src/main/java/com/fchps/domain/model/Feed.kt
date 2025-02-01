package com.fchps.domain.model

data class Feed(
    val author: String,
    val filter: String,
    val id: Int,
    val likes: Int,
    val type: String,
    val uri: String,
    val timeStamp: String
) : java.io.Serializable