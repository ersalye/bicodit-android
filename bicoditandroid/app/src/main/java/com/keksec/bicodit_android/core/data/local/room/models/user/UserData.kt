package com.keksec.bicodit_android.core.data.local.room.models.user

import androidx.room.*

/**
 * This model holds important user information
 */
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity
data class UserData(
        @PrimaryKey var id: String,
        var email: String,
        var login: String,
        var profileName: String,
        var profileInfo: String
)