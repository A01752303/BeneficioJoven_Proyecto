package com.govAtizapan.beneficiojoven.model.sessionManager

object SessionManager {

    /**
     * Variable para guardar el token de acceso del usuario.
     * Solo puede ser modificada desde este objeto (private set).
     */
    var accessToken: String? = null
        private set

    /**
     * Guarda el token al iniciar sesión.
     * @param token El access_token que viene de la respuesta de la API.
     */
    fun saveAuthToken(token: String) {
        accessToken = token
    }

    /**
     * Limpia el token al cerrar sesión.
     */
    fun clearAuthToken() {
        accessToken = null
    }
}