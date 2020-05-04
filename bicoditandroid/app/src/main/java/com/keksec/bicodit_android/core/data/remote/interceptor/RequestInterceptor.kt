package com.keksec.bicodit_android.core.data.remote.interceptor

import com.keksec.bicodit_android.AppController
import com.keksec.bicodit_android.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This interceptor adds api key and authorization token to the request
 */
class RequestInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val url = originalUrl.newBuilder()
            .addQueryParameter("api_key", BuildConfig.BICODIT_API_KEY)
            .build()

        var token = AppController.prefs.accessToken
        if (token == null) {
            token = ""
        }
        val requestBuilder = originalRequest.newBuilder().addHeader("token", token).url(url)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

