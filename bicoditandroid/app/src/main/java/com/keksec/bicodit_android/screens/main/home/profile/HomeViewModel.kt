package com.keksec.bicodit_android.screens.main.home.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keksec.bicodit_android.core.data.local.room.dao.rating.RatingDao
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.models.rating.RatingData
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import com.keksec.bicodit_android.core.data.remote.api.RatingApiService
import com.keksec.bicodit_android.core.data.remote.api.UserApiService
import com.keksec.bicodit_android.core.data.remote.model.Event
import com.keksec.bicodit_android.core.data.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class HomeViewModel @Inject constructor(userDao: UserDao, ratingDao: RatingDao, userApiService: UserApiService, ratingApiService: RatingApiService) : ViewModel() {
    private val _logoutMonitor = MutableLiveData<Event<Boolean>>()
    val logoutMonitor: LiveData<Event<Boolean>> = _logoutMonitor
    private val _ratingsLiveData = MutableLiveData<Event<List<RatingData>>>()
    val ratingsLiveData: LiveData<Event<List<RatingData>>> = _ratingsLiveData
    private val _userLiveData = MutableLiveData<Event<UserData>>()
    val userLiveData: LiveData<Event<UserData>> = _userLiveData

    private val homeRepository: HomeRepository =
        HomeRepository(
            userDao,
            ratingDao,
            userApiService,
            ratingApiService
        )

    suspend fun getUserData() {
        homeRepository.getUserData(_userLiveData)
    }

    suspend fun logoutUser() {
      homeRepository.logoutUser(_logoutMonitor)
    }

    suspend fun getAllUserRatings() {
        homeRepository.getAllUserRatings(_ratingsLiveData)
    }
}