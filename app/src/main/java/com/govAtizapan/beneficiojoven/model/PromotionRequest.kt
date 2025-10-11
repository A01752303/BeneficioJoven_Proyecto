package com.govAtizapan.beneficiojoven.model

import com.google.gson.annotations.SerializedName

data class PromotionRequest(
    @SerializedName("id_negocio") val idNegocio: Int,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("discount_type") val discountType: String, // "PERCENT"
    @SerializedName("discount_value") val discountValue: Int,  // 50
    @SerializedName("start_date") val startDate: String,       // "yyyy-MM-dd"
    @SerializedName("end_date") val endDate: String,           // "yyyy-MM-dd"
    @SerializedName("online_only") val onlineOnly: Boolean,
    @SerializedName("is_stackable") val isStackable: Boolean,
    @SerializedName("terms") val terms: String
)
