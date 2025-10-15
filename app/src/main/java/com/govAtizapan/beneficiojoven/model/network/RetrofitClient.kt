package com.govAtizapan.beneficiojoven.model.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {

    // ⛳️ TU URL BASE AQUÍ (con / al final)
    private const val BASE_URL = "http://107.22.168.136:8000/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)                // 👈 aquí se usa la URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    val promotionsApi: PromotionsApi by lazy {
        retrofit.create(PromotionsApi::class.java)
    }
}
