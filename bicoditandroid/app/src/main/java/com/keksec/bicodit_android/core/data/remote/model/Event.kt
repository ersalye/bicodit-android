package com.keksec.bicodit_android.core.data.remote.model

/**
 * This model holds an important to lifecycle event status and error
 */
data class Event<out T>(val status: Status, val data: T?, val error: Error?) {

    companion object {
        fun <T> loading(): Event<T> {
            return Event(Status.LOADING, null, null)
        }

        fun <T> success(data: T?): Event<T> {
            return Event(Status.SUCCESS, data, null)
        }

        fun <T> error(error: Error?): Event<T> {
            return Event(Status.ERROR, null, error)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

