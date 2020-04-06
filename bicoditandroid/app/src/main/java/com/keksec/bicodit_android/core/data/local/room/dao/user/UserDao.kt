package com.keksec.bicodit_android.core.data.local.room.dao.user

import androidx.room.*
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

/**
 * This dao performs database operations related to user accounts
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userData: UserData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(userData: List<UserData>)

    @Delete
    fun delete(userData: UserData)

    @Update
    fun update(userData: UserData): Int

    @Query("DELETE FROM UserData")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(userData: UserData) {
        Timber.i("INSERTING DATA")
        deleteAll()
        insert(userData)
    }

    @Transaction
    @Query("DELETE FROM UserData WHERE id = :id")
    fun deleteById(id: String)

    @Transaction
    @Query("SELECT * FROM UserData WHERE id = :id")
    fun getUserById(id: String): UserData

    @Transaction
    @Query("SELECT * FROM UserData")
    fun getAll(): List<UserData>

    /**
     * This might be used to observe DB changes
     */
    @Transaction
    @Query("SELECT * FROM UserData")
    fun getFlow(): Flow<UserData>
}

