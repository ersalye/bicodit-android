package com.keksec.bicodit_android.core.data.local.room.models.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

/**
 * This model holds user data
 */
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity
data class UserData(
        @PrimaryKey var id: String,
        var email: String,
        var login: String,
        var avatar: String = "rabbodefault",
        var profileName: String,
        var profileInfo: String
)

