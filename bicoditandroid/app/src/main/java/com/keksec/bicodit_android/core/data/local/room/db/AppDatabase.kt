package com.keksec.bicodit_android.core.data.local.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.dao.utils.StringKeyValueDao
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import com.keksec.bicodit_android.core.data.local.room.models.utils.StringKeyValuePair

/**
 * Initialising app database
 */
@Database(
    entities = [UserData::class, StringKeyValuePair::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun keyValueDao(): StringKeyValueDao
}