package com.keksec.bicodit_android.core.data.remote.model.request_content

import com.keksec.bicodit_android.core.data.local.room.models.user.UserData

/**
 * This model holds registration response data
 */
data class RegistrationResponse(
    val token: String,
    val userData: UserData
)