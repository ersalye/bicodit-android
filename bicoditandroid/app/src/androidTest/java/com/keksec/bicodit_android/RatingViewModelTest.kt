package com.keksec.bicodit_android

import android.os.Handler
import android.os.Looper
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.keksec.bicodit_android.constants.*
import com.keksec.bicodit_android.core.data.helpers.RequestHelpers
import com.keksec.bicodit_android.core.data.local.room.dao.rating.RatingDao
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.db.AppDatabase
import com.keksec.bicodit_android.core.data.local.room.helpers.converters.Converters
import com.keksec.bicodit_android.core.data.remote.api.RatingApiService
import com.keksec.bicodit_android.core.data.remote.model.Error
import com.keksec.bicodit_android.core.data.remote.model.Status
import com.keksec.bicodit_android.mock.MockServer
import com.keksec.bicodit_android.screens.main.home.createrating.RatingViewModel
import junit.framework.Assert.fail
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RatingViewModelTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var ratingDao: RatingDao
    private lateinit var mockServer: MockServer

    private lateinit var ratingViewModel: RatingViewModel

    /**
     * Initialising database and dao
     */
    @Before
    @Throws(Exception::class)
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        )
            .build()
        userDao = db.userDao()
        ratingDao = db.ratingDao()
    }

    /**
     * Creating and starting a mock server to respond to test requests
     */
    @Before
    fun createAndStartMockServer() {
        mockServer = MockServer()
        mockServer.start()
    }

    /**
     * Creating and initialising ratingViewModel
     */
    @Before
    fun createRatingViewModel() {
        val ratingApiService = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(mockServer.mockWebServer.url("/"))
            .build()
            .create(RatingApiService::class.java)

        ratingViewModel = RatingViewModel(userDao, ratingDao, ratingApiService)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        db.close()
    }

    @After
    fun shutdown() {
        mockServer.shutdown()
    }

    @Test
    fun whenServerRespondsWithStatus400ThenSetCorrectErrorMessage() {
        mockServer.testedStatus = 400
        checkThatRepositorySetsCorrectErrorMessage(400)
    }

    @Test
    fun whenServerRespondsWithStatus404ThenSetCorrectErrorMessage() {
        mockServer.testedStatus = 404
        checkThatRepositorySetsCorrectErrorMessage(404)
    }

    @Test
    fun whenServerRespondsWithExceptionStatusThenSetCorrectErrorMessage() {
        mockServer.testedStatus = -1
        checkThatRepositorySetsCorrectErrorMessage(-1)
    }

    @Test
    fun whenServerRespondsWithSuccessStatusThenResponseRatingIsSavedToDB() {
        mockServer.testedStatus = 200
        runBlocking {
            ratingViewModel.createRating("test", 3)
            assertThat(ratingDao.getAllInList(TEST_USER_ID).size).isEqualTo(1)
            val rating = ratingDao.getRatingById(TEST_RATING_ID)
            assertThat(rating.id).isEqualTo(TEST_RATING_ID)
            assertThat(rating.userId).isEqualTo(TEST_USER_ID)
            assertThat(rating.userLogin).isEqualTo(TEST_USER_LOGIN)
            assertThat(rating.userAvatar).isEqualTo(TEST_USER_AVATAR)
            assertThat(rating.text).isEqualTo(TEST_RATING_TEXT)
            assertThat(rating.value).isEqualTo(TEST_RATING_VALUE.toInt())
            val expectedTestTimeUTC = Converters.toOffsetDateTime(TEST_CREATED_TIME)
            assertThat(Converters.fromOffsetDateTime(rating.createdTime))
                .isEqualTo(Converters.fromOffsetDateTime(expectedTestTimeUTC))
        }
    }

    @Test
    fun whenServerRespondsWithSuccessStatusThenRatingLiveDataValueIsSetToSuccessEvent() {
        mockServer.testedStatus = 200
        Handler(Looper.getMainLooper()).post {
            ratingViewModel.ratingLiveData.observeForever {
                when (it.status) {
                    Status.ERROR -> {
                        fail("Server responded with success code so it should be success event")
                    }
                    Status.SUCCESS -> {
                        assertThat(it.error).isEqualTo(null)
                        assertThat(it.data).isEqualTo(null)
                    }
                }
            }
        }
        runBlocking {
            ratingViewModel.createRating("test", 3)
        }
    }

    @Test
    fun whenInvalidRatingTextProvidedThenSetRatingTextValidationErrorToValidatorMessage() {
        val a = StringBuilder("")
        for (i in 0..300) {
            a.append("c")
        }
        Handler(Looper.getMainLooper()).post {
            ratingViewModel.aboutValidationState.observeForever {
                if (it.aboutError != null) {
                    assertThat(it.aboutError).isEqualTo(R.string.invalid_about)
                }
            }
        }
        runBlocking {
            ratingViewModel.createRating(a.toString(), 3)
        }
    }

    @Test
    fun whenValidRatingTextProvidedThenSetRatingTextValidationErrorToNull() {
        val a = StringBuilder("")
        for (i in 0..299) {
            a.append("c")
        }
        Handler(Looper.getMainLooper()).post {
            ratingViewModel.aboutValidationState.observeForever {
                assertThat(it.aboutError).isNull()
            }
        }
        runBlocking {
            ratingViewModel.createRating(a.toString(), 3)
        }
    }

    @Test
    fun whenRatingValueChangesThenRatingLiveDataValueUpdates() {
        val expectedRatingValue = 3
        Handler(Looper.getMainLooper()).post {
            ratingViewModel.ratingValueState.observeForever {
                assertThat(it.ratingValue).isEqualTo(expectedRatingValue)
            }
        }
        runBlocking {
            ratingViewModel.createRating("test", 3)
        }
    }

    private fun checkThatRepositorySetsCorrectErrorMessage(testedStatus: Int) {
        Handler(Looper.getMainLooper()).post {
            ratingViewModel.ratingLiveData.observeForever {
                when (it.status) {
                    Status.ERROR -> {
                        assertThat(it.error).isEqualTo(
                            Error(
                                RequestHelpers.getRatingErrorMessage(
                                    testedStatus
                                )
                            )
                        )
                    }
                }
            }
        }
        runBlocking {
            ratingViewModel.createRating("test", 3)
        }
    }
}

