package com.govAtizapan.beneficiojoven.model.promotionget

data class PromotionResponseGET(
    val id: Int,
    val id_negocio: Int,
    val nombre: String,
    val descripcion: String,
    val fecha_inicio: String,
    val fecha_fin: String,
    val porcentaje: Int,
    val precio: String,
    val activo: Boolean,
    val fecha_creado: String
)