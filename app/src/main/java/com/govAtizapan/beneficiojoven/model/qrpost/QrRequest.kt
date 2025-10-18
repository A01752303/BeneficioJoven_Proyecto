package com.govAtizapan.beneficiojoven.model.qrpost

import com.google.gson.annotations.SerializedName

data class QrRequest(
    @SerializedName("id_usuario") val idUsuario: Int,
    @SerializedName("id_promocion") val idPromocion: Int
)