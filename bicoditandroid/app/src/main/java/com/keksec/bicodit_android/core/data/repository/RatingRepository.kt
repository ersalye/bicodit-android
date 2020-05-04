package com.keksec.bicodit_android.core.data.repository

import androidx.lifecycle.MutableLiveData
import com.keksec.bicodit_android.core.data.helpers.RequestHelpers.getRatingErrorMessage
import com.keksec.bicodit_android.core.data.local.room.dao.rating.RatingDao
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.helpers.converters.Converters
import com.keksec.bicodit_android.core.data.local.room.models.rating.RatingData
import com.keksec.bicodit_android.core.data.remote.api.RatingApiService
import com.keksec.bicodit_android.core.data.remote.model.Error
import com.keksec.bicodit_android.core.data.remote.model.Event
import com.keksec.bicodit_android.core.data.remote.model.request_content.RatingBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This class is responsible for logic of creating user ratings
 */
class RatingRepository(
    private val userDao: UserDao,
    private val ratingDao: RatingDao,
    private val ratingApiService: RatingApiService
) {
    /**
     * This method is responsible for creating a rating
     * Finally, it sets up operation result thus notifying the rating fragment of a change
     *
     * @param ratingLiveData is the lifecycle important live data that holds an operation of creating a rating result
     */
    suspend fun createRating(ratingLiveData: MutableLiveData<Event<RatingData>>, about: String, ratingValue: Int) {
        ratingLiveData.postValue(Event.loading())
        withContext(Dispatchers.IO) {
            try {
                val request = ratingApiService.createRatingAsync(RatingBody(about, ratingValue))
                val response = request.await()
                val responseStatus = response.code()
                val responseBody = response.body()
                if (responseStatus == 200 && responseBody != null) {
                    val ratingData = RatingData(
                        responseBody.id,
                        responseBody.userId,
                        responseBody.userLogin,
                        responseBody.avatar,
                        responseBody.text,
                        responseBody.value,
                        Converters.toOffsetDateTime(responseBody.createdTime)!!
                    )
                    ratingDao.insert(ratingData)
                    ratingLiveData.postValue(Event.success(null))
                } else {
                    ratingLiveData.postValue(Event.error(Error(getRatingErrorMessage(responseStatus))))
                }
            } catch (e: Exception) {
                ratingLiveData.postValue(Event.error(Error(getRatingErrorMessage(-1))))
            }
        }
    }
}