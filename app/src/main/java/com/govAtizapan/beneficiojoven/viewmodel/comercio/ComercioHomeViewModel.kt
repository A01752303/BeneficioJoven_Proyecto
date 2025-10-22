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
