package com.govAtizapan.beneficiojoven.viewmodel.emailVerification

data class RegistrationData(
    val email: String = "",
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = ""
    // Aquí agregarás más campos como fecha de nacimiento, dirección, etc.
)

