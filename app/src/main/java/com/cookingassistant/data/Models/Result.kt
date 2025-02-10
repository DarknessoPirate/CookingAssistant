package com.cookingassistant.data.Models

sealed class Result<out T> {
    data class Success<out T>(val data: T): Result<T>()
    data class Error(
        val message: String,
        val errorCode: Int?,
        val detailedErrors: Map<String,List<String>>? = null,
    ): Result<Nothing>()
}