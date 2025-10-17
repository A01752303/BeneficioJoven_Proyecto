package com.govAtizapan.beneficiojoven.model.promotionget

data class PromotionResponseGET(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val fecha_inicio: String,
    val fecha_fin: String,
    val tipo: String?,           // puede ser null
    val porcentaje: String,       // "15.00"
    val precio: String,           // "85.00000"
    val activo: Boolean,          // 🔑 FALTABA
    val numero_canjeados: Int     // 🔑 FALTABA
)