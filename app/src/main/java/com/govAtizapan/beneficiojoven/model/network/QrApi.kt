package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.qrpost.QrRequest
import com.govAtizapan.beneficiojoven.model.qrpost.QrResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface QrApi {

    // POST /api/canjes/generar_qr
    @POST("/functionality/usuario/codigo-qr/")
    suspend fun generarQr(
        @Body body: QrRequest
    ): Response<QrResponse>
}