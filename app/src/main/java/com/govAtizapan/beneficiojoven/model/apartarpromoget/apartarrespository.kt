package com.govAtizapan.beneficiojoven.model.promocionesapartar

import android.util.Log
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository encargado de manejar la lógica para apartar promociones.
 * Endpoint: POST /functionality/usuario/apartar-promocion/
 */
class ApartarPromocionRepository {

    private val api = RetrofitClient.apartarPromocionApi

    /**
     * Envía la solicitud al backend para apartar una promoción.
     *
     * @param idPromocion El ID de la promoción que se desea apartar.
     * @return Mensaje del servidor o un error controlado.
     */
    suspend fun apartarPromocion(idPromocion: Int): String = withContext(Dispatchers.IO) {
        try {
            val request = ApartarPromocionRequest(idPromocion)
            val response = api.apartarPromocion(request)

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("ApartarPromocionRepository", "Promoción apartada: ${body?.message}")
                body?.message ?: "Operación completada correctamente."
            } else {
                val errorMsg = "Error del servidor (${response.code()}): ${response.errorBody()?.string()}"
                Log.e("ApartarPromocionRepository", errorMsg)
                errorMsg
            }
        } catch (e: Exception) {
            val msg = "Error al conectar con el servidor: ${e.message}"
            Log.e("ApartarPromocionRepository", msg, e)
            msg
        }
    }
}
