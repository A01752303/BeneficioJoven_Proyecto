package com.govAtizapan.beneficiojoven.model.qrvalidacion

import com.google.gson.annotations.SerializedName

/**
 * Cuerpo del request para validar un código QR escaneado.
 */
data class QrValidateRequest(
    @SerializedName("codigo") val codigo: String,                // Ejemplo: "CANJE-103"
    // ID del comercio que valida
)