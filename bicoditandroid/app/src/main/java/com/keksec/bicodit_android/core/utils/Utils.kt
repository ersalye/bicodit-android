package com.keksec.bicodit_android.core.utils
import java.net.URL

/**
 * This class holds configuration constants
 */
object Utils {
    const val DATABASE_NAME = "user-database"
    // This is the url of the server, this address is temporary and will be changed in future versions
    // when app server will be released and deployed
    val BASE_URL = URL("http://192.168.0.37:5000")
    const val URL_REGISTER = "/bicoditapi/account/register"
    const val URL_LOGIN = "/bicoditapi/account/login"
    const val URL_CREATE_RATING = "/bicoditapi/rating/add"
    const val URL_GET_RATINGS = "/bicoditapi/rating/getall"
    const val URL_GET_ACCOUNT = "/bicoditapi/account/me"
}

