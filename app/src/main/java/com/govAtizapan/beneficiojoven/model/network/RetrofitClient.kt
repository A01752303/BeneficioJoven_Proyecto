package com.govAtizapan.beneficiojoven.model.network

import android.util.Log
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://54.227.48.118:8000/"
    private const val AUTH_TOKEN = "RB36chhL0J2astNCWSfNqSyIlV5tSX"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS // Cambiado para ver headers
    }

    // 🟢 Interceptor SIN "Connection: close"
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header("Authorization", "Bearer $AUTH_TOKEN")
            .header("Accept", "application/json") // 🔑 Especifica que esperas JSON
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }
    private val httpClient = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_1_1))
        // 🔑 CAMBIO: Permitir conexiones persistentes
        .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
        .addInterceptor(logging)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS) // Aumentado
        .readTimeout(30, TimeUnit.SECONDS)    // Aumentado
        .writeTimeout(30, TimeUnit.SECONDS)   // Aumentado
        .retryOnConnectionFailure(true)
        .build()


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }



    val promotionsApi: PromotionsApi by lazy {
        retrofit.create(PromotionsApi::class.java)
    }
    val userLoginApi: UserLoginApi by lazy {
        retrofit.create(UserLoginApi::class.java)
    }

    // 🔑 AGREGA ESTO - Tu compañero olvidó esta parte
    val userRegisterApi: UserRegisterApi by lazy {
        retrofit.create(UserRegisterApi::class.java)
    }
    val qrApi: QrApi by lazy {
        retrofit.create(QrApi::class.java)
    }
}