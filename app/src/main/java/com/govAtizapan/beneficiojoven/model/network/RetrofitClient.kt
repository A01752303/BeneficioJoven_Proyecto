/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define `RetrofitClient`, un objeto singleton responsable de:
 *
 * * Configurar Retrofit con la URL base del backend y el convertidor Gson.
 * * Construir y compartir una única instancia de OkHttpClient con:
 * * Interceptor de logging (`HttpLoggingInterceptor`) para depurar peticiones/respuestas HTTP.
 * * Interceptor de autenticación (`authInterceptor`) que inyecta el header Authorization: Bearer <token>
 * ```
usando el token obtenido desde `SessionManager`.
```
 * * Timeouts aumentados (30s) y `retryOnConnectionFailure(true)` para mayor tolerancia a red inestable.
 * * `ConnectionPool` para reutilizar conexiones HTTP/1.1 y evitar reconexiones constantes.
 *
 * * Exponer interfaces de la API (`PromotionsApi`, `UserLoginApi`, `QrApi`, etc.) ya listas para ser usadas
 * en repositorios, ViewModels o casos de uso.
 *
 * Nota:
 * `BASE_URL` apunta al backend Django/FastAPI del proyecto Beneficio Joven. Cambiarla si el servidor
 * se desplaza o si se usan entornos diferentes (desarrollo, staging, producción).
 */


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

        val authToken = SessionManager.fetchAuthToken()

        val request = original.newBuilder()
            .apply {
                if (authToken != null) { // Cambia "AUTH_TOKEN" por "authToken"
                    header("Authorization", "Bearer $authToken")
                }
            }
            .header("Accept", "application/json")
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

    val comercioPromotionsApi: ComercioPromotionsApi by lazy {
        retrofit.create(ComercioPromotionsApi::class.java)
    }

    val obtenerDatosUsuarioApi: ObtenerDatosUsuarioApi by lazy{
        retrofit.create(ObtenerDatosUsuarioApi::class.java)
    }
    val apartarPromocionApi: ApartarPromocionApi by lazy {
        retrofit.create(ApartarPromocionApi::class.java)
    }

    // 🔹 NUEVO: GET /functionality/usuario/list/promociones-apartadas/
    val promocionesApartadasApi: PromocionesApartadasApi by lazy {
        retrofit.create(PromocionesApartadasApi::class.java)
    }}