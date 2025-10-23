package com.govAtizapan.beneficiojoven.model.network


import com.govAtizapan.beneficiojoven.model.promocionesapartar.ApartarPromocionRequest
import com.govAtizapan.beneficiojoven.model.promocionesapartar.ApartarPromocionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApartarPromocionApi {

    @Headers("Content-Type: application/json")
    @POST("/functionality/usuario/apartar-promocion/")
    suspend fun apartarPromocion(
        @Body request: ApartarPromocionRequest
    ): Response<ApartarPromocionResponse>
}