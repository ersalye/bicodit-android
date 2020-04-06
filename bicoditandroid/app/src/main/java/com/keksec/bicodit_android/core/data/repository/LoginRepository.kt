package com.keksec.bicodit_android.core.data.repository

import androidx.lifecycle.MutableLiveData
import com.keksec.bicodit_android.AppController
import com.keksec.bicodit_android.R
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import com.keksec.bicodit_android.core.data.remote.api.UserApiService
import com.keksec.bicodit_android.core.data.remote.model.Error
import com.keksec.bicodit_android.core.data.remote.model.Event
import com.keksec.bicodit_android.core.data.remote.model.request_content.LoginBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

/**
 * This class is responsible for authorization logic of signing in user
 */
@Singleton
class LoginRepository(
        private val userDao: UserDao,
        private val userApiService: UserApiService
) {

    /**
     * This method requests server with user data and if successful saves account data to the database
     * Finally, it sets up operation result and notifies the login view model of a change
     *
     * @param liveData is the lifecycle important live data to holds login operation result
     * @param login is the user's login
     * @param password is the user's password
     */
    suspend fun loginUser(
            liveData: MutableLiveData<Event<UserData>>,
            login: String,
            password: String
    ) {
        liveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = userApiService.loginAsync(LoginBody(login, password))

                val response = request.await()
                val responseStatus = response.code()
                val responseBody = response.body()

                if (responseStatus == 200 && responseBody != null) {
                    userDao.deleteAllAndInsert(responseBody.userData)
                    AppController.prefs.accessToken = responseBody.token
                    liveData.postValue(Event.success(null))
                } else {
                    liveData.postValue(Event.error(Error(getErrorMessage(responseStatus))))
                }
            } catch (e: Exception) {
                liveData.postValue(Event.error(Error(getErrorMessage(-1)))
                )
            }
        }
    }

    /**
     * This function is used to get error message for login operations
     *
     * @param responseStatus - server response status
     * @return an address of a resource with an error message
     */
    fun getErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            400 -> R.string.bad_request
            403 -> R.string.bad_password
            404 -> R.string.user_not_found
            else -> R.string.failed_login
        }
    }
}