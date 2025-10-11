package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.PromotionRequest
import com.govAtizapan.beneficiojoven.model.PromotionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PromotionsApi {
    // Cambia "promotions" por tu endpoint real
    @POST("promociones")
    suspend fun createPromotion(@Body body: PromotionRequest): Response<PromotionResponse>
}
