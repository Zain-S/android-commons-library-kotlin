package com.saeedtechies.commons.utils

sealed class ResultData<out T> {
    data class Complete(val nothing: Nothing? = null): ResultData<Nothing>()
    data class Success<out T>(val data: T? = null): ResultData<T>()
    data class Loading(val nothing: Nothing? = null): ResultData<Nothing>()
    data class Failed(
        val message: String? = null,
        val status: String? = null,
        val title: String? = null,
        val specialError: Boolean = false
    ) : ResultData<Nothing>()
    data class Exception(val exception: kotlin.Exception? = null): ResultData<Nothing>()
}