package com.govAtizapan.beneficiojoven.model.obtenerDatosUsuario

data class ObtenerUsuarioResponseGET (
    val id: Int,
    val nombre: String,
    val apellido_paterno: String,
    val apellido_materno: String,
    val correo: String,
    val telefono: String
)