package com.irinamihaila.quizzapp.utils

sealed class AppResult<out T> {
    object Progress : AppResult<Nothing>()
    data class Retry<out T>(val retryNo: Int) : AppResult<T>()
    data class Success<out T>(val successData: T?) : AppResult<T>()
    data class Error<out T>(val exception: Throwable) : AppResult<T>()
}