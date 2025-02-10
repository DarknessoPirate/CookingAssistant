package com.cookingassistant.util

import com.cookingassistant.data.Models.ApiErrorResponse
import com.cookingassistant.data.Models.Result
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import retrofit2.Response

class ApiResponseParser {
    fun <T> wrapResponse(response: retrofit2.Response<T>): Result<T?> {
        return if (response.isSuccessful) {
            if (response.code() == 204) {
                // println("204 No Content received")
                Result.Success(null)
            } else {
                // println("Success with body: ${response.body()}")
                Result.Success(response.body())
            }
        } else {
            val errorJson = response.errorBody()?.string()

            // Attempt to parse the error body as JSON, but fall back to a string if that fails
            val detailedErrors = try {
                errorJson?.let {
                    val apiError = Gson().fromJson(it, ApiErrorResponse::class.java)
                    apiError.errors
                }
            } catch (e: JsonSyntaxException) {
                null
            } catch (e: JsonParseException) {
                null
            }

            // If detailed errors are null, handle error body as a simple string message
            val message = detailedErrors?.let { response.message() }
                ?: (errorJson ?: response.message())

            Result.Error(
                message = message,
                errorCode = response.code(),
                detailedErrors = detailedErrors
            )
        }
    }

}