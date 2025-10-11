package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.govAtizapan.beneficiojoven.model.PromotionRequest
import com.govAtizapan.beneficiojoven.viewmodel.CreatePromotionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePromotionScreen(vm: CreatePromotionViewModel = viewModel()) {
    val ui = vm.ui.collectAsState()
    val focus = LocalFocusManager.current

    // ---------- Estados de los campos ----------
    var idNegocio by rememberSaveable { mutableStateOf("") }
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }

    val tipos = listOf("PERCENT", "FIXED_AMOUNT")
    var discountTypeExpanded by remember { mutableStateOf(false) }
    var discountType by rememberSaveable { mutableStateOf(tipos.first()) }

    var discountValue by rememberSaveable { mutableStateOf("") }

    // Formato YYYY-MM-DD (simple; si quieres, después integramos DatePicker)
    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }

    var onlineOnly by rememberSaveable { mutableStateOf(false) }
    var isStackable by rememberSaveable { mutableStateOf(false) }
    var terms by rememberSaveable { mutableStateOf("") }

    // ---------- Validaciones simples ----------
    val idNegocioInt = idNegocio.toIntOrNull()
    val discountValueInt = discountValue.toIntOrNull()
    val fechaRegex = Regex("""\d{4}-\d{2}-\d{2}""")

    val esValido =
        idNegocioInt != null &&
                !titulo.isBlank() &&
                !descripcion.isBlank() &&
                discountValueInt != null &&
                startDate.matches(fechaRegex) &&
                endDate.matches(fechaRegex) &&
                !terms.isBlank()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Crear promoción", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = idNegocio,
            onValueChange = { idNegocio = it.filter { c -> c.isDigit() } },
            label = { Text("ID del negocio") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = idNegocio.isNotBlank() && idNegocioInt == null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            minLines = 3,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // ----- Dropdown: discount_type -----
        ExposedDropdownMenuBox(
            expanded = discountTypeExpanded,
            onExpandedChange = { discountTypeExpanded = !discountTypeExpanded }
        ) {
            OutlinedTextField(
                value = discountType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de descuento") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = discountTypeExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
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
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = discountValue,
            onValueChange = { discountValue = it.filter { c -> c.isDigit() } },
            label = { Text("Valor del descuento") },
            supportingText = {
                Text(
                    if (discountType == "PERCENT") "Ej: 50 = 50%" else "Ej: 100 = $100 MXN"
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = discountValue.isNotBlank() && discountValueInt == null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Fecha inicio (YYYY-MM-DD)") },
            singleLine = true,
            isError = startDate.isNotBlank() && !startDate.matches(fechaRegex),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("Fecha fin (YYYY-MM-DD)") },
            singleLine = true,
            isError = endDate.isNotBlank() && !endDate.matches(fechaRegex),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // ----- Switches -----
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Solo en línea")
            Switch(checked = onlineOnly, onCheckedChange = { onlineOnly = it })
        }

        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Acumulable (stackable)")
            Switch(checked = isStackable, onCheckedChange = { isStackable = it })
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = terms,
            onValueChange = { terms = it },
            label = { Text("Términos") },
            minLines = 2,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            enabled = esValido && !ui.value.isLoading,
            onClick = {
                // Oculta teclado
                focus.clearFocus()

                // Construye el JSON con lo escrito por el usuario
                val payload = PromotionRequest(
                    idNegocio = idNegocioInt!!,
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim(),
                    discountType = discountType,
                    discountValue = discountValueInt!!,
                    startDate = startDate.trim(),
                    endDate = endDate.trim(),
                    onlineOnly = onlineOnly,
                    isStackable = isStackable,
                    terms = terms.trim()
                )

                vm.createPromotion(payload)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (ui.value.isLoading) "Enviando..." else "Enviar promoción")
        }

        Spacer(Modifier.height(12.dp))

        // ----- Feedback de la llamada -----
        ui.value.success?.let {
            Text("✔ Enviado correctamente.", color = MaterialTheme.colorScheme.primary)
            it.id?.let { id -> Text("ID creado: $id") }
            it.status?.let { s -> Text("Status: $s") }
            it.message?.let { m -> Text("Mensaje: $m") }
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
                valueOk = discountValueInt != null,
                startOk = startDate.matches(fechaRegex),
                endOk = endDate.matches(fechaRegex),
                termsOk = terms.isNotBlank()
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
    endOk: Boolean,
    termsOk: Boolean
) {
    val pendientes = buildList {
        if (!idOk) add("• ID del negocio (número)")
        if (!titleOk) add("• Título")
        if (!descOk) add("• Descripción")
        if (!valueOk) add("• Valor del descuento (número)")
        if (!startOk) add("• Fecha inicio con formato YYYY-MM-DD")
        if (!endOk) add("• Fecha fin con formato YYYY-MM-DD")
        if (!termsOk) add("• Términos")
    }
    if (pendientes.isNotEmpty()) {
        Text(
            "Completa/valida: \n${pendientes.joinToString("\n")}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
