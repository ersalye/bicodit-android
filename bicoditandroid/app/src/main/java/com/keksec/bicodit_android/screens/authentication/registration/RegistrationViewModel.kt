package com.keksec.bicodit_android.screens.authentication.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.keksec.bicodit_android.core.data.local.room.dao.user.UserDao
import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import com.keksec.bicodit_android.core.data.remote.api.UserApiService
import com.keksec.bicodit_android.core.data.remote.model.Event
import com.keksec.bicodit_android.core.data.repository.RegistrationRepository
import com.keksec.bicodit_android.screens.helpers.Validators
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(userDao: UserDao, userApiService: UserApiService) :
    ViewModel() {
    private val _registrationValidationState = MutableLiveData<RegistrationValidationState>()
    val registrationValidationState: LiveData<RegistrationValidationState> =
        _registrationValidationState
    private val _userLiveData = MutableLiveData<Event<UserData>>()
    val userLiveData: LiveData<Event<UserData>> = _userLiveData

    private val registrationRepository: RegistrationRepository =
        RegistrationRepository(
            userDao,
            userApiService
        )

    suspend fun registerUser(email: String, login: String, password: String) {
        if (Validators.isEmailValid(email) == 0
            && Validators.isLoginValid(login) == 0
            && Validators.isPasswordValid(password) == 0
        ) {
            registrationRepository.registerUser(_userLiveData, email, login, password)
        } else {
            emailChanged(email)
            loginChanged(login)
            passwordChanged(password)
        }
    }

    fun emailChanged(email: String?) {
        val registrationValidationError: Int? = Validators.isEmailValid(email)
        registrationValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _registrationValidationState.postValue(
                    RegistrationValidationState(
                        emailError = errorMessage,
                        loginError = _registrationValidationState.value?.loginError,
                        passwordError = _registrationValidationState.value?.passwordError
                    )
                )
            } else {
                _registrationValidationState.postValue(
                    RegistrationValidationState(
                        emailError = null,
                        loginError = _registrationValidationState.value?.loginError,
                        passwordError = _registrationValidationState.value?.passwordError
                    )
                )
            }
        }
    }

    fun loginChanged(login: String?) {
        val registrationValidationError: Int? = Validators.isLoginValid(login)
        registrationValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _registrationValidationState.postValue(
                    RegistrationValidationState(
                        emailError = _registrationValidationState.value?.emailError,
                        loginError = errorMessage,
                        passwordError = _registrationValidationState.value?.passwordError
                    )
                )
            } else {
                _registrationValidationState.postValue(
                    RegistrationValidationState(
                        emailError = _registrationValidationState.value?.emailError,
                        loginError = null,
                        passwordError = _registrationValidationState.value?.passwordError
                    )
                )
            }
        }
    }

    fun passwordChanged(password: String?) {
        val registrationValidationError: Int? = Validators.isPasswordValid(password)
        registrationValidationError?.let { errorMessage ->
            if (errorMessage != 0) {
                _registrationValidationState.postValue(
                    RegistrationValidationState(
                        emailError = _registrationValidationState.value?.emailError,
                        loginError = _registrationValidationState.value?.loginError,
                        passwordError = errorMessage
                    )
                )
            } else {
                _registrationValidationState.postValue(
                    RegistrationValidationState(
                        emailError = _registrationValidationState.value?.emailError,
                        loginError = _registrationValidationState.value?.loginError,
                        passwordError = null
                    )
                )
            }
        }
    }
}

