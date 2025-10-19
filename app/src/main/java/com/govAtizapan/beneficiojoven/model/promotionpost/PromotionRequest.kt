package com.govAtizapan.beneficiojoven.model.promotionpost

import com.google.gson.annotations.SerializedName

/**
 * Modelo para crear una promoción (POST).
 * El backend obtiene el id_negocio desde el token, no se envía en el cuerpo.
 */
data class PromotionRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("fecha_inicio") val fechaInicio: String,     // "YYYY-MM-DD"
    @SerializedName("fecha_fin") val fechaFin: String,           // "YYYY-MM-DD"
    @SerializedName("porcentaje") val porcentaje: Int,           // 0 si no aplica
    @SerializedName("precio") val precio: Int,                   // 0 si no aplica
    @SerializedName("activo") val activo: Boolean,               // siempre true
    @SerializedName("limite_total") val limiteTotal: Int,        // 0 = sin límite
    @SerializedName("limite_por_usuario") val limitePorUsuario: Int // 0 = sin límite
)
