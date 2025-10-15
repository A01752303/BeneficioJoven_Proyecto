package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionResponse
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface PromotionsApi {

    // 👇 Endpoint relativo. Retrofit completa con BASE_URL
    @POST("functionality/promociones/create/")
    suspend fun crearPromocion(
        @Body body: PromotionRequest
    ): Response<PromotionResponse>
    @GET("promociones/")
    suspend fun obtenerPromociones(): Response<List<PromotionResponseGET>>
}
