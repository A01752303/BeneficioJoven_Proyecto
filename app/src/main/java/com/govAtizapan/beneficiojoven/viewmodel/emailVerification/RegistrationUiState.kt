package com.govAtizapan.beneficiojoven.viewmodel.emailVerification

sealed interface RegistrationUiState {
    object Idle : RegistrationUiState // Estado inicial, no ha pasado nada
    object Loading : RegistrationUiState // Proceso en curso, muestra un ProgressBar
    data class Error(val message: String) : RegistrationUiState // Ocurrió un error
    object VerificationEmailSent : RegistrationUiState // Correo enviado, navega a "Revisa tu bandeja"
    object UserVerified : RegistrationUiState // Usuario verificado, navega a la pantalla de "Nombre"

    object RegistrationComplete : RegistrationUiState
}