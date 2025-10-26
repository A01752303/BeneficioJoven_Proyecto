/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Esta interfaz define el cliente Retrofit `ComercioPromotionsApi`, encargado de
 * consumir el endpoint remoto que devuelve las promociones asociadas al comercio autenticado.
 *
 * Responsabilidades:
 * * Hacer la petición GET a `/functionality/list/promociones`.
 * * Retornar la respuesta cruda del servidor como `Response<List<PromotionResponseGET>>`,
 * donde `PromotionResponseGET` es el DTO que refleja el modelo tal como viene del backend.
 *
 * Uso:
 * Esta API normalmente es llamada desde un repositorio (por ejemplo,
 * `ComercioPromotionsRepository`), donde se maneja el mapeo al modelo de dominio
 * y el control de errores antes de exponer los datos a la capa de UI.
 */


package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import retrofit2.Response
import retrofit2.http.GET


interface ComercioPromotionsApi {

    @GET("functionality/list/promociones")
    suspend fun listarMisPromociones(): Response<List<PromotionResponseGET>>
}
