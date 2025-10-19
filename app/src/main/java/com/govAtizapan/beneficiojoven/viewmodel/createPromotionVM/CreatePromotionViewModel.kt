package com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import androidx.core.net.toUri

class CreatePromotionViewModel : ViewModel() {

    private val _ui = MutableStateFlow(CreatePromotionUiState())
    val ui: StateFlow<CreatePromotionUiState> = _ui

    // ==========================
    // Eventos
    // ==========================
    fun onEvent(ev: CreatePromotionEvent) {
        when (ev) {
            is CreatePromotionEvent.TituloChanged ->
                update { copy(titulo = ev.value) }

            is CreatePromotionEvent.DescripcionChanged ->
                update { copy(descripcion = ev.value) }

            is CreatePromotionEvent.TipoChanged ->
                update {
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

            is CreatePromotionEvent.ImagenSeleccionada ->
                update { copy(imagenUri = ev.uriString) }

            CreatePromotionEvent.QuitarImagen ->
                update { copy(imagenUri = null) }

            is CreatePromotionEvent.SetContentResolver ->
                update { copy(contentResolver = ev.resolver) }

            CreatePromotionEvent.Submit -> submitMultipart()
            CreatePromotionEvent.ClearForm -> clearForm()
            CreatePromotionEvent.ConsumeMessages ->
                update { copy(successMessage = null, errorMessage = null) }
        }
        validate()
    }

    // ==========================
    // Helpers de estado
    // ==========================
    private fun update(block: CreatePromotionUiState.() -> CreatePromotionUiState) {
        _ui.value = _ui.value.block()
    }

    private fun validate() {
        val s = _ui.value

        val titleOk = s.titulo.isNotBlank()
        val descOk = s.descripcion.isNotBlank()

        val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
        val datesOk = s.startDate.matches(dateRegex) &&
                s.endDate.matches(dateRegex) &&
                s.startDate <= s.endDate

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

        _ui.value = s.copy(
            isValid = titleOk && descOk && datesOk && pctOk && priceOk && limRelOk
        )
    }

    private fun clearForm() {
        _ui.value = _ui.value.copy(
            titulo = "",
            descripcion = "",
            tipo = PromotionType.DESCUENTO,
            porcentajeTxt = "",
            precioTxt = "",
            startDate = "",
            endDate = "",
            limiteTotalTxt = "",
            limitePorUsuarioTxt = "",
            imagenUri = null,
            isValid = false
        )
    }

    // ==========================
    //  SUBMIT MULTIPART
    // ==========================
    private fun submitMultipart() {
        val s = _ui.value
        if (!s.isValid) return

        viewModelScope.launch {
            // Validar sesión
            if (SessionManager.accessToken.isNullOrBlank()) {
                update { copy(errorMessage = "No hay token de sesión. Inicia sesión de nuevo.") }
                return@launch
            }

            // Normalización
            val porcentaje = if (s.tipo == PromotionType.DESCUENTO)
                s.porcentajeTxt.toIntOrNull() ?: 0 else 0

            val precio = if (s.tipo == PromotionType.PRECIO_FIJO)
                s.precioTxt.toIntOrNull() ?: 0 else 0

            val descripcion = when (s.tipo) {
                PromotionType.DESCUENTO, PromotionType.PRECIO_FIJO -> s.descripcion.trim()
                PromotionType.DOSxUNO    -> "2x1: ${s.descripcion.trim()}"
                PromotionType.TRAE_AMIGO -> "Trae un amigo: ${s.descripcion.trim()}"
                PromotionType.OTRA       -> "Otra: ${s.descripcion.trim()}"
            }

            // Partes de texto
            val parts = hashMapOf<String, RequestBody>().apply {
                put("nombre",              s.titulo.trim().toPlain())
                put("descripcion",         descripcion.toPlain())
                put("fecha_inicio",        s.startDate.trim().toPlain())
                put("fecha_fin",           s.endDate.trim().toPlain())
                put("porcentaje",          porcentaje.toString().toPlain())
                put("precio",              precio.toString().toPlain())
                put("activo",              true.toString().toPlain())
                put("limite_total",        (s.limiteTotalTxt.toIntOrNull() ?: 0).toString().toPlain())
                put("limite_por_usuario",  (s.limitePorUsuarioTxt.toIntOrNull() ?: 0).toString().toPlain())
            }

            // Imagen opcional
            val imagePart: MultipartBody.Part? = try {
                s.imagenUri?.let { uriString ->
                    buildImagePartFromUriString(uriString, s.contentResolver)
                }
            } catch (_: Throwable) { null }

            update { copy(isLoading = true, errorMessage = null, successMessage = null) }

            try {
                val resp = RetrofitClient.promotionsApi.crearPromocionMultipart(parts, imagePart)
                if (resp.isSuccessful) {
                    val msg = resp.body()?.message ?: "Promoción creada."
                    update { copy(isLoading = false, successMessage = msg) }
                    clearForm()
                } else {
                    update {
                        copy(
                            isLoading = false,
                            errorMessage = "Error ${resp.code()}: ${resp.errorBody()?.string() ?: "Error al crear la promoción"}"
                        )
                    }
                }
            } catch (t: Throwable) {
                update { copy(isLoading = false, errorMessage = t.message ?: "Error de red") }
            }
        }
    }

    // ===========
    //  HELPERS
    // ===========
    private fun String.toPlain(): RequestBody =
        this.toRequestBody("text/plain".toMediaType())

    /**
     * Construye la parte de la imagen desde un uriString usando ContentResolver.
     * @param fieldName nombre del campo que espera el backend (por defecto "imagen").
     */
    private fun buildImagePartFromUriString(
        uriString: String,
        resolver: ContentResolver?,
        fieldName: String = "file"   // 👈 ahora el nombre del campo es "file"
    ): MultipartBody.Part? {
        if (resolver == null) return null
        val uri = uriString.toUri()

        val mime = resolver.getType(uri) ?: "image/*"
        val input = resolver.openInputStream(uri) ?: return null

        // Copiamos a un archivo temporal (MultipartBody necesita File/RequestBody)
        val temp = File.createTempFile("promo_", ".tmp")
        FileOutputStream(temp).use { out -> input.copyTo(out) }

        val body = temp.asRequestBody(mime.toMediaType())
        return MultipartBody.Part.createFormData(
            name = fieldName,  // 👈 será "file"
            filename = temp.name,
            body = body
        )
    }
}
