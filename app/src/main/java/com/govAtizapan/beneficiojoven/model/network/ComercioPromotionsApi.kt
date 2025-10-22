package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import retrofit2.Response
import retrofit2.http.GET


interface ComercioPromotionsApi {

    @GET("functionality/list/promociones")
    suspend fun listarMisPromociones(): Response<List<PromotionResponseGET>>
}
