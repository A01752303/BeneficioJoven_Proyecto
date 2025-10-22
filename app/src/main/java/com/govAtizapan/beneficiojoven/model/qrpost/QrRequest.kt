package com.govAtizapan.beneficiojoven.model.qrpost

import com.google.gson.annotations.SerializedName

data class QrRequest(

    @SerializedName("id_promocion") val idPromocion: Int
)