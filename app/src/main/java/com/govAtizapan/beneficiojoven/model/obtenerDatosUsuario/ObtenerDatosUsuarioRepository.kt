package com.govAtizapan.beneficiojoven.model.obtenerDatosUsuario

import android.util.Log
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import com.govAtizapan.beneficiojoven.model.obtenerDatosUsuario.ObtenerUsuarioResponseGET
// Ya no importamos SessionManager aquí, el interceptor se encarga.

class UserRepository {

    private val api = RetrofitClient.obtenerDatosUsuarioApi
    private val TAG = "UserRepository"

    suspend fun fetchUserData(): ObtenerUsuarioResponseGET? {
        Log.d(TAG, "Iniciando fetchUserData (token será manejado por interceptor)")

        // 1. Ya no obtenemos el token manualmente.
        // El 'authInterceptor' en RetrofitClient se encargará de añadirlo
        // automáticamente a la cabecera (Header).

        try {
            // 2. Llamamos a la función sin pasarle el token
            val response = api.obtenerDatosUsuario()
            Log.d(TAG, "Llamada a API realizada. URL: ${response.raw().request.url}")


            if (response.isSuccessful) {
                val userData = response.body()
                Log.i(TAG, "Datos del usuario recibidos: $userData")
                return userData // <-- DEVOLVEMOS LOS DATOS
            } else {
                // Manejamos errores
                val errorCode = response.code()
                val errorMessage = response.errorBody()?.string()
                Log.e(TAG, "Error al obtener datos: Código $errorCode, Mensaje: $errorMessage")
                return null // <-- Devolvemos null en caso de error
            }
        } catch (e: Exception) {
            // Manejamos excepciones de red u otras
            Log.e(TAG, "Excepción al realizar la llamada: ${e.message}", e)
            return null // <-- Devolvemos null en caso de excepción
        }
    }
}