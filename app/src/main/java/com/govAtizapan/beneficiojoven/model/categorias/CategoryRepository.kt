package com.govAtizapan.beneficiojoven.model.categorias

import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CategoryRepository {
    private val api = RetrofitClient.categoryApi

    suspend fun obtenerCategoria(): List<CategoryResponseGET> = withContext(Dispatchers.IO) {
        val response = api.obtenerCategoria()
        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }
}