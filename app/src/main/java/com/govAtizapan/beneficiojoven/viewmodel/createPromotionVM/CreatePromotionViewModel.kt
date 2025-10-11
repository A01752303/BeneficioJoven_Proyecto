package com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.PromotionRequest
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CreatePromotionViewModel : ViewModel() {
    private val _ui = MutableStateFlow(CreatePromotionUiState())
    val ui: StateFlow<CreatePromotionUiState> = _ui

    fun createPromotion(body: PromotionRequest) {
        _ui.value = _ui.value.copy(isLoading = true, error = null, success = null)
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.promotionsApi.createPromotion(body)
                if (resp.isSuccessful) {
                    _ui.value = _ui.value.copy(isLoading = false, success = resp.body())
                } else {
                    val errText = resp.errorBody()?.string() ?: resp.message()
                    _ui.value = _ui.value.copy(isLoading = false, error = "Error ${resp.code()}: $errText")
                }
            } catch (e: IOException) {
                _ui.value = _ui.value.copy(isLoading = false, error = "Sin conexión: ${e.localizedMessage}")
            } catch (e: HttpException) {
                _ui.value = _ui.value.copy(isLoading = false, error = "HTTP ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(isLoading = false, error = "Error: ${e.localizedMessage}")
            }
        }
    }
}
