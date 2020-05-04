package com.keksec.bicodit_android.helpers

import com.keksec.bicodit_android.core.data.local.room.models.rating.RatingData
import org.threeten.bp.OffsetDateTime
import java.util.*
import kotlin.math.floor

class RatingTestHelper {
    companion object {
        fun createRating(): RatingData {
            return RatingData(generateId(),
                UserTestHelper.generateId(),
                UserTestHelper.generateLogin(),
                "rabbodefault",
                generateText(),
                generateValue(),
                OffsetDateTime.now())
        }

        fun generateText(): String {
            val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 _"
            val salt = StringBuilder()
            val rnd = Random()
            while (salt.length < 100) { // length of the random string.
                val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
                salt.append(SALTCHARS[index])
            }
            return salt.toString()
        }

        fun generateValue(): Int {
            return floor(Math.random() * 4 + 1).toInt()
        }

        private fun generateId(): String {
            return UUID.randomUUID().toString();
        }
    }
}