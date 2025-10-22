package com.govAtizapan.beneficiojoven.viewmodel.cupondetalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.qrpost.QrRequest
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la generación de códigos QR desde el detalle del cupón.
 * Usa autenticación basada en token (Bearer) desde RetrofitClient.
 */
class CuponDetalleViewModel : ViewModel() {

    // Estado de carga del botón
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Mensajes para Snackbars o errores
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    // ID del canje generado por el backend
    private val _idCanje = MutableStateFlow<Int?>(null)
    val idCanje: StateFlow<Int?> = _idCanje

    /**
     * Genera un código QR para la promoción seleccionada.
     * Solo se envía el id_promocion; el backend obtiene el usuario desde el token.
     */
    fun generarQr(idPromocion: Int) {
        if (_isLoading.value) return // Previene doble clic

        _isLoading.value = true
        viewModelScope.launch {
            try {
                // 🔹 Enviar solo el id de la promoción (el token se agrega automáticamente)
                val response = RetrofitClient.qrApi.generarQr(QrRequest(idPromocion))

                if (response.isSuccessful) {
                    val idCanjeResponse = response.body()?.idCanje
                    if (idCanjeResponse != null) {
                        _idCanje.value = idCanjeResponse
                        _snackbarMessage.value = "✅ Cupón generado con éxito."
                    } else {
                        _snackbarMessage.value = "⚠️ No se recibió idCanje del servidor."
                    }
                } else {
                    _snackbarMessage.value = "❌ Error del servidor al generar QR (${response.code()})"
                }

            } catch (e: Exception) {
                _snackbarMessage.value = "⚠️ Error al generar QR: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpia el mensaje del Snackbar una vez mostrado.
     */
    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    /**
     * Limpia el ID del canje después de usarlo (por ejemplo, tras mostrar el QR).
     */
    fun clearIdCanje() {
        _idCanje.value = null
    }
}