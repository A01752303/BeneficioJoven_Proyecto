package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.categorias.CategoryResponseGET
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApi {
    @GET("/functionality/usuario/list/categorias/")
    suspend fun obtenerCategoria(): Response<List<CategoryResponseGET>>
}