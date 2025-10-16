package com.govAtizapan.beneficiojoven.model.userRegister

import com.google.gson.annotations.SerializedName

data class RegisterUserRequest(
    @SerializedName("correo") val email: String = "",
    @SerializedName("contrasena") val contrasena: String = "",
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("apellido_paterno") val apellidoP: String = "",
    @SerializedName("apellido_materno") val apellidoM: String = "",
    @SerializedName("genero") val genero: String = "",
    @SerializedName("nacimiento") val fechaNacimiento: String = "",
    @SerializedName("calle") val calle: String = "",
    @SerializedName("numero") val numero: String = "",
    @SerializedName("colonia") val colonia: String = "",
    @SerializedName("cp") val codigoPostal: String = "",
    @SerializedName("municipio") val municipio: String = "",
    @SerializedName("estado") val estado: String = ""
)
