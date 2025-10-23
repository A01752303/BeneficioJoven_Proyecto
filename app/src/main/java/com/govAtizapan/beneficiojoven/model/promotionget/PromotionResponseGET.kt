package com.govAtizapan.beneficiojoven.model.promotionget

// Objeto para la categoría interna
data class Categoria(
    val titulo: String
)

// Data class actualizada
data class PromotionResponseGET(
    val id: Int,
    val negocio_nombre: String,
    val negocio_logo: String,
    val categorias: List<Categoria>, // Campo añadido
    val nombre: String,
    val descripcion: String,
    val limite_por_usuario: Int,
    val limite_total: Int,
    val fecha_inicio: String,
    val fecha_fin: String,
    val imagen: String,
    val numero_canjeados: Int,
    val tipo: String,
    val porcentaje: String,
    val precio: String,
    val activo: Boolean,
    val fecha_creado: String,
    val id_administrador_negocio: Int,
    val id_negocio: Int,
    val es_apartado: Boolean = false
)