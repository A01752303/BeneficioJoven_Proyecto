/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define el data class `CreatePromotionUiState`, que modela TODO el estado
 * de la UI durante el flujo de creación de una promoción (wizard paso a paso).
 *
 * Contenido del estado:
 * * Datos base de la promoción:
 * * `titulo`, `descripcion`, `tipo` (`PromotionType`).
 *
 * * Datos condicionales según el tipo de promoción:
 * * `porcentajeTxt`: usado cuando `tipo = DESCUENTO` (1..100).
 * * `precioTxt`: usado cuando `tipo = PRECIO_FIJO` (>0 MXN).
 * Para los demás tipos se interpreta como "0".
 *
 * * Vigencia:
 * * `startDate`, `endDate` en formato ISO "YYYY-MM-DD".
 *
 * * Límites de canje:
 * * `limiteTotalTxt`: límite total de canjes (vacío o "0" = sin límite).
 * * `limitePorUsuarioTxt`: límite por usuario (vacío o "0" = sin límite).
 *
 * * Imagen opcional:
 * * `imagenUri`: URI (toString) de la imagen seleccionada en la UI o `null` si no hay.
 * * `contentResolver`: se inyecta desde la UI con `SetContentResolver`, para que
 * ```
el ViewModel pueda leer el contenido y armar multipart/form-data al hacer el POST.
```
 *
 * * Control de la UI:
 * * `isValid`: indica si el formulario global está listo para enviarse.
 * * `isLoading`: indica si se está haciendo submit.
 * * `successMessage` / `errorMessage`: mensajes que activan diálogos en la vista final
 * ```
(`PromoResumenView`).
```
 *
 * Uso:
 * `CreatePromotionViewModel` expone una instancia de este estado como `StateFlow`
 * y lo va actualizando en respuesta a eventos `CreatePromotionEvent`.
 */




package com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM

import android.content.ContentResolver
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType

/**
 * Estado de UI para el flujo de creación de promociones.
 * Compatible con envío multipart/form-data e imagen opcional.
 */
data class CreatePromotionUiState(
    // Datos base
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: PromotionType = PromotionType.DESCUENTO,

    // Condicionales por tipo
    val porcentajeTxt: String = "",   // 1..100 cuando tipo = DESCUENTO, si no, "0"
    val precioTxt: String = "",       // >0 cuando tipo = PRECIO_FIJO, si no, "0"

    // Fechas ISO (YYYY-MM-DD)
    val startDate: String = "",
    val endDate: String = "",

    // Límites (0 o vacío = sin límite)
    val limiteTotalTxt: String = "",
    val limitePorUsuarioTxt: String = "",

    // Imagen opcional (uri.toString() desde la UI; null si no hay imagen)
    val imagenUri: String? = null,

    // Si decides construir la parte de imagen en el ViewModel (opción simple):
    // Inyéctalo desde la UI con CreatePromotionEvent.SetContentResolver(LocalContext.current.contentResolver)
    val contentResolver: ContentResolver? = null,

    // Control de UI
    val isValid: Boolean = false,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
