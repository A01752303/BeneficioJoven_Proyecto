package com.govAtizapan.beneficiojoven.viewmodel.emailVerification

data class RegistrationData(
    val email: String = "",
    val nombre: String = "",
    val apellidoP: String = "",
    val apellidoM: String = "",
    val genero: String = "",
    val fechaNacimiento: String = "",
    val calle: String = "",
    val numero: String = "",
    val colonia: String = "",
    val codigoPostal: String = ""
    // Aquí agregarás más campos como fecha de nacimiento, dirección, etc.
)

