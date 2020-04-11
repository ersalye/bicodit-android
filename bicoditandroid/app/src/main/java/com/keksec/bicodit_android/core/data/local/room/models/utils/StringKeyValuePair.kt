package com.keksec.bicodit_android.core.data.local.room.models.utils

import androidx.room.Entity

@Entity(primaryKeys = ["key"])
data class StringKeyValuePair(
    val key: String, val value: String
)

