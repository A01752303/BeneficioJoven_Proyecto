package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppDateRangeField
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppPrimaryButton
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppStandardTextField
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppThinDividerWithDot
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionEvent
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel
import androidx.compose.material3.ExposedDropdownMenuAnchorType

@Composable
fun CreatePromotionScreen(
    vm: CreatePromotionViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    val focusManager = LocalFocusManager.current

    // Diálogo de éxito
    if (ui.successMessage != null) {
        AlertDialog(
            onDismissRequest = { vm.onEvent(CreatePromotionEvent.ConsumeMessages) },
            title = { Text("Promoción enviada") },
            text  = { Text(ui.successMessage ?: "Se envió correctamente.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.onEvent(CreatePromotionEvent.ClearForm)
                    vm.onEvent(CreatePromotionEvent.ConsumeMessages)
                }) { Text("Aceptar") }
            }
        )
    }
    // Diálogo de error (opcional)
    if (ui.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { vm.onEvent(CreatePromotionEvent.ConsumeMessages) },
            title = { Text("No se pudo enviar") },
            text  = { Text(ui.errorMessage ?: "Intenta de nuevo.") },
            confirmButton = {
                TextButton(onClick = { vm.onEvent(CreatePromotionEvent.ConsumeMessages) }) {
                    Text("Entendido")
                }
            }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Crear promoción", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        AppStandardTextField(
            value = ui.idNegocio,
            onValueChange = { vm.onEvent(CreatePromotionEvent.IdNegocioChanged(it)) },
            label = "ID del negocio",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = ui.idNegocio.isNotBlank() && ui.idNegocio.toIntOrNull() == null
        )
        Spacer(Modifier.height(10.dp))

        AppStandardTextField(
            value = ui.titulo,
            onValueChange = { vm.onEvent(CreatePromotionEvent.TituloChanged(it)) },
            label = "Título (nombre)"
        )
        Spacer(Modifier.height(10.dp))

        // Tipo de promoción (mismo look que los TextField)
        TipoPromocionField(
            selected = ui.tipo,
            onSelected = { vm.onEvent(CreatePromotionEvent.TipoChanged(it)) }
        )
        Spacer(Modifier.height(10.dp))

        when (ui.tipo) {
            PromotionType.DESCUENTO -> {
                AppStandardTextField(
                    value = ui.porcentajeTxt,
                    onValueChange = { vm.onEvent(CreatePromotionEvent.PorcentajeChanged(it)) },
                    label = "Porcentaje (%)",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    supportingText = "1 a 100. Se enviará porcentaje=<valor> y precio=0"
                )
                Spacer(Modifier.height(10.dp))
            }
            PromotionType.PRECIO_FIJO -> {
                AppStandardTextField(
                    value = ui.precioTxt,
                    onValueChange = { vm.onEvent(CreatePromotionEvent.PrecioChanged(it)) },
                    label = "Precio fijo (MXN)",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    supportingText = "Monto en MXN. Se enviará precio=<valor> y porcentaje=0"
                )
                Spacer(Modifier.height(10.dp))
            }
            else -> Unit
        }

        AppStandardTextField(
            value = ui.descripcion,
            onValueChange = { vm.onEvent(CreatePromotionEvent.DescripcionChanged(it)) },
            label = when (ui.tipo) {
                PromotionType.DESCUENTO, PromotionType.PRECIO_FIJO -> "Descripción"
                PromotionType.DOSxUNO -> "Descripción / Mecánica (2x1)"
                PromotionType.TRAE_AMIGO -> "Descripción / Mecánica (Trae un amigo)"
                PromotionType.OTRA -> "Descripción / Mecánica"
            },
            singleLine = false,
            minHeightDp = 96
        )

        Spacer(Modifier.height(10.dp))

        // Límites (0 o vacío = sin límite)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) {
                AppStandardTextField(
                    value = ui.limiteTotalTxt,
                    onValueChange = { vm.onEvent(CreatePromotionEvent.LimiteTotalChanged(it)) },
                    label = "Límite total (opcional)",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    supportingText = "0 o vacío = sin límite"
                )
            }
            Column(Modifier.weight(1f)) {
                AppStandardTextField(
                    value = ui.limitePorUsuarioTxt,
                    onValueChange = { vm.onEvent(CreatePromotionEvent.LimitePorUsuarioChanged(it)) },
                    label = "Límite por usuario (opcional)",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    supportingText = "0 o vacío = sin límite"
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Fecha
        AppDateRangeField(
            onChange = { start, end -> vm.onEvent(CreatePromotionEvent.StartEndChanged(start, end)) },
            label = "Rango de fechas (YYYY-MM-DD)"
        )
        if (ui.startDate.isNotBlank() || ui.endDate.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text("Inicio: ${ui.startDate} • Fin: ${ui.endDate}", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))

        AppPrimaryButton(
            text = if (ui.isLoading) "Enviando..." else "Enviar promoción",
            enabled = ui.isValid && !ui.isLoading,
            onClick = {
                focusManager.clearFocus()
                vm.onEvent(CreatePromotionEvent.Submit)
            }
        )

        Spacer(Modifier.height(12.dp))
        AppThinDividerWithDot()
        Spacer(Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TipoPromocionField(
    selected: PromotionType,
    onSelected: (PromotionType) -> Unit
) {
    val opciones = listOf(
        PromotionType.DESCUENTO to "Descuento (%)",
        PromotionType.PRECIO_FIJO to "Precio fijo (MXN)",
        PromotionType.DOSxUNO to "2x1",
        PromotionType.TRAE_AMIGO to "Trae un amigo",
        PromotionType.OTRA to "Otra"
    )
    var expanded by remember { mutableStateOf(false) }
    val etiqueta = opciones.first { it.first == selected }.second

    // mismos colores/shape que AppStandardTextField
    val lightCyan = Color(0xFFE0F7FA)
    val borderCyan = Color(0xFFB2EBF2)
    val shape = RoundedCornerShape(16.dp)

    Column {
        Text(
            text = "Tipo de promoción",
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            color = Color.Black
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = etiqueta,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = shape,
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .border(1.dp, borderCyan, shape),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = lightCyan,
                    focusedContainerColor   = lightCyan,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor   = Color.Transparent
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            expanded = false
                            onSelected(value)
                        }
                    )
                }
            }
        }
    }
}
