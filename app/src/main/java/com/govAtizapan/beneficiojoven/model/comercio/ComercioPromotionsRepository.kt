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
