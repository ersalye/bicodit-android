package com.keksec.bicodit_android

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.keksec.bicodit_android.constants.TEST_USER_ID
import com.keksec.bicodit_android.core.data.local.room.dao.rating.RatingDao
import com.keksec.bicodit_android.core.data.local.room.db.AppDatabase
import com.keksec.bicodit_android.helpers.RatingTestHelper
import org.junit.After
import org.junit.Before
import org.junit.Test

class RatingDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var ratingDao: RatingDao

    /**
     * Initialising database and rating dao
     */
    @Before
    @Throws(Exception::class)
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        )
            .build()
        ratingDao = db.ratingDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun whenInsertRatingThenGetByIdTheSameOne() {
        val rating = RatingTestHelper.createRating()
        ratingDao.insert(rating)
        val dbRating = ratingDao.getRatingById(rating.id)
        assertThat(dbRating).isEqualTo(rating)
    }

    @Test
    fun whenInsertRatingWithDuplicateIdThenReplaceOne() {
        val rating1 = RatingTestHelper.createRating()
        val rating2 = RatingTestHelper.createRating()
        rating2.id = rating1.id
        ratingDao.insert(rating1)
        ratingDao.insert(rating2)
        val dbRating = ratingDao.getRatingById(rating1.id)
        assertThat(dbRating).isEqualTo(rating2)
    }

    @Test
    fun whenInsertRatingThenDeleteSuccessfully() {
        val rating = RatingTestHelper.createRating()
        ratingDao.insert(rating)
        ratingDao.delete(rating)
        assertThat(ratingDao.getRatingById(rating.id)).isEqualTo(null)
    }

    @Test
    fun whenInsertRatingThenDeleteByIdSuccessfully() {
        val rating = RatingTestHelper.createRating()
        ratingDao.insert(rating)
        ratingDao.deleteById(rating.id)
        assertThat(ratingDao.getRatingById(rating.id)).isEqualTo(null)
    }

    @Test
    fun whenInsertMultipleRatingsThenGetAllSuccessfully() {
        val rating1 = RatingTestHelper.createRating()
        val rating2 = RatingTestHelper.createRating()
        val rating3 = RatingTestHelper.createRating()
        rating1.userId = TEST_USER_ID
        rating2.userId = TEST_USER_ID
        rating3.userId = TEST_USER_ID
        ratingDao.insertAll(listOf(rating1, rating2, rating3))
        val users = ratingDao.getAllInList(TEST_USER_ID)
        assertThat(users[0]).isEqualTo(rating1)
        assertThat(users[1]).isEqualTo(rating2)
        assertThat(users[2]).isEqualTo(rating3)
    }

    @Test
    fun whenInsertMultipleRatingsThenDeleteAllSuccessfully() {
        val rating1 = RatingTestHelper.createRating()
        val rating2 = RatingTestHelper.createRating()
        val rating3 = RatingTestHelper.createRating()
        rating1.userId = TEST_USER_ID
        rating2.userId = TEST_USER_ID
        rating3.userId = TEST_USER_ID
        ratingDao.insertAll(listOf(rating1, rating2, rating3))
        ratingDao.deleteAll()
        val ratings = ratingDao.getAllInList(TEST_USER_ID)
        assertThat(ratings).isEmpty()
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun whenUpdateUserThenReadTheSameOne() {
        val rating = RatingTestHelper.createRating()
        ratingDao.insert(rating)
        rating.text = "Hello world"
        ratingDao.update(rating)
        val dbRating = ratingDao.getRatingById(rating.id)
        assertThat(dbRating).isEqualTo(rating)
    }
}