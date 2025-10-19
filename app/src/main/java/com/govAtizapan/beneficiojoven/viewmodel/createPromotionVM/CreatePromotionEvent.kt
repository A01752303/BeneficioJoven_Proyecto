package com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM

import android.content.ContentResolver
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType

/**
 * Eventos del flujo de creación de promociones (paso a paso + submit).
 * Incluye soporte para imagen promocional opcional (multipart/form-data).
 */
sealed class CreatePromotionEvent {

    // Campos base
    data class TituloChanged(val value: String) : CreatePromotionEvent()
    data class DescripcionChanged(val value: String) : CreatePromotionEvent()
    data class TipoChanged(val value: PromotionType) : CreatePromotionEvent()

    // Campos condicionales por tipo
    data class PorcentajeChanged(val value: String) : CreatePromotionEvent()
    data class PrecioChanged(val value: String) : CreatePromotionEvent()

    // Fechas
    data class StartEndChanged(val startIso: String, val endIso: String) : CreatePromotionEvent()

    // Límites
    data class LimiteTotalChanged(val value: String) : CreatePromotionEvent()
    data class LimitePorUsuarioChanged(val value: String) : CreatePromotionEvent()

// Imagen (opcional)
data class ImagenSeleccionada(val uriString: String) : CreatePromotionEvent()


data object QuitarImagen : CreatePromotionEvent()


data class SetContentResolver(val resolver: ContentResolver) : CreatePromotionEvent()

// Acciones
data object Submit : CreatePromotionEvent()
data object ClearForm : CreatePromotionEvent()
data object ConsumeMessages : CreatePromotionEvent()
}
