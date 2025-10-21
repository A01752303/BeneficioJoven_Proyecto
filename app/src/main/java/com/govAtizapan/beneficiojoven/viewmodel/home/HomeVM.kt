package com.govAtizapan.beneficiojoven.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionRepository
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow // Importa asStateFlow
import kotlinx.coroutines.launch

class HomeVM : ViewModel() {

    private val repository = PromotionRepository()

    private val _promociones = MutableStateFlow<List<PromotionResponseGET>>(emptyList())
    val promociones: StateFlow<List<PromotionResponseGET>> = _promociones.asStateFlow()

    // --- 👇 ESTADOS DE UI AÑADIDOS ---

    // 1. Para el "Skeleton Loader" inicial
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 2. Para el "Error con Reintento"
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    // --- (Se quitó _isRefreshing) ---

    // 👇 MODIFICADO: 'cargarPromociones' ahora maneja los estados de carga y error
    fun cargarPromociones() {
        viewModelScope.launch {
            _isLoading.value = true // Activa el Skeleton
            _errorState.value = null // Limpia errores previos
            try {
                val data = repository.obtenerPromociones()
                _promociones.value = data
            } catch (e: Exception) {
                _errorState.value = "Error al cargar las promociones. Revisa tu conexión."
                _promociones.value = emptyList()
            } finally {
                _isLoading.value = false // Oculta el Skeleton
            }
        }
    }

    // --- (Se quitó la función refrescarPromociones) ---
}