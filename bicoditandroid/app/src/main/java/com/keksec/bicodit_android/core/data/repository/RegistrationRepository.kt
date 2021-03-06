package com.keksec.bicodit_android.core.data.repository

import androidx.lifecycle.MutableLiveData
import com.keksec.bicodit_android.AppController
import com.keksec.bicodit_android.core.data.helpers.RequestHelpers.getRegistrationErrorMessage
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import com.keksec.bicodit_android.core.data.remote.api.UserApiService
import com.keksec.bicodit_android.core.data.remote.model.Error
import com.keksec.bicodit_android.core.data.remote.model.Event
import com.keksec.bicodit_android.core.data.remote.model.request_content.RegistrationBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

/**
 * This class is responsible for authorization logic of signing up user
 */
@Singleton
class RegistrationRepository(
    private val userDao: UserDao,
    private val userApiService: UserApiService
) {

    /**
     * This method requests server with user data and if successful saves account data to the database
     * Finally, it sets up operation result and notifies the registration fragment of a change
     *
     * @param liveData is the lifecycle important live data to holds login operation result
     * @param email is the users's email
     * @param login is the user's login
     * @param password is the user's password
     */
    suspend fun registerUser(
        liveData: MutableLiveData<Event<UserData>>,
        email: String,
        login: String,
        password: String
    ) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = userApiService.registerAsync(
                    RegistrationBody(
                        email,
                        login,
                        password
                    )
                )

                val response = request.await()
                val responseStatus = response.code()
                val responseBody = response.body()

                if (responseStatus == 200 && responseBody != null) {
                    withContext(Dispatchers.Default) {
                        userDao.deleteAllAndInsert(responseBody.userData);
                    }
                    AppController.prefs.accessToken = responseBody.token
                    AppController.prefs.userId = responseBody.userData.id
                    liveData.postValue(Event.success(null))
                } else {
                    liveData.postValue(
                        Event.error(
                            Error(getRegistrationErrorMessage(responseStatus))
                        )
                    )
                }
            } catch (e: Exception) {
                liveData.postValue(
                    Event.error(
                        Error(getRegistrationErrorMessage(-1))
                    )
                )
            }
        }
    }
}

