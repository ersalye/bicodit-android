package com.keksec.bicodit_android.core.data.remote.api

import com.keksec.bicodit_android.core.data.remote.model.request_content.LoginBody
import com.keksec.bicodit_android.core.data.remote.model.request_content.LoginResponse
import com.keksec.bicodit_android.core.data.remote.model.request_content.RegistrationBody
import com.keksec.bicodit_android.core.data.remote.model.request_content.RegistrationResponse
import com.keksec.bicodit_android.core.utils.Utils
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * This service is used to make account requests
 */
interface UserApiService {
    @POST(Utils.URL_LOGIN)
    fun loginAsync(@Body loginBody: LoginBody): Deferred<Response<LoginResponse>>

    @POST(Utils.URL_REGISTER)
    fun registerAsync(@Body registrationBody: RegistrationBody): Deferred<Response<RegistrationResponse>>
}

