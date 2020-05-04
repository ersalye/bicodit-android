package com.keksec.bicodit_android.mock

import com.keksec.bicodit_android.constants.*
import com.keksec.bicodit_android.core.data.local.room.models.rating.RatingData
import com.keksec.bicodit_android.helpers.RatingTestHelper
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.lang.StringBuilder

class MockServer {
    var testedStatus = -1
    val mockWebServer: MockWebServer = MockWebServer()

    init {
        val dispatcher = getDispatcherForMockServer()
        mockWebServer.dispatcher = dispatcher
    }

    fun start() {
        mockWebServer.start()
    }

    fun shutdown() {
        mockWebServer.shutdown()
    }

    private fun getMockRatingResponseBody(): String {
        return "{'id':'$TEST_RATING_ID', 'userId':'$TEST_USER_ID'," +
                " 'userLogin':'$TEST_USER_LOGIN', 'avatar':'$TEST_USER_AVATAR', 'text':'$TEST_RATING_TEXT', 'value':'$TEST_RATING_VALUE', 'createdTime':'$TEST_CREATED_TIME'}"
    }

    private fun getMockRatingListResponseBody(): String {
        val ratings: ArrayList<RatingData> = ArrayList()
        for (i in 0..5) {
            ratings.add(RatingTestHelper.createRating())
        }
        val s = ratings.joinToString(",") { rating ->
            "{'id':'${rating.id}', 'userId':'$TEST_USER_ID'," +
                    " 'userLogin':'$TEST_USER_LOGIN', 'avatar':'$TEST_USER_AVATAR'," +
                    " 'text':'${rating.text}', 'value':'${rating.value}', 'createdTime':'${rating.createdTime}'}"
        }
        return "{'ratings': [$s]}"
    }

    private fun getMockAuthResponseBody(): String {
        return "{'token':'$TEST_TOKEN', 'userData':{" +
                "'id':'$TEST_USER_ID', " +
                "'email':'$TEST_EMAIL', " +
                "'login':'$TEST_USER_LOGIN', " +
                "'avatar':'$TEST_USER_AVATAR', " +
                "'profileName':'$TEST_PROFILE_NAME', " +
                "'profileInfo':'$TEST_PROFILE_INFO'}" +
                "}"
    }

    private fun getMockAccountResponseBody(): String {
        return "{'userData':{" +
                "'id':'$TEST_USER_ID', " +
                "'email':'$TEST_EMAIL', " +
                "'login':'$TEST_USER_LOGIN', " +
                "'avatar':'$TEST_USER_AVATAR', " +
                "'profileName':'$TEST_PROFILE_NAME', " +
                "'profileInfo':'$TEST_PROFILE_INFO'}}"
    }

    private fun getGoodResponseWithBody(mockBody: String): MockResponse {
        return MockResponse()
            .setResponseCode(200)
            .setHeader("content-type", "application/json")
            .setBody(mockBody)
    }

    private fun getMockResponsesForFailedStatuses(): MockResponse {
        return when (testedStatus) {
            400 -> MockResponse()
                .setResponseCode(400)
            403 -> MockResponse()
                .setResponseCode(403)
            404 -> MockResponse()
                .setResponseCode(404)
            else -> MockResponse()
                .setHttp2ErrorCode(0x7)
        }
    }

    private fun getDispatcherForMockServer(): Dispatcher {
        return object : Dispatcher() {
            @Throws(InterruptedException::class)
            override fun dispatch(request: RecordedRequest): MockResponse {
                when (request.path) {
                    "/bicoditapi/rating/add" -> {
                        return when (testedStatus) {
                            200 -> {
                                val responseBody = getMockRatingResponseBody()
                                getGoodResponseWithBody(responseBody)
                            }
                            else -> getMockResponsesForFailedStatuses()
                        }
                    }
                    "/bicoditapi/rating/getall" -> {
                        return when (testedStatus) {
                            200 -> {
                                val responseBody = getMockRatingListResponseBody()
                                getGoodResponseWithBody(responseBody)
                            }
                            else -> getMockResponsesForFailedStatuses()
                        }
                    }
                    "/bicoditapi/account/login", "/bicoditapi/account/register" -> {
                        return when (testedStatus) {
                            200 -> {
                                val responseBody = getMockAuthResponseBody()
                                getGoodResponseWithBody(responseBody)
                            }
                            else -> getMockResponsesForFailedStatuses()
                        }
                    }
                    "/bicoditapi/account/me" -> {
                        return when (testedStatus) {
                            200 -> {
                                val responseBody = getMockAccountResponseBody()
                                getGoodResponseWithBody(responseBody)
                            }
                            else -> getMockResponsesForFailedStatuses()
                        }
                    }
                }
                return MockResponse().setResponseCode(404)
            }
        }
    }
}