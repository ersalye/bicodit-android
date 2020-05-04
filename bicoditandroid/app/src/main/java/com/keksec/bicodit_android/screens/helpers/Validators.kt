package com.keksec.bicodit_android.screens.helpers

import com.keksec.bicodit_android.R
import java.util.regex.Pattern.compile

class Validators {
    companion object {
        // Email validation check
        fun isEmailValid(email: String?): Int? {
            email?.let {
                if (!EMAIL_PATTERN.matcher(it).matches()) {
                    return R.string.invalid_email
                }
                return 0
            }
            return null
        }

        // Login validation check
        fun isLoginValid(login: String?): Int? {
            login?.let {
                if (!it.matches(Regex("[A-Za-z0-9_]+")))
                    return R.string.invalid_login_characters
                if (!it.matches(Regex("[A-Za-z0-9_]{6,40}")))
                    return R.string.invalid_login_length
                return 0
            }
            return null;
        }

        // Password validation check
        fun isPasswordValid(password: String?): Int? {
            password?.let {
                if (!it.matches(Regex("^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[0-9a-zA-Z]+\$"))) {
                    return R.string.invalid_password_characters
                }
                if (!it.matches(Regex("^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[0-9a-zA-Z]{6,30}\$"))) {
                    return R.string.invalid_password_length
                }
                return 0
            }
            return null
        }

        // About validation check
        fun isAboutValid(about: String?): Int? {
            about?.let {
                if (it.length > 300) {
                    return R.string.invalid_about
                }
                return 0
            }
            return null
        }

        private val EMAIL_PATTERN = compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
    }
}

