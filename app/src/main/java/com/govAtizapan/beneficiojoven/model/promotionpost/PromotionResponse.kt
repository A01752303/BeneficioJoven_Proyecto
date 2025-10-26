/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define el data class `PromotionResponse`, que modela la respuesta
 * del backend al crear o modificar una promoción.
 *
 * Campos:
 * * `id`: Identificador de la promoción afectada (si aplica).
 * * `message`: Mensaje informativo devuelto por el servidor (por ejemplo, éxito o error).
 * * `status`: Estado de la operación (por ejemplo, "success", "error", etc.).
 *
 * Uso:
 * Se utiliza típicamente después de enviar una solicitud POST/PUT relacionada
 * con promociones, para interpretar el resultado de la operación en la UI o lógica de negocio.
 */



package com.govAtizapan.beneficiojoven.model.promotionpost

data class PromotionResponse(
    val id: Int? = null,
    val message: String? = null,
    val status: String?=null
)