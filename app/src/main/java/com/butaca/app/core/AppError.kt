package com.butaca.app.core

import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

enum class AppError {
    NO_CONNECTION,
    TIMEOUT,
    UNAUTHORIZED,
    NOT_FOUND,
    SERVER,
    UNKNOWN
}

fun Throwable.toAppError(): AppError = when (this) {
    is UnknownHostException, is ConnectException -> AppError.NO_CONNECTION
    is SocketTimeoutException -> AppError.TIMEOUT
    is HttpException -> when (code()) {
        401 -> AppError.UNAUTHORIZED
        404 -> AppError.NOT_FOUND
        else -> AppError.SERVER
    }
    is IOException -> AppError.NO_CONNECTION
    else -> AppError.UNKNOWN
}
