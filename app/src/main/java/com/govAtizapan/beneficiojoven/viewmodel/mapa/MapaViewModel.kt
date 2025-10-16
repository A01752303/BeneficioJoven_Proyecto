package com.govAtizapan.beneficiojoven.viewmodel.mapa

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.android.gms.location.LocationServices

class MapaViewModel(private val context: Context) : ViewModel() {

    private val _ubicacionActual = MutableStateFlow<LatLng?>(null)
    val ubicacionActual: StateFlow<LatLng?> = _ubicacionActual.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val location: LatLng?) : UiState()
        data class Error(val message: String) : UiState()
    }

    init {
        cargarUbicacionActual()
    }

    public fun cargarUbicacionActual() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            viewModelScope.launch {
                try {
                    val locationResult = fusedLocationClient.lastLocation
                    locationResult.addOnSuccessListener { location ->
                        if (location != null) {
                            _ubicacionActual.value = LatLng(location.latitude, location.longitude)
                            _uiState.value = UiState.Success(LatLng(location.latitude, location.longitude))
                        } else {
                            _ubicacionActual.value = LatLng(19.557, -99.269) // Coordenadas por defecto (Ciudad de México)
                            _uiState.value = UiState.Success(LatLng(19.557, -99.269))
                        }
                    }
                } catch (e: SecurityException) {
                    _uiState.value = UiState.Error("Permiso de ubicación denegado: ${e.message}")
                } catch (e: Exception) {
                    _uiState.value = UiState.Error("Error al cargar ubicación: ${e.message}")
                }
            }
        } else {
            _uiState.value = UiState.Error("Permiso de ubicación no otorgado")
        }
    }
}