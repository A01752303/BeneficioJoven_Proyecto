package com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionRequest
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreatePromotionUiState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null
)

class CreatePromotionViewModel : ViewModel() {

    private val _ui = MutableStateFlow(CreatePromotionUiState())
    val ui: StateFlow<CreatePromotionUiState> = _ui

    fun createPromotion(body: PromotionRequest) {
        viewModelScope.launch {
            _ui.value = CreatePromotionUiState(isLoading = true)
            try {
                val resp = RetrofitClient.promotionsApi.crearPromocion(body)
                if (resp.isSuccessful) {
                    _ui.value = CreatePromotionUiState(
                        success = "Promoción creada correctamente"
                    )
                } else {
                    _ui.value = CreatePromotionUiState(
                        error = "HTTP ${resp.code()} - ${resp.errorBody()?.string() ?: "sin detalle"}"
                    )
                }
            } catch (e: Exception) {
                _ui.value = CreatePromotionUiState(error = e.message ?: "Error de red")
            }
        }
    }
}
