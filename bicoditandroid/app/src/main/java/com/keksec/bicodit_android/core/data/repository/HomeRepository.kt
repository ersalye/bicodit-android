package com.keksec.bicodit_android.core.data.repository

import androidx.lifecycle.MutableLiveData
import com.keksec.bicodit_android.AppController
import com.keksec.bicodit_android.R
import com.keksec.bicodit_android.core.data.helpers.RequestHelpers
import com.keksec.bicodit_android.core.data.local.room.dao.rating.RatingDao
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.helpers.converters.Converters
import com.keksec.bicodit_android.core.data.local.room.models.rating.RatingData
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import com.keksec.bicodit_android.core.data.remote.api.RatingApiService
import com.keksec.bicodit_android.core.data.remote.api.UserApiService
import com.keksec.bicodit_android.core.data.remote.model.Error
import com.keksec.bicodit_android.core.data.remote.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

/**
 * This class is responsible for logic of logging out user and getting user ratings, profile data
 */
@Singleton
class HomeRepository(
    private val userDao: UserDao,
    private val ratingDao: RatingDao,
    private val userApiService: UserApiService,
    private val ratingApiService: RatingApiService
) {

    /**
     * This method is responsible for logging out and clearing data of user locally
     * Finally, it sets up operation result thus notifying the home fragment of a change
     *
     * @param logoutMonitor is the lifecycle important live data that holds logging out operation result
     */
    suspend fun logoutUser(logoutMonitor: MutableLiveData<Event<Boolean>>) {
        logoutMonitor.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                userDao.deleteAll()
                ratingDao.deleteAll()
                AppController.prefs.accessToken = ""
                AppController.prefs.userId = ""
                logoutMonitor.postValue(Event.success(null))
            } catch (e: Exception) {
                logoutMonitor.postValue(Event.error(Error(R.string.logout_failed)))
            }
        }
    }

    /**
     * This method requests server with authorization token and if successful saves account data from a response to the database
     * Finally, it sets up operation result thus notifying the home view model of a change
     *
     * @param liveData is the lifecycle important live data to holds operation of getting user data result
     */
    suspend fun getUserData(liveData: MutableLiveData<Event<UserData>>) {
        liveData.postValue(Event.loading())
        val dbUser = userDao.getUserById(AppController.prefs.userId!!)
        if (dbUser == null) {
            withContext(Dispatchers.IO) {
                try {
                    val request = userApiService.getAccountAsync()

                    val response = request.await()
                    val responseStatus = response.code()
                    val responseBody = response.body()
                    if (responseStatus == 200 && responseBody != null) {
                        userDao.insert(responseBody.userData)
                        AppController.prefs.userId = responseBody.userData.id
                        liveData.postValue(Event.success(userDao.getUserById(AppController.prefs.userId!!)))
                    } else {
                        val errorMessage = RequestHelpers.getHomeUserDataRequestErrorMessage(responseStatus)
                        liveData.postValue(Event.error(Error(errorMessage)))
                    }
                } catch (e: Exception) {
                    val errorMessage = RequestHelpers.getHomeUserDataRequestErrorMessage(-1)
                    liveData.postValue(Event.error(Error(errorMessage)))
                }
            }
        } else {
            liveData.postValue(Event.success(dbUser))
        }
    }

    /**
     * This method requests server with authorization token and if successful saves all user rating from a response to the database
     * Finally, it sets up operation result thus notifying the home view model of a change
     *
     * @param liveData is the lifecycle important live data to holds operation of getting user ratings result
     */
    suspend fun getAllUserRatings(liveData: MutableLiveData<Event<List<RatingData>>>) {
        liveData.postValue(Event.loading())
        val userId = if (AppController.prefs.userId != null) AppController.prefs.userId else ""
        val dbRatings = ratingDao.getAllInList(userId!!)
        if (dbRatings.isEmpty()) {
            withContext(Dispatchers.IO) {
                try {
                    val request = ratingApiService.getRatingsAsync()

                    val response = request.await()
                    val responseStatus = response.code()
                    val responseBody = response.body()

                    if (responseStatus == 200 && responseBody != null) {
                        if (responseBody.ratings.isNotEmpty()) {
                            val ratings = responseBody.ratings.map { ratingResponse ->
                                RatingData(
                                    ratingResponse.id,
                                    ratingResponse.userId,
                                    ratingResponse.userLogin,
                                    ratingResponse.avatar,
                                    ratingResponse.text,
                                    ratingResponse.value,
                                    Converters.toOffsetDateTime(ratingResponse.createdTime)!!
                                )
                            }
                            ratingDao.insertAll(ratings)
                        }
                        liveData.postValue(Event.success(ratingDao.getAllInList(AppController.prefs.userId!!)))
                    } else {
                        liveData.postValue(Event.error(Error(R.string.user_not_found)))
                    }
                } catch (e: Exception) {
                    liveData.postValue(Event.error(Error(R.string.failed_get_ratings)))
                }
            }
        } else {
            liveData.postValue(Event.success(dbRatings))
        }
    }
}

