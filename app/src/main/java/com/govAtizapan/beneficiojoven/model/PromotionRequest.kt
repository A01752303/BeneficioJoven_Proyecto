package com.govAtizapan.beneficiojoven.model.network

import com.google.gson.annotations.SerializedName

// Match EXACTO con tu JSON (snake_case)
data class PromotionRequest(
    @SerializedName("id_negocio") val idNegocio: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("fecha_inicio") val fechaInicio: String, // ISO 8601
    @SerializedName("fecha_fin") val fechaFin: String,       // ISO 8601
    @SerializedName("porcentaje") val porcentaje: Int,
    @SerializedName("precio") val precio: Int,
    @SerializedName("activo") val activo: Boolean
)
