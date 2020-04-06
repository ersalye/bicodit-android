package com.keksec.bicodit_android.core.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import com.keksec.bicodit_android.BuildConfig

/**
 * This interceptor adds api key to the request
 */
class RequestInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val url = originalUrl.newBuilder()
            .addQueryParameter("api_key", BuildConfig.BICODIT_API_KEY)
            .build()

        val requestBuilder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}