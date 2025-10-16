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
import androidx.compose.ui.graphics.Color
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

    // ---------- Estados de formulario ----------
    var idNegocio by rememberSaveable { mutableStateOf("") }
    var titulo by rememberSaveable { mutableStateOf("") }          // mapea a "nombre"
    var descripcion by rememberSaveable { mutableStateOf("") }

    val tipos = listOf("PERCENT", "FIXED_AMOUNT")
    var discountTypeExpanded by remember { mutableStateOf(false) }
    var discountType by rememberSaveable { mutableStateOf(tipos.first()) }

    var discountValue by rememberSaveable { mutableStateOf("") }   // valor numérico (porcentaje o precio)

    // Fechas ISO (YYYY-MM-DD) del DateRangePicker
    var startDate by rememberSaveable { mutableStateOf("") }       // fecha_inicio
    var endDate by rememberSaveable { mutableStateOf("") }         // fecha_fin

    // Activo (siempre true por defecto)
    var activo by rememberSaveable { mutableStateOf(true) }

    // ---------- Validaciones ----------
    val idNegocioInt = idNegocio.toIntOrNull()
    val discountValueInt = discountValue.toIntOrNull()
    val fechaRegex = Regex("""\d{4}-\d{2}-\d{2}""")

    val esValido =
        idNegocioInt != null &&
                titulo.isNotBlank() &&
                descripcion.isNotBlank() &&
                discountValueInt != null && discountValueInt > 0 &&
                startDate.matches(fechaRegex) &&
                endDate.matches(fechaRegex)

    // Colores/shape pill para el dropdown, a juego con AppTextField
    val pillColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFDDF7F7),
        unfocusedContainerColor = Color(0xFFDDF7F7),
        disabledContainerColor = Color(0xFFE6E6E6),
        focusedBorderColor = Color(0x33000000),
        unfocusedBorderColor = Color(0x33000000),
        disabledBorderColor = Color(0x33000000),
        cursorColor = MaterialTheme.colorScheme.primary
    )
    val pillShape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .safeDrawingPadding()
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

        AppTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = "Descripción",
            singleLine = false
        )

        Spacer(Modifier.height(10.dp))

        // --------- Dropdown: tipo de descuento ----------
        ExposedDropdownMenuBox(
            expanded = discountTypeExpanded,
            onExpandedChange = { discountTypeExpanded = !discountTypeExpanded }
        ) {
            OutlinedTextField(
                value = discountType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de descuento") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = discountTypeExpanded) },
                colors = pillColors,
                shape = pillShape,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            )

            ExposedDropdownMenu(
                expanded = discountTypeExpanded,
                onDismissRequest = { discountTypeExpanded = false }
            ) {
                tipos.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            discountType = option
                            discountTypeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        AppTextField(
            value = discountValue,
            onValueChange = { discountValue = it.filter(Char::isDigit) },
            label = if (discountType == "PERCENT") "Valor (%)" else "Valor (MXN)",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = discountValue.isNotBlank() && (discountValueInt == null || discountValueInt <= 0),
            supportingText = if (discountType == "PERCENT")
                "Ej: 50 = 50% (se enviará porcentaje=50, precio=0)"
            else
                "Ej: 100 = $100 MXN (se enviará precio=100, porcentaje=0)"
        )

        Spacer(Modifier.height(10.dp))

        // --------- Selector de rango de fechas (calendario) ----------
        AppDateRangeField(
            onChange = { startIso, endIso ->
                startDate = startIso
                endDate = endIso
            },
            modifier = Modifier.fillMaxWidth(),
            label = "Seleccionar fecha"
        )

        if (startDate.isNotBlank() || endDate.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text("Inicio: $startDate   •   Fin: $endDate", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(10.dp))

        // --------- Switch "Activo" ----------
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Activa")
            Switch(checked = activo, onCheckedChange = { activo = it })
        }

        Spacer(Modifier.height(16.dp))

        AppPrimaryButton(
            text = if (ui.value.isLoading) "Enviando..." else "Enviar promoción",
            enabled = esValido && !ui.value.isLoading,
            onClick = {
                focus.clearFocus()

                // Convertimos a porcentaje/precio según el tipo seleccionado
                val porcentaje = if (discountType == "PERCENT") (discountValueInt ?: 0) else 0
                val precio = if (discountType == "FIXED_AMOUNT") (discountValueInt ?: 0) else 0

                val payload = PromotionRequest(
                    idNegocio = idNegocioInt!!,
                    nombre = titulo.trim(),
                    descripcion = descripcion.trim(),
                    fechaInicio = startDate.trim(),   // "YYYY-MM-DD"
                    fechaFin = endDate.trim(),        // "YYYY-MM-DD"
                    porcentaje = porcentaje,          // 0 si no aplica
                    precio = precio,                  // 0 si no aplica
                    activo = activo
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
            // Muestra lo que sea que tengas en success (String, objeto, etc.)
            Text("Respuesta del servidor: $successAny")
        }

        ui.value.error?.let { err ->
            Text("✖ Error: $err", color = MaterialTheme.colorScheme.error)
        }

        ui.value.error?.let {
            Text("✖ Error: $it", color = MaterialTheme.colorScheme.error)
        }

        if (!esValido) {
            Spacer(Modifier.height(8.dp))
            AssistiveValidationHint(
                idOk = idNegocioInt != null,
                titleOk = titulo.isNotBlank(),
                descOk = descripcion.isNotBlank(),
                valueOk = discountValueInt != null && discountValueInt > 0,
                startOk = startDate.matches(fechaRegex),
                endOk = endDate.matches(fechaRegex)
            )
        }
    }
}

@Composable
private fun AssistiveValidationHint(
    idOk: Boolean,
    titleOk: Boolean,
    descOk: Boolean,
    valueOk: Boolean,
    startOk: Boolean,
    endOk: Boolean
) {
    val pendientes = buildList {
        if (!idOk) add("• ID del negocio (número)")
        if (!titleOk) add("• Título (nombre)")
        if (!descOk) add("• Descripción")
        if (!valueOk) add("• Valor del descuento (> 0)")
        if (!startOk) add("• Fecha inicio con formato YYYY-MM-DD")
        if (!endOk) add("• Fecha fin con formato YYYY-MM-DD")
    }
    if (pendientes.isNotEmpty()) {
        Text(
            "Completa/valida: \n${pendientes.joinToString("\n")}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

