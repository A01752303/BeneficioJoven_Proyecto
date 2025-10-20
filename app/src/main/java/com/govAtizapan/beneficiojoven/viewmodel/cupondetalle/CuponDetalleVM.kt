package com.govAtizapan.beneficiojoven.viewmodel.cupondetalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.qrpost.QrRequest
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CuponDetalleViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    private val _idCanje = MutableStateFlow<Int?>(null)
    val idCanje: StateFlow<Int?> = _idCanje

    fun generarQr(idUsuario: Int, idPromocion: Int) {
        if (_isLoading.value) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.qrApi.generarQr(QrRequest(idUsuario, idPromocion))
                if (response.isSuccessful) {
                    val idCanjeResponse = response.body()?.idCanje
                    if (idCanjeResponse != null) {
                        _idCanje.value = idCanjeResponse
                        _snackbarMessage.value = "✅ Cupón generado con éxito."
                    } else {
                        _snackbarMessage.value = "No se recibió idCanje del servidor."
                    }
                } else {
                    _snackbarMessage.value = "Error del servidor al generar QR."
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al generar QR: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun clearIdCanje() {
        _idCanje.value = null
    }
}