package com.keksec.bicodit_android.screens.authentication.registration

/**
 * Data validation state of the registration form.
 */
data class RegistrationValidationState(
    val emailError: Int? = null,
    val loginError: Int? = null,
    val passwordError: Int? = null
)