package com.govAtizapan.beneficiojoven.model.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.govAtizapan.beneficiojoven.model.qrvalidacion.QrValidateRequest
import com.govAtizapan.beneficiojoven.model.qrvalidacion.QrValidateResponse


interface ValidarQrApi {
    @POST("/functionality/cajero/validar-qr/")
    suspend fun validarQr(
        @Body body: QrValidateRequest
    ): Response<QrValidateResponse>
}