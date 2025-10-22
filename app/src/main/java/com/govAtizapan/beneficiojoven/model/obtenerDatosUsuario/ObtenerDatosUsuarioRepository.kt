package com.govAtizapan.beneficiojoven.model.obtenerDatosUsuario

import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionManager

class UserRepository {

    private val api = RetrofitClient.obtenerDatosUsuarioApi

    suspend fun fetchUserData(): ObtenerUsuarioResponseGET? {
        // 1. Obtienes el token guardado en tu SessionManager
        val authToken = SessionManager.accessToken

        // 2. Verificas que el token no sea nulo o vacío antes de hacer la llamada
        if (!authToken.isNullOrEmpty()) {
            try {
                // 3. Pasas el token como argumento a la función de la API
                val response = api.obtenerDatosUsuario(authToken)

                if (response.isSuccessful) {
                    val userData = response.body()
                    // Aquí manejas la respuesta exitosa con los datos del usuario (userData)
                    println("Datos del usuario recibidos: $userData")
                    return userData // <-- DEVOLVEMOS LOS DATOS
                } else {
                    // Aquí manejas los errores, como token inválido, etc.
                    val errorCode = response.code()
                    val errorMessage = response.errorBody()?.string()
                    println("Error al obtener datos: Código $errorCode, Mensaje: $errorMessage")
                    return null // <-- Devolvemos null en caso de error
                }
            } catch (e: Exception) {
                // Manejas excepciones de red u otras
                println("Excepción al realizar la llamada: ${e.message}")
                return null // <-- Devolvemos null en caso de excepción
            }
        } else {
            // Manejas el caso en que no haya un token disponible
            println("Error: No se encontró un token de autenticación.")
            return null // <-- Devolvemos null si no hay token
        }
    }
}