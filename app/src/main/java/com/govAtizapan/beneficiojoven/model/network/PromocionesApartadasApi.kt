package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import retrofit2.Response
import retrofit2.http.GET

interface PromocionesApartadasApi {

    @GET("/functionality/usuario/list/promociones-apartadas/")
    suspend fun listarPromocionesApartadas(): Response<List<PromotionResponseGET>>
}