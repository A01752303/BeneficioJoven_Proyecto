package com.govAtizapan.beneficiojoven.model.qrvalidacion

/**
 * Respuesta del endpoint de validación QR.
 */
data class QrValidateResponse(
    val success: Boolean?,
    val message: String?,
    val id_canje: Int?,
    val id_establecimiento: Int?
)