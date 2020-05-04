package com.keksec.bicodit_android.core.data.local.room.models.rating

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import org.threeten.bp.OffsetDateTime

/**
 * This model holds rating data
 */
@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity
data class RatingData(
        @PrimaryKey var id: String,
        var userId: String,
        var userLogin: String,
        var userAvatar: String,
        var text: String,
        var value: Int,
        val createdTime: OffsetDateTime
)

