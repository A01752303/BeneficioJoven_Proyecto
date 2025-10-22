package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import retrofit2.Response
import retrofit2.http.GET

/**
 * Lista las promociones del comercio autenticado.
 * El backend identifica el id_negocio a partir del token (Authorization: Bearer ...).
 */
interface ComercioPromotionsApi {

    @GET("functionality/list/promociones")
    suspend fun listarMisPromociones(): Response<List<PromotionResponseGET>>
}
