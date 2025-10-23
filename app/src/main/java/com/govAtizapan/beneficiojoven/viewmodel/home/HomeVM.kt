package com.govAtizapan.beneficiojoven.viewmodel.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionRepository
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeVM : ViewModel() {

    private val repository = PromotionRepository()

    // 🧩 Promociones
    private val _promociones = MutableStateFlow<List<PromotionResponseGET>>(emptyList())
    val promociones: StateFlow<List<PromotionResponseGET>> = _promociones.asStateFlow()

    // 🧩 Estados de UI
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    // ❤️ Lista local de favoritos (en memoria)
    private val favoritos = mutableStateListOf<PromotionResponseGET>()

    // 🧠 Cargar promociones desde el repositorio
    fun cargarPromociones() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorState.value = null
            try {
                val data = repository.obtenerPromociones()
                _promociones.value = data
            } catch (e: Exception) {
                _errorState.value = "Error al cargar las promociones. Revisa tu conexión."
                _promociones.value = emptyList()
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ❤️ Agregar o quitar una promoción de favoritos
    fun toggleFavorito(promo: PromotionResponseGET) {
        if (favoritos.any { it.id == promo.id }) {
            favoritos.removeAll { it.id == promo.id } // Quitar si ya estaba
        } else {
            favoritos.add(promo) // Agregar si no está
        }
    }

    // ❤️ Verificar si una promoción ya es favorita
    fun esFavorito(promo: PromotionResponseGET): Boolean {
        return favoritos.any { it.id == promo.id }
    }

    // ❤️ Obtener todos los favoritos (para FavoritosView)
    fun obtenerFavoritos(): List<PromotionResponseGET> = favoritos
}