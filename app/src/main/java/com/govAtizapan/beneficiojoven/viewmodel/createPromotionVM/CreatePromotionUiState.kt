package com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM

import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType

data class CreatePromotionUiState(
    val isLoading: Boolean = false,

    // Form
    val idNegocio: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: PromotionType = PromotionType.DESCUENTO,
    val porcentajeTxt: String = "",          // sólo si tipo=DESCUENTO
    val precioTxt: String = "",              // sólo si tipo=PRECIO_FIJO
    val startDate: String = "",              // YYYY-MM-DD
    val endDate: String = "",                // YYYY-MM-DD
    val limiteTotalTxt: String = "",         // 0 o vacío = sin límite
    val limitePorUsuarioTxt: String = "",    // 0 o vacío = sin límite

    // Validación agregada
    val isValid: Boolean = false,

    // Mensajes
    val successMessage: String? = null,
    val errorMessage: String? = null
)
