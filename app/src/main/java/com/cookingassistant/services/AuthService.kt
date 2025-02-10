package com.cookingassistant.services

import android.util.Log
import com.cookingassistant.data.DTO.LoginRequest
import com.cookingassistant.data.DTO.LoginResponse
import com.cookingassistant.data.Models.Result
import com.cookingassistant.data.DTO.RegisterRequest
import com.cookingassistant.data.repositories.ApiRepository
import com.cookingassistant.util.ApiResponseParser
import retrofit2.Response

class AuthService(private val _apiRepository: ApiRepository) {
    private val _apiResponseParser = ApiResponseParser()

    suspend fun logInUser(email: String, password:String): Result<LoginResponse?> {
        val requestBody = LoginRequest(
            email,
            password
        )
        return try {
            val response = _apiRepository.logIn(requestBody)
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception){
            Log.e("AuthService", "Exception in logInUser: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred during login: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }


    }

    suspend fun registerUser(username: String, email: String, password: String): Result<Unit?>
    {
        val requestBody = RegisterRequest(
            username,
            email,
            password
        )
        return try {
            val response = _apiRepository.register(requestBody)
            _apiResponseParser.wrapResponse(response)

        } catch (e: Exception) {
            Log.e("AuthService", "Exception in registerUser: ${e.localizedMessage}", e)
            Result.Error(
                message = "An exception occurred during registration: ${e.localizedMessage}",
                errorCode = INTERNAL_SERVICE_ERROR_CODE
            )
        }
    }



}