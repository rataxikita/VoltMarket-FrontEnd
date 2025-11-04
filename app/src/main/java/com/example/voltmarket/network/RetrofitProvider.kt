package com.example.voltmarket.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitProvider {

    private const val USE_EMULATOR = true
    private const val EMULATOR_IP = "10.0.2.2"

    // cmd / ipconfig / ipv4
    private const val PHYSICAL_IP = "192.168.100.24"

    private val BASE_URL = if (USE_EMULATOR) {
        "http://$EMULATOR_IP:8080/api/"
    } else {
        "http://$PHYSICAL_IP:8080/api/"
    }

    private var authToken: String? = null

    fun setToken(token: String?) {
        authToken = token
    }

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val url = request.url.toString()
        
        // No agregar token a endpoints de autenticación
        val isAuthEndpoint = url.contains("/auth/login") || url.contains("/auth/register")
        
        val requestBuilder = request.newBuilder()
        if (!isAuthEndpoint && authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer $authToken")
        }
        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}