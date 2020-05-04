package com.keksec.bicodit_android.core.data.helpers

import com.keksec.bicodit_android.R

object RequestHelpers {
    /**
     * This function is used to get error message for login operations
     *
     * @param responseStatus - server response status
     * @return an address of a resource with an error message
     */
    fun getLoginErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            400 -> R.string.bad_request
            403 -> R.string.bad_password
            404 -> R.string.user_not_found
            else -> R.string.failed_login
        }
    }

    /**
     * This function is used to get error message for registration operations
     *
     * @param responseStatus - server response status
     * @return an address of a resource with an error message
     */
    fun getRegistrationErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            400 -> R.string.bad_request
            403 -> R.string.user_exists
            else -> R.string.failed_registration
        }
    }

    /**
     * This function is used to get error message for home operations related to user data
     *
     * @param responseStatus - server response status
     * @return an address of a resource with an error message
     */
    fun getHomeUserDataRequestErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            400 -> R.string.bad_request
            404 -> R.string.user_not_found
            else -> R.string.no_user_data
        }
    }

    /**
     * This function is used to get error message for home operations related to ratings
     *
     * @param responseStatus - server response status
     * @return an address of a resource with an error message
     */
    fun getHomeRatingsRequestErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            400 -> R.string.bad_request
            404 -> R.string.user_not_found
            else -> R.string.failed_get_ratings
        }
    }

    /**
     * This function is used to get error message for rating operations
     *
     * @param responseStatus - server response status
     * @return an address of a resource with an error message
     */
    fun getRatingErrorMessage(responseStatus: Int): Int {
        return when (responseStatus) {
            400 -> R.string.bad_request
            404 -> R.string.user_not_found
            else -> R.string.create_rating_failed
        }
    }
}