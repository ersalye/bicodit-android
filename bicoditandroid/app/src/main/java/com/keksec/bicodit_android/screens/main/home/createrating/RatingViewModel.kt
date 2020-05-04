package com.keksec.bicodit_android.screens.main.home.createrating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.keksec.bicodit_android.core.data.local.room.dao.rating.RatingDao
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.models.rating.RatingData
import com.keksec.bicodit_android.core.data.remote.api.RatingApiService
import com.keksec.bicodit_android.core.data.remote.model.Event
import com.keksec.bicodit_android.core.data.repository.RatingRepository
import com.keksec.bicodit_android.screens.helpers.Validators
import javax.inject.Inject

class RatingViewModel @Inject constructor(
    userDao: UserDao,
    ratingDao: RatingDao,
    ratingApiService: RatingApiService
) : ViewModel() {

    private val _aboutValidationState = MutableLiveData<AboutValidationState>()
    val aboutValidationState: LiveData<AboutValidationState> = _aboutValidationState
    private val _ratingValueState = MutableLiveData<RatingValueState>(RatingValueState(3))
    val ratingValueState: LiveData<RatingValueState> = _ratingValueState
    private val _ratingLiveData = MutableLiveData<Event<RatingData>>()
    val ratingLiveData: LiveData<Event<RatingData>> = _ratingLiveData

    private val ratingRepository: RatingRepository =
        RatingRepository(
            userDao,
            ratingDao,
            ratingApiService
        )

    suspend fun createRating(about: String, ratingValue: Int) {
        if (Validators.isAboutValid(about) == 0) {
            aboutChanged(about)
            ratingRepository.createRating(_ratingLiveData, about, ratingValue)
        } else {
            aboutChanged(about)
        }
    }

    fun aboutChanged(about: String?) {
        val aboutValidationError: Int? = Validators.isAboutValid(about)
        aboutValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _aboutValidationState.postValue(AboutValidationState(aboutError = errorMessage))
            } else {
                _aboutValidationState.postValue(AboutValidationState(aboutError = null))
            }
        }
    }

    fun ratingValueChanged(ratingValue: Int) {
        _ratingValueState.postValue(RatingValueState(ratingValue))
    }
}