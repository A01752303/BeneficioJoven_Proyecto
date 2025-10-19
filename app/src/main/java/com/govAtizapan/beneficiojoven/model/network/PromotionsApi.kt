package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.PartMap

interface PromotionsApi {

    // 👇 Endpoint relativo. Retrofit completa con BASE_URL
    @Multipart
    @POST("functionality/promociones/create/")
    suspend fun crearPromocionMultipart(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part? = null
    ): Response<PromotionResponse>
    @GET("/functionality/usuario/list/promociones/")
    suspend fun obtenerPromociones(): Response<List<PromotionResponseGET>>
}
