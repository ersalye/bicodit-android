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
import com.keksec.bicodit_android.core.data.remote.api.UserApiService
import com.keksec.bicodit_android.core.data.remote.model.Error
import com.keksec.bicodit_android.core.data.remote.model.Status
import com.keksec.bicodit_android.mock.MockServer
import com.keksec.bicodit_android.screens.authentication.registration.RegistrationViewModel
import junit.framework.Assert.fail
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegistrationViewModelTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var ratingDao: RatingDao
    private lateinit var mockServer: MockServer

    private lateinit var registrationViewModel: RegistrationViewModel

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

        registrationViewModel = RegistrationViewModel(userDao, userApiService)
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
    fun whenServerRespondsWithStatus403ThenSetCorrectErrorMessage() {
        mockServer.testedStatus = 403
        checkThatRepositorySetsCorrectErrorMessage(403)
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
    fun whenSuccessfullyLoginUserThenInsertOneToDbAndSetPrefs() {
        mockServer.testedStatus = 200
        runBlocking {
            registrationViewModel.registerUser("test@test.com","tester", "123456l")
            val users = userDao.getAll()
            assertThat(users.size).isEqualTo(1)
            val user = userDao.getUserById(TEST_USER_ID)
            assertThat(user?.id).isEqualTo(TEST_USER_ID)
            assertThat(user?.login).isEqualTo(TEST_USER_LOGIN)
            assertThat(user?.avatar).isEqualTo(TEST_USER_AVATAR)
            assertThat(user?.profileName).isEqualTo(TEST_PROFILE_NAME)
            assertThat(user?.profileInfo).isEqualTo(TEST_PROFILE_INFO)
            assertThat(AppController.prefs.accessToken).isEqualTo(TEST_TOKEN)
            assertThat(AppController.prefs.userId).isEqualTo(TEST_USER_ID)
        }
    }

    @Test
    fun whenServerRespondsWithSuccessStatusThenUserLiveDataValueIsSetToSuccessEvent() {
        mockServer.testedStatus = 200
        Handler(Looper.getMainLooper()).post {
            registrationViewModel.userLiveData.observeForever {
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
            registrationViewModel.registerUser("test@test.com","tester", "123456l")
        }
    }

    @Test
    fun whenInvalidEmailProvidedThenSetLoginValidationErrorToValidatorMessage() {
        Handler(Looper.getMainLooper()).post {
            registrationViewModel.registrationValidationState.observeForever {
                if (it.emailError != null) {
                    assertThat(it.emailError).isEqualTo(R.string.invalid_email)
                }
            }
        }
        runBlocking {
            registrationViewModel.registerUser("","tester", "123456l")
        }
    }

    @Test
    fun whenInvalidLoginProvidedThenSetLoginValidationErrorToValidatorMessage() {
        Handler(Looper.getMainLooper()).post {
            registrationViewModel.registrationValidationState.observeForever {
                if (it.loginError != null) {
                    assertThat(it.loginError)
                        .isEqualTo(R.string.invalid_login_characters)
                }
            }
        }
        runBlocking {
            registrationViewModel.registerUser("test@test.com","mister tester", "123456l")
        }
    }

    @Test
    fun whenInvalidPasswordProvidedThenSetPasswordValidationErrorToValidatorMessage() {
        Handler(Looper.getMainLooper()).post {
            registrationViewModel.registrationValidationState.observeForever {
                if (it.passwordError != null) {
                    assertThat(it.passwordError)
                        .isEqualTo(R.string.invalid_password_characters)
                }
            }
        }
        runBlocking {
            registrationViewModel.registerUser("test.com","tester", "123456")
        }
    }

    @Test
    fun whenValidEmailProvidedThenSetEmailValidationErrorToNull() {
        Handler(Looper.getMainLooper()).post {
            registrationViewModel.registrationValidationState.observeForever {
                assertThat(it.emailError).isNull()
            }
        }
        runBlocking {
            registrationViewModel.registerUser("test@test.com","tester", "123456l")
        }
    }

    @Test
    fun whenValidLoginProvidedThenSetLoginValidationErrorToNull() {
        Handler(Looper.getMainLooper()).post {
            registrationViewModel.registrationValidationState.observeForever {
                assertThat(it.loginError).isNull()
            }
        }
        runBlocking {
            registrationViewModel.registerUser("test@test.com","tester", "123456l")
        }
    }

    @Test
    fun whenValidPasswordProvidedThenSetPasswordValidationErrorToNull() {
        Handler(Looper.getMainLooper()).post {
            registrationViewModel.registrationValidationState.observeForever {
                assertThat(it.passwordError).isNull()
            }
        }
        runBlocking {
            registrationViewModel.registerUser("test@test.com","tester", "123456l")
        }
    }


    private fun checkThatRepositorySetsCorrectErrorMessage(testedStatus: Int) {
        Handler(Looper.getMainLooper()).post {
            registrationViewModel.userLiveData.observeForever {
                when (it.status) {
                    Status.ERROR -> {
                        Truth.assertThat(it.error)
                            .isEqualTo(Error(RequestHelpers.getRegistrationErrorMessage(testedStatus)))
                    }
                }
            }
        }
        runBlocking {
            registrationViewModel.registerUser("test@test.com","tester", "123456l")
        }
    }
}