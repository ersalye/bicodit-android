package com.keksec.bicodit_android.core.data.remote.model.request_content

import com.keksec.bicodit_android.core.data.local.room.models.user.UserData

/**
 * This model holds account data from a response
 */
data class AccountResponse(val userData: UserData)