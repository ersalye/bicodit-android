package com.keksec.bicodit_android.core.data.remote.api

import android.service.autofill.UserData
import com.keksec.bicodit_android.core.data.remote.model.request_content.*
import com.keksec.bicodit_android.core.utils.Utils
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * This service is used to make account requests
 */
interface UserApiService {
    @POST(Utils.URL_LOGIN)
    fun loginAsync(@Body loginBody: LoginBody): Deferred<Response<LoginResponse>>

    @POST(Utils.URL_REGISTER)
    fun registerAsync(@Body registrationBody: RegistrationBody): Deferred<Response<RegistrationResponse>>

    @GET(Utils.URL_GET_ACCOUNT)
    fun getAccountAsync(): Deferred<Response<AccountResponse>>
}

