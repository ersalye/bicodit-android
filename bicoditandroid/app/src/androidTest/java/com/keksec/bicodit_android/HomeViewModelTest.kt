package com.keksec.bicodit_android

import android.os.Handler
import android.os.Looper
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.keksec.bicodit_android.constants.*
import com.keksec.bicodit_android.core.data.helpers.RequestHelpers
import com.keksec.bicodit_android.core.data.local.room.dao.rating.RatingDao
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.db.AppDatabase
import com.keksec.bicodit_android.core.data.remote.api.RatingApiService
import com.keksec.bicodit_android.core.data.remote.api.UserApiService
import com.keksec.bicodit_android.core.data.remote.model.Error
import com.keksec.bicodit_android.core.data.remote.model.Status
import com.keksec.bicodit_android.mock.MockServer
import com.keksec.bicodit_android.screens.authentication.login.LoginViewModel
import com.keksec.bicodit_android.screens.main.home.profile.HomeViewModel
import junit.framework.Assert
import junit.framework.Assert.fail
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModelTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var ratingDao: RatingDao
    private lateinit var mockServer: MockServer

    private lateinit var homeViewModel: HomeViewModel

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
        val userApiService = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(mockServer.mockWebServer.url("/"))
            .build()
            .create(UserApiService::class.java)

        val ratingApiService = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(mockServer.mockWebServer.url("/"))
            .build()
            .create(RatingApiService::class.java)

        homeViewModel = HomeViewModel(userDao, ratingDao, userApiService, ratingApiService)
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
    fun whenServerRespondsToUserDataRequestWithStatus400ThenSetCorrectErrorMessage() {
        mockServer.testedStatus = 400
        checkThatRepositorySetsCorrectErrorMessageForUserDataRequest(400)
    }

    @Test
    fun whenServerRespondsToUserDataRequestWithStatus404ThenSetCorrectErrorMessage() {
        mockServer.testedStatus = 404
        checkThatRepositorySetsCorrectErrorMessageForUserDataRequest(404)
    }

    @Test
    fun whenServerRespondsToUserDataRequestWithExceptionStatusThenSetCorrectErrorMessage() {
        mockServer.testedStatus = -1
        checkThatRepositorySetsCorrectErrorMessageForUserDataRequest(-1)
    }

    @Test
    fun whenServerRespondsToRatingRequestWithStatus400ThenSetCorrectErrorMessage() {
        mockServer.testedStatus = 400
        checkThatRepositorySetsCorrectErrorMessageForRatingRequest(400)
    }

    @Test
    fun whenServerRespondsToRatingRequestWithStatus404ThenSetCorrectErrorMessage() {
        mockServer.testedStatus = 404
        checkThatRepositorySetsCorrectErrorMessageForRatingRequest(404)
    }

    @Test
    fun whenServerRespondsToRatingRequestWithExceptionStatusThenSetCorrectErrorMessage() {
        mockServer.testedStatus = -1
        checkThatRepositorySetsCorrectErrorMessageForRatingRequest(-1)
    }

    @Test
    fun whenSuccessfullyGetUserThenInsertOneToDb() {
        mockServer.testedStatus = 200
        runBlocking {
            homeViewModel.getUserData()
            val users = userDao.getAll()
            assertThat(users.size).isEqualTo(1)
            val user = userDao.getUserById(TEST_USER_ID)
            assertThat(user?.id).isEqualTo(TEST_USER_ID)
            assertThat(user?.login).isEqualTo(TEST_USER_LOGIN)
            assertThat(user?.avatar).isEqualTo(TEST_USER_AVATAR)
            assertThat(user?.profileName).isEqualTo(TEST_PROFILE_NAME)
            assertThat(user?.profileInfo).isEqualTo(TEST_PROFILE_INFO)
        }
    }

    @Test
    fun whenServerRespondsToUserDataRequestWithSuccessStatusThenUserLiveDataValueIsSetToSuccessEvent() {
        mockServer.testedStatus = 200
        Handler(Looper.getMainLooper()).post {
            homeViewModel.userLiveData.observeForever {
                when (it.status) {
                    Status.ERROR -> {
                        fail("Server responded with success code so it should be success event")
                    }
                    Status.SUCCESS -> {
                        assertThat(it.error).isEqualTo(null)
                        assertThat(it.data).isNotEqualTo(null)
                    }
                }
            }
        }
        runBlocking {
            homeViewModel.getUserData()
        }
    }

    @Test
    fun whenSuccessfullyGetRatingsThenInsertAllToDb() {
        mockServer.testedStatus = 200
        runBlocking {
            homeViewModel.getAllUserRatings()
            val ratings = ratingDao.getAllInList(TEST_USER_ID)
            assertThat(ratings.size).isEqualTo(6)
            ratings.forEach {
                assertThat(it.userId).isEqualTo(TEST_USER_ID)
            }
        }
    }

    @Test
    fun whenServerRespondsToRatingRequestWithSuccessStatusThenUserLiveDataValueIsSetToSuccessEvent() {
        mockServer.testedStatus = 200
        Handler(Looper.getMainLooper()).post {
            homeViewModel.ratingsLiveData.observeForever {
                when (it.status) {
                    Status.ERROR -> {
                        fail("Server responded with success code so it should be success event")
                    }
                    Status.SUCCESS -> {
                        assertThat(it.error).isEqualTo(null)
                        assertThat(it.data).isNotEqualTo(null)
                    }
                }
            }
        }
        runBlocking {
            homeViewModel.getAllUserRatings()
        }
    }

    private fun checkThatRepositorySetsCorrectErrorMessageForUserDataRequest(testedStatus: Int) {
        Handler(Looper.getMainLooper()).post {
            homeViewModel.userLiveData.observeForever {
                when (it.status) {
                    Status.ERROR -> {
                        assertThat(it.error)
                            .isEqualTo(
                                Error(
                                    RequestHelpers.getHomeUserDataRequestErrorMessage(
                                        testedStatus
                                    )
                                )
                            )
                    }
                }
            }
        }
        runBlocking {
            homeViewModel.getUserData()
        }
    }

    private fun checkThatRepositorySetsCorrectErrorMessageForRatingRequest(testedStatus: Int) {
        Handler(Looper.getMainLooper()).post {
            homeViewModel.ratingsLiveData.observeForever {
                when (it.status) {
                    Status.ERROR -> {
                        assertThat(it.error)
                            .isEqualTo(
                                Error(
                                    RequestHelpers.getHomeRatingsRequestErrorMessage(
                                        testedStatus
                                    )
                                )
                            )
                    }
                }
            }
        }
        runBlocking {
            homeViewModel.getUserData()
        }
    }
}