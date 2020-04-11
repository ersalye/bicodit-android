package com.keksec.bicodit_android.screens.authentication.login

/**
 * Data validation state of the login form.
 */
data class LoginValidationState(
    val loginError: Int? = null,
    val passwordError: Int? = null
)

