/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define el enum `PromotionType`, que representa las categorías/tipos
 * de promoción que un comercio puede ofrecer dentro de la app.
 *
 * Valores comunes:
 * * `DESCUENTO`: Descuento porcentual sobre el precio normal.
 * * `PRECIO_FIJO`: Precio especial fijo para la promoción.
 * * `DOSxUNO`: Promociones tipo "2x1" o equivalentes.
 * * `TRAE_AMIGO`: Beneficio condicionado a traer a otra persona / referido.
 * * `OTRA`: Cualquier tipo de promoción que no encaje en las anteriores.
 *
 * Uso:
 * Este enum ayuda a la UI y a la lógica de negocio a clasificar y mostrar
 * promociones de forma consistente.
 */



package com.govAtizapan.beneficiojoven.model.promotionpost

enum class PromotionType { DESCUENTO, PRECIO_FIJO, DOSxUNO, TRAE_AMIGO, OTRA }
