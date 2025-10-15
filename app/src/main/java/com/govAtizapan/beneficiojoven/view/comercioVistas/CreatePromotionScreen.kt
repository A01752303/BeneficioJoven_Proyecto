package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionRequest
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppDateRangeField
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppPrimaryButton
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppTextField
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppThinDividerWithDot
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePromotionScreen(vm: CreatePromotionViewModel = viewModel()) {
    val ui = vm.ui.collectAsState()
    val focus = LocalFocusManager.current

    // ---------- Estados ----------
    var idNegocio by rememberSaveable { mutableStateOf("") }
    var titulo by rememberSaveable { mutableStateOf("") }             // mapea a "nombre"
    var descripcion by rememberSaveable { mutableStateOf("") }

    // Tipos de promoción
    val tiposPromo = listOf("Descuento (%)", "Precio fijo (MXN)", "2x1", "Trae un amigo", "Otra")
    var tipoExpanded by remember { mutableStateOf(false) }
    var tipoSeleccionado by rememberSaveable { mutableStateOf(tiposPromo.first()) }
    val esDescuento = tipoSeleccionado == "Descuento (%)"
    val esPrecioFijo = tipoSeleccionado == "Precio fijo (MXN)"

    // Valores numéricos según tipo
    var porcentajeTxt by rememberSaveable { mutableStateOf("") }      // 1..100 cuando es descuento
    var precioTxt by rememberSaveable { mutableStateOf("") }          // >0 cuando es precio fijo

    // Límites (opcionales; 0 = sin límite)
    var limiteTotalTxt by rememberSaveable { mutableStateOf("") }
    var limitePorUsuarioTxt by rememberSaveable { mutableStateOf("") }

    // Fechas ISO (YYYY-MM-DD)
    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }

    // ---------- Validaciones ----------
    val idNegocioInt = idNegocio.toIntOrNull()
    val porcentajeInt = porcentajeTxt.toIntOrNull()
    val precioInt = precioTxt.toIntOrNull()
    val limiteTotalInt = limiteTotalTxt.toIntOrNull() ?: 0
    val limitePorUsuarioInt = limitePorUsuarioTxt.toIntOrNull() ?: 0

    val fechaRegex = Regex("""\d{4}-\d{2}-\d{2}""")

    val porcentajeOk = if (esDescuento) (porcentajeInt != null && porcentajeInt in 1..100) else true
    val precioOk = if (esPrecioFijo) (precioInt != null && precioInt > 0) else true
    val limiteTotalOk = limiteTotalTxt.isBlank() || (limiteTotalInt >= 0)
    val limiteUsuarioOk = limitePorUsuarioTxt.isBlank() || (limitePorUsuarioInt >= 0)

    val esValido =
        idNegocioInt != null &&
                titulo.isNotBlank() &&
                descripcion.isNotBlank() &&
                porcentajeOk &&
                precioOk &&
                limiteTotalOk &&
                limiteUsuarioOk &&
                startDate.matches(fechaRegex) &&
                endDate.matches(fechaRegex)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Crear promoción", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        AppTextField(
            value = idNegocio,
            onValueChange = { idNegocio = it.filter(Char::isDigit) },
            label = "ID del negocio",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = idNegocio.isNotBlank() && idNegocioInt == null
        )

        Spacer(Modifier.height(10.dp))

        AppTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = "Título (nombre)",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(Modifier.height(10.dp))

        // --------- Selector: Tipo de promoción ----------
        ExposedDropdownMenuBox(
            expanded = tipoExpanded,
            onExpandedChange = { tipoExpanded = !tipoExpanded }
        ) {
            OutlinedTextField(
                value = tipoSeleccionado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de promoción") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            )
            ExposedDropdownMenu(
                expanded = tipoExpanded,
                onDismissRequest = { tipoExpanded = false }
            ) {
                tiposPromo.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            tipoSeleccionado = option
                            tipoExpanded = false
                            when (option) {
                                "Descuento (%)" -> { precioTxt = "" }
                                "Precio fijo (MXN)" -> { porcentajeTxt = "" }
                                else -> { porcentajeTxt = ""; precioTxt = "" }
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Campos condicionales (según tipo)
        if (esDescuento) {
            AppTextField(
                value = porcentajeTxt,
                onValueChange = { porcentajeTxt = it.filter(Char::isDigit) },
                label = "Porcentaje (%)",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = porcentajeTxt.isNotBlank() && !(porcentajeInt?.let { it in 1..100 } == true),
                supportingText = "1 a 100. Se enviará porcentaje=<valor> y precio=0"
            )
            Spacer(Modifier.height(10.dp))
        } else if (esPrecioFijo) {
            AppTextField(
                value = precioTxt,
                onValueChange = { precioTxt = it.filter(Char::isDigit) },
                label = "Precio fijo (MXN)",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = precioTxt.isNotBlank() && !(precioInt?.let { it > 0 } == true),
                supportingText = "Monto en MXN. Se enviará precio=<valor> y porcentaje=0"
            )
            Spacer(Modifier.height(10.dp))
        }

        AppTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = when {
                esDescuento -> "Descripción"
                esPrecioFijo -> "Descripción"
                else -> "Descripción / Mecánica (2x1, trae un amigo, etc.)"
            },
            singleLine = false
        )

        Spacer(Modifier.height(10.dp))

        // Límites (opcionales; 0 = sin límite)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) {
                AppTextField(
                    value = limiteTotalTxt,
                    onValueChange = { limiteTotalTxt = it.filter(Char::isDigit) },
                    label = "Límite total (opcional)",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = limiteTotalTxt.isNotBlank() && limiteTotalInt < 0,
                    supportingText = "0 o vacío = sin límite"
                )
            }
            Column(Modifier.weight(1f)) {
                AppTextField(
                    value = limitePorUsuarioTxt,
                    onValueChange = { limitePorUsuarioTxt = it.filter(Char::isDigit) },
                    label = "Límite por usuario (opcional)",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = limitePorUsuarioTxt.isNotBlank() && limitePorUsuarioInt < 0,
                    supportingText = "0 o vacío = sin límite"
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // --------- Rango de fechas ----------
        AppDateRangeField(
            onChange = { startIso, endIso ->
                startDate = startIso
                endDate = endIso
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Rango de fechas (YYYY-MM-DD)"
        )

        if (startDate.isNotBlank() || endDate.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text("Inicio: $startDate   •   Fin: $endDate", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))

        AppPrimaryButton(
            text = if (ui.value.isLoading) "Enviando..." else "Enviar promoción",
            enabled = esValido && !ui.value.isLoading,
            onClick = {
                focus.clearFocus()

                // Valores según tipo:
                // - Descuento: porcentaje = valor, precio = 0
                // - Precio fijo: precio = valor, porcentaje = 0
                // - Otros: ambos 0
                val porcentaje = when {
                    esDescuento -> (porcentajeInt ?: 0)
                    else -> 0
                }
                val precio = when {
                    esPrecioFijo -> (precioInt ?: 0)
                    else -> 0
                }

                // Prefijo para mecánicas que no son % ni precio fijo (2x1, trae un amigo, etc.)
                val descripcionConTipo =
                    if (!esDescuento && !esPrecioFijo && descripcion.isNotBlank())
                        "${tipoSeleccionado}: ${descripcion.trim()}"
                    else
                        descripcion.trim()

                val payload = PromotionRequest(
                    idNegocio = idNegocioInt!!,
                    nombre = titulo.trim(),
                    descripcion = descripcionConTipo,
                    fechaInicio = startDate.trim(),      // "YYYY-MM-DD"
                    fechaFin = endDate.trim(),           // "YYYY-MM-DD"
                    porcentaje = porcentaje,             // 0 cuando no aplique
                    precio = precio,                     // 0 cuando no aplique
                    activo = true,                       // siempre true
                    limiteTotal = limiteTotalInt,        // 0 = sin límite
                    limitePorUsuario = limitePorUsuarioInt // 0 = sin límite
                )

                vm.createPromotion(payload)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))
        AppThinDividerWithDot()

        Spacer(Modifier.height(12.dp))

        // --------- Feedback de la llamada ----------
        val successAny = ui.value.success
        if (successAny != null) {
            Text("✔ Enviado correctamente.", color = MaterialTheme.colorScheme.primary)
            Text("Respuesta del servidor: $successAny")
        }
        ui.value.error?.let { err ->
            Text("✖ Error: $err", color = MaterialTheme.colorScheme.error)
        }

        if (!esValido) {
            Spacer(Modifier.height(8.dp))
            AssistiveValidationHint(
                idOk = idNegocioInt != null,
                titleOk = titulo.isNotBlank(),
                descOk = descripcion.isNotBlank(),
                tipoOk = porcentajeOk && precioOk,
                startOk = startDate.matches(fechaRegex),
                endOk = endDate.matches(fechaRegex),
                limTotalOk = limiteTotalOk,
                limUserOk = limiteUsuarioOk
            )
        }
    }
}

@Composable
private fun AssistiveValidationHint(
    idOk: Boolean,
    titleOk: Boolean,
    descOk: Boolean,
    tipoOk: Boolean,
    startOk: Boolean,
    endOk: Boolean,
    limTotalOk: Boolean,
    limUserOk: Boolean
) {
    val pendientes = buildList {
        if (!idOk) add("• ID del negocio (número)")
        if (!titleOk) add("• Título (nombre)")
        if (!descOk) add("• Descripción / Mecánica")
        if (!tipoOk) add("• Completa el valor del tipo elegido (porcentaje 1..100 o precio > 0)")
        if (!startOk) add("• Fecha inicio con formato YYYY-MM-DD")
        if (!endOk) add("• Fecha fin con formato YYYY-MM-DD")
        if (!limTotalOk) add("• Límite total debe ser ≥ 0")
        if (!limUserOk) add("• Límite por usuario debe ser ≥ 0")
    }
    if (pendientes.isNotEmpty()) {
        Text(
            "Completa/valida: \n${pendientes.joinToString("\n")}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
