package com.keksec.bicodit_android.helpers

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

        fun generateEmail(): String {
            val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
            val salt = StringBuilder()
            val rnd = Random()
            while (salt.length < 10) { // length of the random string.
                val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
                salt.append(SALTCHARS[index])
            }
            return "$salt@keksec.com"
        }

        fun generateLogin(): String {
            val SALTCHARS = "abcdefghijklmnopqrstuvwxyz1234567890_"
            val salt = StringBuilder()
            val rnd = Random()
            while (salt.length < 10) { // length of the random string.
                val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
                salt.append(SALTCHARS[index])
            }
            return salt.toString()
        }

        fun generateId(): String {
            return UUID.randomUUID().toString();
        }
    }
}

