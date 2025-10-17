package com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM

import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType

data class CreatePromotionUiState(
    val isLoading: Boolean = false,
    // Form
    val idNegocio: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: PromotionType = PromotionType.DESCUENTO,
    val porcentajeTxt: String = "",
    val precioTxt: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val limiteTotalTxt: String = "",
    val limitePorUsuarioTxt: String = "",
    // Validación
    val isValid: Boolean = false,
    // Mensajes
    val successMessage: String? = null,
    val errorMessage: String? = null
)
