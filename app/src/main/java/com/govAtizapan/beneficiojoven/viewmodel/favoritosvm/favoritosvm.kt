package com.govAtizapan.beneficiojoven.viewmodel.favoritosvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class FavoritosViewModel : ViewModel() {

    private val _favoritos = MutableStateFlow<List<PromotionResponseGET>>(emptyList())
    val favoritos: StateFlow<List<PromotionResponseGET>> = _favoritos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val api = RetrofitClient.promocionesApartadasApi

    /**
     * Carga desde el backend las promociones apartadas (favoritos reales).
     */
    fun cargarFavoritos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) { api.listarPromocionesApartadas() }

                if (response.isSuccessful) {
                    _favoritos.value = response.body() ?: emptyList()
                    Log.d("FavoritosVM", "Promociones apartadas: ${_favoritos.value.size}")
                } else {
                    Log.e("FavoritosVM", "Error del servidor (${response.code()})")
                    _favoritos.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("FavoritosVM", "Error al obtener favoritos: ${e.message}", e)
                _favoritos.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
