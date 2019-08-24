package com.reactive.trach.beautyphotoapp.data

import java.lang.Exception

data class Response<T>(
        val status: Status,
        val data: T?,
        val error: Throwable?,
        val isFirst: Boolean = false
) {

    companion object {

        fun <T> loading(isFirst: Boolean) = Response<T>(Status.LOADING, null, null, isFirst)

        fun <T> refreshing() = Response<T>(Status.REFRESHING, null, null)

        fun <T> empty() = Response<T>(Status.EMPTY, null, null)

        fun <T> succeed(data: T, isFirst: Boolean) = Response(Status.SUCCEED, data, null, isFirst)

        fun <T> error(t: Throwable) = Response<T>(Status.FAILED, null, t)

        fun <T> networkLost() = Response<T>(Status.NO_CONNECTION, null, null)
    }
}

enum class Status {
    LOADING,
    REFRESHING,
    EMPTY,
    SUCCEED,
    FAILED,
    NO_CONNECTION
}

object NoNetworkException : Exception()
