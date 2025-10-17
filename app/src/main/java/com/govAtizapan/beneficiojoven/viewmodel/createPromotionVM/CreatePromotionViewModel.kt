package com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreatePromotionViewModel : ViewModel() {

    private val _ui = MutableStateFlow(CreatePromotionUiState())
    val ui: StateFlow<CreatePromotionUiState> = _ui

    fun onEvent(ev: CreatePromotionEvent) {
        when (ev) {
            is CreatePromotionEvent.IdNegocioChanged ->
                update { copy(idNegocio = ev.value.filter(Char::isDigit)) }
            is CreatePromotionEvent.TituloChanged ->
                update { copy(titulo = ev.value) }
            is CreatePromotionEvent.DescripcionChanged ->
                update { copy(descripcion = ev.value) }
            is CreatePromotionEvent.TipoChanged -> update {
                when (ev.value) {
                    PromotionType.DESCUENTO   -> copy(tipo = ev.value, precioTxt = "")
                    PromotionType.PRECIO_FIJO -> copy(tipo = ev.value, porcentajeTxt = "")
                    else -> copy(tipo = ev.value, porcentajeTxt = "", precioTxt = "")
                }
            }
            is CreatePromotionEvent.PorcentajeChanged ->
                update { copy(porcentajeTxt = ev.value.filter(Char::isDigit)) }
            is CreatePromotionEvent.PrecioChanged ->
                update { copy(precioTxt = ev.value.filter(Char::isDigit)) }
            is CreatePromotionEvent.StartEndChanged ->
                update { copy(startDate = ev.startIso, endDate = ev.endIso) }
            is CreatePromotionEvent.LimiteTotalChanged ->
                update { copy(limiteTotalTxt = ev.value.filter(Char::isDigit)) }
            is CreatePromotionEvent.LimitePorUsuarioChanged ->
                update { copy(limitePorUsuarioTxt = ev.value.filter(Char::isDigit)) }
            CreatePromotionEvent.Submit -> submit()
            CreatePromotionEvent.ClearForm -> clearForm()
            CreatePromotionEvent.ConsumeMessages ->
                update { copy(successMessage = null, errorMessage = null) }
        }
        validate()
    }

    private fun update(block: CreatePromotionUiState.() -> CreatePromotionUiState) {
        _ui.value = _ui.value.block()
    }

    private fun validate() {
        val s = _ui.value
        val idOk = s.idNegocio.toIntOrNull() != null
        val titleOk = s.titulo.isNotBlank()
        val descOk = s.descripcion.isNotBlank()
        val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
        val datesOk = s.startDate.matches(dateRegex) && s.endDate.matches(dateRegex)

        val pctOk = when (s.tipo) {
            PromotionType.DESCUENTO -> s.porcentajeTxt.toIntOrNull()?.let { it in 1..100 } == true
            else -> true
        }
        val priceOk = when (s.tipo) {
            PromotionType.PRECIO_FIJO -> s.precioTxt.toIntOrNull()?.let { it > 0 } == true
            else -> true
        }

        val limTotal = s.limiteTotalTxt.toIntOrNull() ?: 0
        val limUser  = s.limitePorUsuarioTxt.toIntOrNull() ?: 0
        val limRelOk = !((limTotal > 0) && (limUser > 0) && (limUser > limTotal))

        val valid = idOk && titleOk && descOk && datesOk && pctOk && priceOk && limRelOk
        _ui.value = _ui.value.copy(isValid = valid)
    }

    private fun clearForm() {
        _ui.value = _ui.value.copy(
            idNegocio = "",
            titulo = "",
            descripcion = "",
            tipo = PromotionType.DESCUENTO,
            porcentajeTxt = "",
            precioTxt = "",
            startDate = "",
            endDate = "",
            limiteTotalTxt = "",
            limitePorUsuarioTxt = "",
            isValid = false
        )
    }

    private fun submit() {
        val s = _ui.value
        if (!s.isValid) return

        val idNegocio = s.idNegocio.toInt()
        val porcentaje = when (s.tipo) {
            PromotionType.DESCUENTO -> s.porcentajeTxt.toIntOrNull() ?: 0
            else -> 0
        }
        val precio = when (s.tipo) {
            PromotionType.PRECIO_FIJO -> s.precioTxt.toIntOrNull() ?: 0
            else -> 0
        }
        val desc = when (s.tipo) {
            PromotionType.DESCUENTO, PromotionType.PRECIO_FIJO -> s.descripcion.trim()
            PromotionType.DOSxUNO -> "2x1: ${s.descripcion.trim()}"
            PromotionType.TRAE_AMIGO -> "Trae un amigo: ${s.descripcion.trim()}"
            PromotionType.OTRA -> "Otra: ${s.descripcion.trim()}"
        }

        val body = com.govAtizapan.beneficiojoven.model.promotionpost.PromotionRequest(
            idNegocio = idNegocio,
            nombre = s.titulo.trim(),
            descripcion = desc,
            fechaInicio = s.startDate.trim(),
            fechaFin = s.endDate.trim(),
            porcentaje = porcentaje,
            precio = precio,
            activo = true,
            limiteTotal = s.limiteTotalTxt.toIntOrNull() ?: 0,
            limitePorUsuario = s.limitePorUsuarioTxt.toIntOrNull() ?: 0
        )

        viewModelScope.launch {
            update { copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                val resp = RetrofitClient.promotionsApi.crearPromocion(body)
                if (resp.isSuccessful) {
                    update { copy(isLoading = false, successMessage = "Promoción creada.") }
                } else {
                    update { copy(isLoading = false, errorMessage = "HTTP ${resp.code()}") }
                }
            } catch (t: Throwable) {
                update { copy(isLoading = false, errorMessage = t.message ?: "Error desconocido") }
            }
        }
    }
}
