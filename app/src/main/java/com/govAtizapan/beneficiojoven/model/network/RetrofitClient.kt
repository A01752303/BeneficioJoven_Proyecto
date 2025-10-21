package com.govAtizapan.beneficiojoven.model.network

import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionManager

object RetrofitClient {

    private const val BASE_URL = "http://54.227.48.118:8000/"


    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS // Cambiado para ver headers
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()

        // 🔄 Toma el token actual del SessionManager
        val AUTH_TOKEN = SessionManager.accessToken

        val request = original.newBuilder()
            // Solo agrega el header si hay token guardado
            .apply {
                if (AUTH_TOKEN != null) {
                    header("Authorization", "Bearer $AUTH_TOKEN")
                }
            }
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

    val userRegisterApi: UserRegisterApi by lazy {
        retrofit.create(UserRegisterApi::class.java)
    }
    val qrApi: QrApi by lazy {
        retrofit.create(QrApi::class.java)
    }
    val validarQrApi: ValidarQrApi by lazy {
        retrofit.create(ValidarQrApi::class.java)
    }

    val categoryApi: CategoryApi by lazy {
        retrofit.create(CategoryApi::class.java)
    }

}