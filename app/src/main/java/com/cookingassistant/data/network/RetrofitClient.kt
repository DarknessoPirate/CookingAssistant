package com.cookingassistant.data.network

import com.cookingassistant.data.repositories.TokenRepository
import com.cookingassistant.data.repositories.ApiRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


class RetrofitClient(private val tokenRepository: TokenRepository){
    private val BASE_URL = "http://192.168.100.14:5080/api/"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    // Create Bearer Authorization header before sending request
    private val authInterceptor = AuthInterceptor(tokenRepository)

    // configure OkHttpClient for logging, authentication
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)

        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()


    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Use custom Gson
            .client(okHttpClient)
            .build().create(ApiRepository::class.java)
    }
}