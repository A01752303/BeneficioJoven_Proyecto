package com.govAtizapan.beneficiojoven.model.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PromotionsApi {

    // 👇 Endpoint relativo. Retrofit completa con BASE_URL
    @POST("promociones/") // = http://...:8000/promociones/
    suspend fun crearPromocion(
        @Body body: PromotionRequest
    ): Response<PromotionResponse>
}
