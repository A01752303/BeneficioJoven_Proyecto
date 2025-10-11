package com.govAtizapan.beneficiojoven.model.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Reemplaza por tu base URL (debe terminar con '/')
    private const val BASE_URL = "http://107.22.168.136:8000/promociones/"

    private val logging by lazy {
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging) // quítalo en release si no quieres logs
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val promotionsApi: PromotionsApi by lazy {
        retrofit.create(PromotionsApi::class.java)
    }
}
