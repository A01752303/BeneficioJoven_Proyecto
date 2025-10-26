/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define el ViewModel `ComercioHomeViewModel` y el estado de UI
 * asociado `ComercioHomeUiState`, usados por la pantalla principal del módulo
 * de comercio (ComercioHome).
 *
 * `ComercioHomeViewModel`:
 * * Se encarga de obtener las promociones del comercio autenticado usando
 * `ComercioPromotionsRepository`, el cual a su vez llama a la API remota
 * vía `RetrofitClient.comercioPromotionsApi`.
 * * Expone un `StateFlow<ComercioHomeUiState>` con:
 * * `isLoading`: indicador de carga.
 * * `promociones`: lista lista para UI (`ComercioPromotion`).
 * * `error`: mensaje de error si la petición falla.
 * * `lastRefreshedMillis`: marca de tiempo del último fetch exitoso.
 * * Provee las funciones `cargarPromociones()` y `refrescar()` para controlar
 * el ciclo de carga desde la vista.
 *
 * `ComercioHomeUiState`:
 * * Modelo inmutable que representa el estado actual de la pantalla,
 * incluyendo datos, loading y errores.
 *
 * Este ViewModel se inicializa cargando automáticamente las promociones
 * en `init { cargarPromociones() }`, y usa `viewModelScope.launch` para
 * ejecutar la llamada de red en corrutina.
 */





package com.govAtizapan.beneficiojoven.viewmodel.comercio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.comercio.ComercioPromotion
import com.govAtizapan.beneficiojoven.model.comercio.ComercioPromotionsRepository
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la Home de Comercios:
 * - Carga y expone las promociones del comercio autenticado.
 * - Maneja estados: loading / data / error.
 */
class ComercioHomeViewModel(
    private val repository: ComercioPromotionsRepository = ComercioPromotionsRepository(
        api = RetrofitClient.comercioPromotionsApi
    )
) : ViewModel() {

    private val _ui = MutableStateFlow(ComercioHomeUiState())
    val ui: StateFlow<ComercioHomeUiState> = _ui

    init {
        cargarPromociones()
    }

    fun cargarPromociones() {
        _ui.value = _ui.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = repository.fetchMisPromociones()
            _ui.value = result.fold(
                onSuccess = { list ->
                    _ui.value.copy(
                        isLoading = false,
                        error = null,
                        promociones = list,
                        lastRefreshedMillis = System.currentTimeMillis()
                    )
                },
                onFailure = { err ->
                    _ui.value.copy(
                        isLoading = false,
                        error = err.message ?: "Error al obtener promociones"
                    )
                }
            )
        }
    }

    fun refrescar() = cargarPromociones()

}

data class ComercioHomeUiState(
    val isLoading: Boolean = false,
    val promociones: List<ComercioPromotion> = emptyList(),
    val error: String? = null,
    val lastRefreshedMillis: Long? = null
)
