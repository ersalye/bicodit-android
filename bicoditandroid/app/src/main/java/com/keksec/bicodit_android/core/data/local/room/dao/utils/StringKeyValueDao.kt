package com.keksec.bicodit_android.core.data.local.room.dao.utils

import androidx.annotation.NonNull
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.keksec.bicodit_android.core.data.local.room.models.utils.StringKeyValuePair

/**
 * This dao performs database operations related to key value pairs
 */
@Dao
interface StringKeyValueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keyValueData: StringKeyValuePair)

    @Query("SELECT * FROM StringKeyValuePair WHERE `key` = :key LIMIT 1")
    suspend fun get(@NonNull key: String): StringKeyValuePair?

    @Query("DELETE FROM StringKeyValuePair WHERE `key` = :key")
    suspend fun delete(@NonNull key: String)

    @Query("DELETE FROM StringKeyValuePair")
    suspend fun clear()

    @Query("SELECT * FROM StringKeyValuePair")
    suspend fun getAll(): List<StringKeyValuePair>?
}

