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
