/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define el repositorio `ComercioPromotionsRepository`, el modelo de dominio `ComercioPromotion`
 * y las funciones de mapeo utilizadas para transformar las respuestas de la API a objetos listos para UI.
 *
 * Responsabilidades principales:
 * * Consumir el endpoint remoto (ComercioPromotionsApi) para obtener las promociones del comercio autenticado.
 * * Manejar errores HTTP, excepciones de red y cancelaciones de corrutinas de forma segura.
 * * Convertir los datos crudos del backend (por ejemplo, números representados como String y snake_case)
 * a un modelo de dominio Kotlin fuertemente tipado y amigable para la capa de presentación.
 * * Exponer `ComercioPromotion`, que contiene información de cada promoción: nombre, tipo,
 * vigencia, estado activo, número de canjeos y URL opcional de imagen.
 *
 * Nota:
 * Las fechas se mantienen tal cual llegan en formato ISO (por ejemplo "2025-10-25T18:40:00Z") y la vista decide
 * el formateo final. Los campos numéricos como `porcentaje` y `precio` se convierten a Double con fallback 0.0.
 */



package com.govAtizapan.beneficiojoven.model.comercio


import com.govAtizapan.beneficiojoven.model.network.ComercioPromotionsApi
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import kotlinx.coroutines.CancellationException

/**
 * Repositorio para listar las promociones del comercio autenticado.
 * Usa ComercioPromotionsApi y expone un modelo de dominio simplificado para la UI.
 */
class ComercioPromotionsRepository(
    private val api: ComercioPromotionsApi
) {

    /**
     * Devuelve Result con la lista de promociones mapeadas a [ComercioPromotion].
     * - Maneja HTTP errors y excepciones de red.
     * - Convierte strings numéricos ("30.00", "0.00000") a valores Double.
     */
    suspend fun fetchMisPromociones(): Result<List<ComercioPromotion>> {
        return try {
            val resp = api.listarMisPromociones()
            if (resp.isSuccessful) {
                val list = (resp.body() ?: emptyList()).map { it.toDomain() }
                Result.success(list)
            } else {
                Result.failure(
                    IllegalStateException(
                        "HTTP ${resp.code()}: ${resp.errorBody()?.string() ?: "Error al obtener promociones"}"
                    )
                )
            }
        } catch (ce: CancellationException) {
            throw ce
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}

/**
 * Modelo de dominio listo para UI.
 * - Mantiene tipos prácticos (Double/Int/Boolean)
 * - Fechas en ISO tal como llegan; la UI decide el formateo.
 */
data class ComercioPromotion(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val fechaInicioIso: String,
    val fechaFinIso: String,
    val tipo: String,                // "porcentaje" | "precio" | ...
    val porcentaje: Double,          // 0.0 si no aplica
    val precio: Double,              // 0.0 si no aplica
    val activo: Boolean,
    val numeroCanjeados: Int,
    val imagenUrl: String?           // puede ser null
)

/**
 * Mapeo desde el DTO de la API (snake_case y números como string)
 * hacia el modelo de dominio.
 */
private fun PromotionResponseGET.toDomain(): ComercioPromotion =
    ComercioPromotion(
        id = id,
        nombre = nombre.orEmpty(),
        descripcion = descripcion.orEmpty(),
        fechaInicioIso = fecha_inicio.orEmpty(),
        fechaFinIso = fecha_fin.orEmpty(),
        tipo = tipo.orEmpty(),
        porcentaje = porcentaje.toDoubleOrZero(),
        precio = precio.toDoubleOrZero(),
        activo = activo,
        numeroCanjeados = numero_canjeados,
        imagenUrl = imagen
    )

private fun String?.toDoubleOrZero(): Double = this?.toDoubleOrNull() ?: 0.0
