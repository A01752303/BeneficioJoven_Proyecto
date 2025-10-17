package com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM

import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType

sealed interface CreatePromotionEvent {
    data class IdNegocioChanged(val value: String): CreatePromotionEvent
    data class TituloChanged(val value: String): CreatePromotionEvent
    data class DescripcionChanged(val value: String): CreatePromotionEvent
    data class TipoChanged(val value: PromotionType): CreatePromotionEvent
    data class PorcentajeChanged(val value: String): CreatePromotionEvent
    data class PrecioChanged(val value: String): CreatePromotionEvent
    data class StartEndChanged(val startIso: String, val endIso: String): CreatePromotionEvent
    data class LimiteTotalChanged(val value: String): CreatePromotionEvent
    data class LimitePorUsuarioChanged(val value: String): CreatePromotionEvent
    data object Submit: CreatePromotionEvent
    data object ClearForm: CreatePromotionEvent
    data object ConsumeMessages: CreatePromotionEvent
}
