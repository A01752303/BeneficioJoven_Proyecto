package com.govAtizapan.beneficiojoven.model.promotionpost

import com.google.gson.annotations.SerializedName

/**
 * Modelo para crear una promoción (POST).
 *
 * JSON esperado por tu backend (ejemplo):
 * {
 *   "id_negocio": 1,
 *   "nombre": "2x1 en hamburguesas",
 *   "descripcion": "Aplica de viernes a domingo con credencial joven.",
 *   "fecha_inicio": "2025-10-10",
 *   "fecha_fin": "2025-10-20",
 *   "porcentaje": 50,
 *   "precio": 0,
 *   "activo": true
 * }
 *
 * Nota:
 * - Si el descuento es porcentual, envía porcentaje>0 y precio=0.
 * - Si el descuento es monto fijo, envía precio>0 y porcentaje=0.
 */

data class PromotionRequest(
    @SerializedName("id_negocio") val idNegocio: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("fecha_inicio") val fechaInicio: String,     // YYYY-MM-DD
    @SerializedName("fecha_fin") val fechaFin: String,           // YYYY-MM-DD
    @SerializedName("porcentaje") val porcentaje: Int,           // 0 si no aplica
    @SerializedName("precio") val precio: Int,                   // 0 si no aplica
    @SerializedName("activo") val activo: Boolean,
    @SerializedName("limite_total") val limiteTotal: Int,        // 0 = sin límite
    @SerializedName("limite_por_usuario") val limitePorUsuario: Int // 0 = sin límite
)
