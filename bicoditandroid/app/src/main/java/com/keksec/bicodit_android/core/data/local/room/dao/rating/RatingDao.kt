package com.keksec.bicodit_android.core.data.local.room.dao.rating

import androidx.room.*
import com.keksec.bicodit_android.core.data.local.room.models.rating.RatingData
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

/**
 * This dao performs database operations related to ratings
 */
@Dao
interface RatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ratingData: RatingData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(ratingData: List<RatingData>)

    @Delete
    fun delete(ratingData: RatingData)

    @Update
    fun update(ratingData: RatingData): Int

    @Transaction
    @Query("DELETE FROM RatingData")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsert(ratingData: RatingData) {
        Timber.i("INSERTING DATA")
        deleteAll()
        insert(ratingData)
    }

    @Transaction
    @Query("DELETE FROM RatingData WHERE id = :id")
    fun deleteById(id: String)

    @Transaction
    @Query("SELECT * FROM RatingData WHERE id = :id")
    fun getRatingById(id: String): RatingData

    @Transaction
    @Query("SELECT * FROM RatingData WHERE userId = :userId ORDER BY datetime(createdTime) DESC")
    fun getAllInList(userId: String): List<RatingData>
}

