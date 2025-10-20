package com.govAtizapan.beneficiojoven.model.promotionget

data class PromotionResponseGET(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val limite_por_usuario: Int,
    val limite_total: Int,
    val fecha_inicio: String,
    val fecha_fin: String,
    val imagen: String,
    val numero_canjeados: Int,
    val tipo: String?,
    val porcentaje: String,
    val precio: String,
    val activo: Boolean,
    val fecha_creado: String,
    val id_administrador_negocio: Int,
    val id_negocio: Int
)