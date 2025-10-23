package com.govAtizapan.beneficiojoven.model.promocionesapartar

import com.google.gson.annotations.SerializedName

/**
 * Cuerpo del request para apartar una promoción.
 * Este endpoint usa el token del usuario para identificarlo, por lo que
 * no se envía el ID del usuario directamente.
 */
data class ApartarPromocionRequest(
    @SerializedName("id_promocion") val idPromocion: Int // ID de la promoción que se desea apartar
)