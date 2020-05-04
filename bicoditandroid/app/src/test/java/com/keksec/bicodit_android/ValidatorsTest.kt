package com.keksec.bicodit_android

import com.google.common.truth.Truth.assertThat
import com.keksec.bicodit_android.screens.helpers.Validators
import org.junit.Test
import java.lang.StringBuilder

class ValidatorsTest {
    private val EMAIL_ERROR_RESOURCE = R.string.invalid_email
    private val INVALID_LOGIN_CHARS_ERROR_RESOURCE = R.string.invalid_login_characters
    private val INVALID_LOGIN_LENGTH_ERROR_RESOURCE = R.string.invalid_login_length
    private val INVALID_PASSWORD_CHARS_ERROR_RESOURCE = R.string.invalid_password_characters
    private val INVALID_PASSWORD_LENGTH_ERROR_RESOURCE = R.string.invalid_password_length
    private val INVALID_RATING_TEXT_LENGTH_ERROR_RESOURCE = R.string.invalid_about

    @Test
    fun emailValidator_CorrectEmailSimple_ReturnsZero() {
        val result: Int? = Validators.isEmailValid("darth_john217@mail.com")
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun emailValidator_InvalidEmailLong_ReturnsErrorCode() {
        val longEmail = StringBuilder("")
        for (i in 0..400) {
            longEmail.append("a")
        }
        longEmail.append("@mail.com")
        val result: Int? = Validators.isEmailValid(longEmail.toString())
        assertThat(result).isEqualTo(EMAIL_ERROR_RESOURCE)
    }

    @Test
    fun emailValidator_InvalidEmailEmpty_ReturnsErrorCode() {
        val result: Int? = Validators.isEmailValid("")
        assertThat(result).isEqualTo(EMAIL_ERROR_RESOURCE)
    }

    @Test
    fun emailValidator_InvalidEmailChars_ReturnsErrorCode() {
        val result: Int? = Validators.isEmailValid("darth_john217")
        assertThat(result).isEqualTo(EMAIL_ERROR_RESOURCE)
    }

    @Test
    fun loginValidator_CorrectLogin_ReturnsZero() {
        val result: Int? = Validators.isLoginValid("darth_john217")
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun loginValidator_InvalidLoginLength_ReturnsErrorCode() {
        val shortLogin = "darth"
        val longLogin = StringBuilder("")
        for (i in 0..41) {
            longLogin.append("a")
        }
        assertThat(Validators.isLoginValid(shortLogin)).isEqualTo(INVALID_LOGIN_LENGTH_ERROR_RESOURCE)
        assertThat(Validators.isLoginValid(longLogin.toString())).isEqualTo(INVALID_LOGIN_LENGTH_ERROR_RESOURCE)
    }

    @Test
    fun loginValidator_InvalidLoginChars_ReturnsErrorCode() {
        val result: Int? = Validators.isLoginValid("дarth joн 217")
        assertThat(result).isEqualTo(INVALID_LOGIN_CHARS_ERROR_RESOURCE)
    }

    @Test
    fun passwordValidator_CorrectPassword_ReturnsZero() {
        val result: Int? = Validators.isPasswordValid("357DarthJohn375")
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun passwordValidator_InvalidPasswordLength_ReturnsErrorCode() {
        val shortPassword = "Da123"
        val longPassword = StringBuilder("")
        for (i in 0..10) {
            longPassword.append("a").append("1").append("A")
        }
        longPassword.append("a")
        assertThat(Validators.isPasswordValid(shortPassword)).isEqualTo(INVALID_PASSWORD_LENGTH_ERROR_RESOURCE)
        assertThat(Validators.isPasswordValid(longPassword.toString())).isEqualTo(INVALID_PASSWORD_LENGTH_ERROR_RESOURCE)
    }

    @Test
    fun passwordValidator_InvalidPasswordChars_ReturnsErrorCode() {
        val noNumberResult = Validators.isPasswordValid("darth_john")
        val noLatinLetterResult = Validators.isPasswordValid("дарт_джон123")
        assertThat(noNumberResult).isEqualTo(INVALID_PASSWORD_CHARS_ERROR_RESOURCE)
        assertThat(noLatinLetterResult).isEqualTo(INVALID_PASSWORD_CHARS_ERROR_RESOURCE)
    }

    @Test
    fun ratingTextValidator_CorrectText_ReturnsZero() {
        val result: Int? = Validators.isAboutValid("Today I feel awesome!")
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun ratingTextValidator_InvalidText_ReturnsErrorCode() {
        val a = StringBuilder()
        for (i in 0..300) {
            a.append("a")
        }
        val tooBigResult = Validators.isAboutValid(a.toString())
        assertThat(tooBigResult).isEqualTo(INVALID_RATING_TEXT_LENGTH_ERROR_RESOURCE)
    }
}

