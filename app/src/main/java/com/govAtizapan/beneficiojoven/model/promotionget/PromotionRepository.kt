package com.govAtizapan.beneficiojoven.model.promotionget


import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PromotionRepository {

    private val api = RetrofitClient.promotionsApi

    suspend fun obtenerPromociones(): List<PromotionResponseGET> = withContext(Dispatchers.IO) {
        val response = api.obtenerPromociones()
        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }
}