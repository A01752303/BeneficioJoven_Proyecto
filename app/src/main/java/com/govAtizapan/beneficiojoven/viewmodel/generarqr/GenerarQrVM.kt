package com.govAtizapan.beneficiojoven.viewmodel.generarqr


import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.view.home.generadorQR.QRCodeGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GenerarQRViewModel : ViewModel() {

    private val _qrBitmap = MutableStateFlow<Bitmap?>(null)
    val qrBitmap: StateFlow<Bitmap?> = _qrBitmap

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun generarQR(idCanje: Int?) {
        if (idCanje == null) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val bitmap = QRCodeGenerator.generarQR("CANJE-$idCanje")
                _qrBitmap.value = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                _qrBitmap.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}