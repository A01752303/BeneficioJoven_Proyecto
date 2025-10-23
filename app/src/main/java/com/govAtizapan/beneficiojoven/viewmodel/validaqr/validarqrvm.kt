package com.govAtizapan.beneficiojoven.viewmodel.validaqr

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import com.govAtizapan.beneficiojoven.model.qrvalidacion.QrValidateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class QrResultType { NONE, SUCCESS, ERROR }

data class QrValidationState(
    val mensaje: String = "",
    val colorFondo: Color = Color.White,
    val tipoResultado: QrResultType = QrResultType.NONE
)

class ValidarQRViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QrValidationState())
    val uiState: StateFlow<QrValidationState> = _uiState

    fun validarCodigoQR(contenido: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.validarQrApi.validarQr(QrValidateRequest(codigo = contenido))

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        // 🟢 Caso: QR válido
                        _uiState.value = QrValidationState(
                            mensaje = "✅ Canje válido",
                            colorFondo = Color(0xFF00C853), // verde
                            tipoResultado = QrResultType.SUCCESS
                        )
                    } else {
                        // 🔴 Caso: QR inválido
                        _uiState.value = QrValidationState(
                            mensaje = "🚫 Cupón inválido o pertenece a otro establecimiento.",
                            colorFondo = Color(0xFFD50000), // rojo
                            tipoResultado = QrResultType.ERROR
                        )
                    }
                } else {
                    _uiState.value = QrValidationState(
                        mensaje = "❌ Error del servidor (${response.code()})",
                        colorFondo = Color(0xFFD50000),
                        tipoResultado = QrResultType.ERROR
                    )
                }
            } catch (e: Exception) {
                _uiState.value = QrValidationState(
                    mensaje = "⚠️ Error de conexión: ${e.message}",
                    colorFondo = Color(0xFFD50000),
                    tipoResultado = QrResultType.ERROR
                )
            }
        }
    }

    fun resetearEstado() {
        _uiState.value = QrValidationState() // vuelve al fondo blanco y sin ícono
    }
}