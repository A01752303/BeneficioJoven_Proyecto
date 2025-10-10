package com.govAtizapan.beneficiojoven.viewmodel.emailVerification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.auth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class EmailVerificationVM : ViewModel() {
    private val instanceId = UUID.randomUUID().toString()
    init {
        // Este espía nos dice cuándo se crea una nueva instancia
        Log.d("ViewModelDebug", "PASO 1: ViewModel CREADO con ID: $instanceId")
    }

    private val auth = Firebase.auth
    private var verificationCheckJob: Job? = null

    // StateFlow para los datos del formulario (email, nombre, etc.)
    private val _registrationData = MutableStateFlow(RegistrationData())
    val registrationData = _registrationData.asStateFlow()

    // StateFlow para el estado de la UI (cargando, error, navegación)
    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)
    val uiState = _uiState.asStateFlow()

    /**
     * Inicia el proceso de registro usando Coroutines y await() para un código más limpio.
     */
    fun registerUserAndSendVerification(email: String, password: String) {
        Log.d("ViewModelDebug", "Updating email in StateFlow to: '$email'")
        _registrationData.update { it.copy(email = email) }
        _uiState.value = RegistrationUiState.Loading

        viewModelScope.launch {

            try {
                // 1. Crea el usuario y espera el resultado
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                    ?: throw IllegalStateException("Firebase user no puede ser nulo después del registro.")

                // 2. Construye la configuración del deep link con el dominio correcto
                val actionCodeSettings = ActionCodeSettings.newBuilder()
                    .setUrl("https://loginbeneficiojoven-1eac8.web.app/verify?email=${user.email}")
                    .setHandleCodeInApp(true)
                    .setAndroidPackageName(
                        "com.tupaquete.app", // Asegúrate de que este es tu paquete correcto
                        true,
                        "21"
                    )
                    .build()

                // 3. Envía el correo de verificación y espera a que se complete
                user.sendEmailVerification(actionCodeSettings).await()

                // 4. Si todo fue exitoso, actualiza el estado
                _uiState.value = RegistrationUiState.VerificationEmailSent
            } catch (e: Exception) {
                // Si algo falla (creación o envío), se captura aquí
                _uiState.value = RegistrationUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    /**
     * Comienza a verificar periódicamente si el usuario ya validó su correo.
     */
    fun startVerificationCheck() {
        verificationCheckJob?.cancel()
        verificationCheckJob = viewModelScope.launch {
            while (true) {
                delay(3000) // Espera 3 segundos
                // Forzamos la recarga del estado del usuario desde el backend de Firebase
                auth.currentUser?.reload()?.await()
                val user = auth.currentUser
                if (user != null && user.isEmailVerified) {
                    _uiState.value = RegistrationUiState.UserVerified
                    stopVerificationCheck()
                    break
                }
            }
        }
    }

    /**
     * Detiene la verificación periódica.
     */
    fun stopVerificationCheck() {
        verificationCheckJob?.cancel()
    }

    fun updateNombreCompleto(_nombre: String, _apellidoP: String, _apellidoM: String) {
        // Asegúrate que tu data class RegistrationData tenga un campo 'name'
        _registrationData.update { it.copy(nombre = _nombre) }
        _registrationData.update { it.copy(apellidoP = _apellidoP) }
        _registrationData.update { it.copy(apellidoM = _apellidoM) }
    }

    fun updateGenero(_genero: String) {
        _registrationData.update { it.copy(genero = _genero) }
    }

    fun completeRegistrationAndSaveToSql() {
        _uiState.value = RegistrationUiState.Loading
        val finalData = _registrationData.value
        Log.d("SQL", "Enviando a SQL los siguientes datos: $finalData")
    }

    fun updateFechaNacimiento(_fechaNacimiento: String) {
        _registrationData.update { it.copy(fechaNacimiento = _fechaNacimiento) }
    }

    fun updateDireccion(_calle: String, _numero: String, _colonia: String, _codigoPostal: String) {
        _registrationData.update { it.copy(calle = _calle) }
        _registrationData.update { it.copy(numero = _numero) }
        _registrationData.update { it.copy(colonia = _colonia) }
        _registrationData.update { it.copy(codigoPostal = _codigoPostal) }
    }

    fun resetUiState() {
        _uiState.value = RegistrationUiState.Idle
    }
}