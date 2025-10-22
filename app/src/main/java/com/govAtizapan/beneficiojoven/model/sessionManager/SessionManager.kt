package com.govAtizapan.beneficiojoven.model.sessionManager

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionRole // ⬅️ IMPORTA EL ENUM

object SessionManager {

    private const val PREFS_NAME = "beneficio_joven_session_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_TYPE = "user_type" // ⬅️ NUEVA KEY

    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        if (sharedPreferences == null) {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            sharedPreferences = EncryptedSharedPreferences.create(
                PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    /**
     * NUEVO: Guarda la sesión completa (token y tipo).
     */
    fun saveSession(token: String, userType: SessionRole) {
        sharedPreferences?.edit()?.apply {
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_TYPE, userType.name) // Guarda el nombre del enum (ej. "USUARIO")
            apply()
        } ?: throw IllegalStateException("SessionManager no ha sido inicializado.")
    }

    /**
     * Obtiene el token guardado.
     * (Este método no cambia)
     */
    fun fetchAuthToken(): String? {
        return sharedPreferences?.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * NUEVO: Obtiene el tipo de usuario de la sesión.
     */
    fun fetchUserType(): SessionRole? {
        val typeString = sharedPreferences?.getString(KEY_USER_TYPE, null)

        // Convierte el String guardado (ej. "USUARIO") de vuelta a un Enum
        return if (typeString != null) {
            try {
                SessionRole.valueOf(typeString)
            } catch (e: IllegalArgumentException) {
                // En caso de que el valor guardado sea inválido
                null
            }
        } else {
            null
        }
    }

    /**
     * MEJORADO: Limpia la sesión completa.
     */
    fun clearSession() {
        sharedPreferences?.edit()?.apply {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_USER_TYPE)
            apply()
        }
    }
}