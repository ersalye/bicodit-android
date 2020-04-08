package com.keksec.bicodit_android

import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.db.AppDatabase
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import com.keksec.bicodit_android.core.data.remote.api.UserApiService
import com.keksec.bicodit_android.core.data.remote.model.Error
import com.keksec.bicodit_android.core.data.remote.model.Event
import com.keksec.bicodit_android.core.data.repository.LoginRepository
import com.keksec.bicodit_android.core.data.repository.RegistrationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
class AuthInstrumentedTest {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var mockWebServer: MockWebServer

    private lateinit var loginRepository: LoginRepository
    private lateinit var registrationRepository: RegistrationRepository

    /**
     * Initialising database and user dao
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
    }

    /**
     * Creating a mock server to respond to test requests
     * Creating an api service to request mock server
     * Initialising repositories with this service and dao
     */
    @Before
    fun createServiceAndRepository() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val dispatcher = getDispatcherForMockServer()
        mockWebServer.dispatcher = dispatcher

        val userApiService = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create(UserApiService::class.java)

        loginRepository = LoginRepository(userDao, userApiService)
        registrationRepository = RegistrationRepository(userDao, userApiService)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        db.close()
    }

    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun whenSuccessfullyLoginUserThenInsertOneToDb() {
        runBlocking {
            val userLiveData = MutableLiveData<Event<UserData>>()
            loginRepository.loginUser(userLiveData, "test-success", "test")
            val users = userDao.getAll()
            assertThat(users.size).isEqualTo(1)
            assertThat(users[0].login).isEqualTo("tester")
            assertThat(AppController.prefs.accessToken).isEqualTo("test-token")
            assertThat(userLiveData.value).isEqualTo(Event.success(null))
        }
    }

    @Test
    fun whenSuccessfullyRegisterUserThenInsertOneToDb() {
        runBlocking {
            val userLiveData = MutableLiveData<Event<UserData>>()
            registrationRepository.registerUser(userLiveData, "test-success", "test", "test")
            val users = userDao.getAll()
            assertThat(users.size).isEqualTo(1)
            assertThat(users[0].login).isEqualTo("tester")
            assertThat(AppController.prefs.accessToken).isEqualTo("test-token")
            assertThat(userLiveData.value).isEqualTo(Event.success(null))
        }
    }

    @Test
    fun whenServerRespondsToLoginRepoWithStatus400ThenSetCorrectErrorMessage() {
        testCorrectErrorMessageForTestedStatusInLoginRepo("test-status-400", 400)
    }

    @Test
    fun whenServerRespondsToRegistrationRepoWithStatus400ThenSetCorrectErrorMessage() {
        testCorrectErrorMessageForTestedStatusInRegistrationRepo("test-status-400", 400)
    }

    @Test
    fun whenServerRespondsToLoginRepoWithStatus403ThenSetCorrectErrorMessage() {
        testCorrectErrorMessageForTestedStatusInLoginRepo("test-status-403", 403)
    }

    @Test
    fun whenServerRespondsToRegistrationRepoWithStatus403ThenSetCorrectErrorMessage() {
        testCorrectErrorMessageForTestedStatusInRegistrationRepo("test-status-403", 403)
    }

    @Test
    fun whenServerRespondsToLoginRepoWithStatus404ThenSetCorrectErrorMessage() {
        testCorrectErrorMessageForTestedStatusInLoginRepo("test-status-404", 404)
    }

    @Test
    fun whenServerRespondsToLoginRepoWithExceptionThenSetCorrectErrorMessage() {
        testCorrectErrorMessageForTestedStatusInLoginRepo("test-exception", -1)
    }

    @Test
    fun whenServerRespondsToRegistrationRepoWithExceptionThenSetCorrectErrorMessage() {
        testCorrectErrorMessageForTestedStatusInRegistrationRepo("test-exception", -1)
    }

    private fun testCorrectErrorMessageForTestedStatusInLoginRepo(
        testedKey: String,
        testedStatus: Int
    ) {
        runBlocking {
            val userLiveData = MutableLiveData<Event<UserData>>()
            withContext(Dispatchers.Default) {
                loginRepository.loginUser(userLiveData, testedKey, "test")
            }
            val users = userDao.getAll()
            assertThat(users.size).isEqualTo(0)
            val expected = Event.error<Error>(
                Error(loginRepository.getErrorMessage(testedStatus))
            )
            assertThat(userLiveData.value).isEqualTo(expected)
        }
    }

    private fun testCorrectErrorMessageForTestedStatusInRegistrationRepo(
        testedKey: String,
        testedStatus: Int
    ) {
        runBlocking {
            val userLiveData = MutableLiveData<Event<UserData>>()
            withContext(Dispatchers.Default) {
                registrationRepository.registerUser(userLiveData, testedKey, "test", "test")
            }
            val users = userDao.getAll()
            assertThat(users.size).isEqualTo(0)
            val expected = Event.error<Error>(
                Error(registrationRepository.getErrorMessage(testedStatus))
            )
            assertThat(userLiveData.value).isEqualTo(expected)
        }
    }

    private fun getBody(): String {
        return "{'token':'test-token', 'userData':{'id':'test', 'email':'hello@keksec.com'," +
                " 'login':'tester', 'profileName':'testName', 'profileInfo':'testInfo'}}"
    }

    private fun extractLogin(reqBody: String): String {
        return reqBody.slice(reqBody.indexOf(":\"") + 2 until reqBody.indexOf("\","))
    }

    private fun getDispatcherForMockServer(): Dispatcher {
        return object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                val login: String = extractLogin(request.body.toString())
                when (request.path) {
                    "/bicoditapi/account/login" -> {
                        when (login) {
                            "test-success" -> return MockResponse()
                                .setResponseCode(200)
                                .setHeader("content-type", "application/json")
                                .setBody(getBody())
                            "test-status-400" -> return MockResponse()
                                .setResponseCode(400)
                            "test-status-403" -> return MockResponse()
                                .setResponseCode(403)
                            "test-status-404" -> return MockResponse()
                                .setResponseCode(404)
                            "test-exception" -> return MockResponse()
                                .setHttp2ErrorCode(0x7)
                        }
                    }
                    "/bicoditapi/account/register" -> {
                        when (login) {
                            "test-success" -> return MockResponse()
                                .setResponseCode(200)
                                .setHeader("content-type", "application/json")
                                .setBody(getBody())
                            "test-status-400" -> return MockResponse()
                                .setResponseCode(400)
                            "test-status-403" -> return MockResponse()
                                .setResponseCode(403)
                            "test-exception" -> return MockResponse()
                                .setHttp2ErrorCode(0x7)
                        }
                    }
                }
                return MockResponse().setResponseCode(404)
            }
        }
    }
}
