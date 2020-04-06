package com.keksec.bicodit_android.screens.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import com.keksec.bicodit_android.core.data.remote.api.UserApiService
import com.keksec.bicodit_android.core.data.remote.model.Event
import com.keksec.bicodit_android.core.data.repository.LoginRepository
import com.keksec.bicodit_android.screens.authentication.helpers.Validators
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(userDao: UserDao, userApiService: UserApiService) :
    ViewModel() {
    private val _loginValidationState = MutableLiveData<LoginValidationState>()
    val loginValidationState: LiveData<LoginValidationState> = _loginValidationState
    private val _userLiveData = MutableLiveData<Event<UserData>>()
    val userLiveData: LiveData<Event<UserData>> = _userLiveData

    private val loginRepository: LoginRepository =
        LoginRepository(
            userDao,
            userApiService
        )

    fun loginUser(login: String, password: String) {
        if (Validators.isLoginValid(login) == 0
            && Validators.isPasswordValid(password) == 0
        ) {
            viewModelScope.launch(Dispatchers.IO) {
                loginRepository.loginUser(_userLiveData, login, password)
            }
        } else {
            loginChanged(login)
            passwordChanged(password)
        }
    }

    fun loginChanged(login: String?) {
        val loginValidationError: Int? = Validators.isLoginValid(login)
        loginValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _loginValidationState.value = LoginValidationState(
                    loginError = errorMessage,
                    passwordError = _loginValidationState.value?.passwordError
                )
            } else {
                _loginValidationState.value = LoginValidationState(
                    loginError = null,
                    passwordError = _loginValidationState.value?.passwordError
                )
            }
        }
    }

    fun passwordChanged(password: String?) {
        val passwordValidationError: Int? = Validators.isPasswordValid(password)
        passwordValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _loginValidationState.value = LoginValidationState(
                    loginError = _loginValidationState.value?.loginError,
                    passwordError = errorMessage
                )
            } else {
                _loginValidationState.value = LoginValidationState(
                    loginError = _loginValidationState.value?.loginError,
                    passwordError = null
                )
            }
        }
    }
}