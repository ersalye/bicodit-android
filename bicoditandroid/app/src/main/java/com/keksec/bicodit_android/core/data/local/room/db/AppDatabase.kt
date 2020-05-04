package com.keksec.bicodit_android.core.data.local.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.keksec.bicodit_android.core.data.local.room.dao.rating.RatingDao
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.dao.utils.StringKeyValueDao
import com.keksec.bicodit_android.core.data.local.room.helpers.converters.Converters
import com.keksec.bicodit_android.core.data.local.room.models.rating.RatingData
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import com.keksec.bicodit_android.core.data.local.room.models.utils.StringKeyValuePair

/**
 * Initialising app database
 */
@Database(
    entities = [UserData::class, RatingData::class, StringKeyValuePair::class], version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun ratingDao(): RatingDao
    abstract fun keyValueDao(): StringKeyValueDao
}

