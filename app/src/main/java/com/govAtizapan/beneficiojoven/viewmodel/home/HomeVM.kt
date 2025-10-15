package com.govAtizapan.beneficiojoven.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionRepository
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeVM : ViewModel() {

    private val repository = PromotionRepository()

    private val _promociones = MutableStateFlow<List<PromotionResponseGET>>(emptyList())
    val promociones: StateFlow<List<PromotionResponseGET>> = _promociones

    fun cargarPromociones() {
        viewModelScope.launch {
            val data = repository.obtenerPromociones()
            _promociones.value = data
        }
    }
}