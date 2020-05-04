package com.keksec.bicodit_android.core.data.remote.api

import com.keksec.bicodit_android.core.data.remote.model.request_content.RatingBody
import com.keksec.bicodit_android.core.data.remote.model.request_content.RatingListResponse
import com.keksec.bicodit_android.core.data.remote.model.request_content.RatingResponse
import com.keksec.bicodit_android.core.utils.Utils
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * This service is used to make rating requests
 */
interface RatingApiService {
    @POST(Utils.URL_CREATE_RATING)
    fun createRatingAsync(@Body ratingBody: RatingBody): Deferred<Response<RatingResponse>>

    @GET(Utils.URL_GET_RATINGS)
    fun getRatingsAsync(): Deferred<Response<RatingListResponse>>
}