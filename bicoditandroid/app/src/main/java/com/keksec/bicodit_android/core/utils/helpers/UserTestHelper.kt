package com.keksec.bicodit_android.core.utils.helpers

import com.keksec.bicodit_android.core.data.local.room.models.user.UserData
import java.util.*

/**
 * This class holds test functions
 */
class UserTestHelper {
    companion object {
        fun createUser(): UserData {
            return UserData(
                email = generateEmail(),
                login = generateLogin(),
                profileName = "",
                profileInfo = "",
                id = generateId()
            )
        }

        private fun generateEmail(): String {
            val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
            val salt = StringBuilder()
            val rnd = Random()
            while (salt.length < 10) { // length of the random string.
                val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
                salt.append(SALTCHARS[index])
            }
            return "$salt@keksec.com"
        }

        private fun generateLogin(): String {
            val SALTCHARS = "abcdefghijklmnopqrstuvwxyz1234567890_"
            val salt = StringBuilder()
            val rnd = Random()
            while (salt.length < 10) { // length of the random string.
                val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
                salt.append(SALTCHARS[index])
            }
            return salt.toString()
        }

        private fun generateId(): String {
            return UUID.randomUUID().toString();
        }
    }
}