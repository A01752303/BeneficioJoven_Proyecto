package com.govAtizapan.beneficiojoven.model.qrpost

import com.google.gson.annotations.SerializedName

data class QrResponse(
    @SerializedName("id_canje") val idCanje: Int,
    @SerializedName("message") val message: String? = null
)