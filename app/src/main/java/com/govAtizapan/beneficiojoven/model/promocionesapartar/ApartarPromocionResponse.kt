package com.govAtizapan.beneficiojoven.model.promocionesapartar

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del servidor al intentar apartar una promoción.
 */
data class ApartarPromocionResponse(

    @SerializedName("success")
    val success: Boolean, // Indica si la operación fue exitosa

    @SerializedName("message")
    val message: String   // Mensaje informativo del backend (por ejemplo: "Promoción apartada correctamente.")
)