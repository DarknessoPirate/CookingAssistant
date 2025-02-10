package com.cookingassistant.data.network
import com.cookingassistant.data.repositories.TokenRepository
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenRepository: TokenRepository): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenRepository.getToken()

        // If the token exists, add it to the Authorization header
        val request = if (token != null && token.isNotEmpty()) {
            val updatedRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            updatedRequest
        } else {
            chain.request()
        }

        // Proceed with the request
        return chain.proceed(request)
    }
}