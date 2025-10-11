package com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM

import com.govAtizapan.beneficiojoven.model.PromotionResponse

data class CreatePromotionUiState(
    val isLoading: Boolean = false,
    val success: PromotionResponse? = null,
    val error: String? = null
)