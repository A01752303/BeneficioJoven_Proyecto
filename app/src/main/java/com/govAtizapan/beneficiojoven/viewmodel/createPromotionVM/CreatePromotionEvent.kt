/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define la sealed class `CreatePromotionEvent`, que representa todos
 * los eventos/disparadores posibles en el flujo de creación de una promoción.
 *
 * Uso:
 * * Estos eventos son enviados desde la UI (Compose) hacia el ViewModel
 * `CreatePromotionViewModel` para actualizar el estado del formulario paso a paso.
 *
 * Contenido cubierto por los eventos:
 * * Datos básicos: título, descripción, tipo de promoción.
 * * Datos condicionales según el tipo: porcentaje de descuento o precio fijo.
 * * Rango de fechas de vigencia.
 * * Límites de canje (total y por usuario).
 * * Imagen opcional de la promoción:
 * * `ImagenSeleccionada(uriString)` guarda la URI seleccionada.
 * * `QuitarImagen` elimina la imagen.
 * * `SetContentResolver(resolver)` permite al ViewModel acceder al `ContentResolver`
 * ```
para construir multipart/form-data al momento de subir la imagen al backend.
```
 *
 * * Acciones globales:
 * * `Submit`: intenta enviar la promoción al backend.
 * * `ClearForm`: limpia el formulario tras un submit exitoso.
 * * `ConsumeMessages`: limpia mensajes de éxito/error ya mostrados en la UI.
 *
 * Este modelo de eventos centraliza toda la interacción de la UI con el ViewModel
 * para mantener un flujo declarativo y predecible.
 */





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
