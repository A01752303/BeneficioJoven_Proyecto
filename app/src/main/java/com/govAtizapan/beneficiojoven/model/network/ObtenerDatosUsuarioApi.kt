package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.obtenerDatosUsuario.ObtenerUsuarioResponseGET
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ObtenerDatosUsuarioApi{
    @GET("/functionality/usuario/info/{token}")
    suspend fun obtenerDatosUsuario(
        @Path("token") token: String
    ): Response<ObtenerUsuarioResponseGET>
}


