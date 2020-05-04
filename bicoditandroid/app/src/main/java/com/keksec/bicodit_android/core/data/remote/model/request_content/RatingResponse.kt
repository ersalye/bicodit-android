package com.keksec.bicodit_android.core.data.remote.model.request_content

/**
 * This model holds rating data from a response
 */
data class RatingResponse(
    val id: String,
    val userId: String,
    val userLogin: String,
    val avatar: String,
    val text: String,
    val value: Int,
    val createdTime: String)