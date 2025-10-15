package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppDateRangeField
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppPrimaryButton
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppTextField
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppThinDividerWithDot
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionEvent
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionUiState
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel

@Composable
fun CreatePromotionScreen(
    vm: CreatePromotionViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    val focus = LocalFocusManager.current

    LaunchedEffect(ui.successMessage, ui.errorMessage) {
        // aquí podrías mostrar snackbar y luego:
        vm.onEvent(CreatePromotionEvent.ConsumeMessages)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Crear promoción", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        AppTextField(
            value = ui.idNegocio,
            onValueChange = { vm.onEvent(CreatePromotionEvent.IdNegocioChanged(it)) },
            label = "ID del negocio",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            isError = ui.idNegocio.isNotBlank() && ui.idNegocio.toIntOrNull() == null
        )
        Spacer(Modifier.height(10.dp))

        AppTextField(
            value = ui.titulo,
            onValueChange = { vm.onEvent(CreatePromotionEvent.TituloChanged(it)) },
            label = "Título (nombre)"
        )
        Spacer(Modifier.height(10.dp))

        // Tipo de promoción
        TipoPromocionField(
            selected = ui.tipo,
            onSelected = { vm.onEvent(CreatePromotionEvent.TipoChanged(it)) }
        )
        Spacer(Modifier.height(10.dp))

        when (ui.tipo) {
            PromotionType.DESCUENTO -> {
                AppTextField(
                    value = ui.porcentajeTxt,
                    onValueChange = { vm.onEvent(CreatePromotionEvent.PorcentajeChanged(it)) },
                    label = "Porcentaje (%)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    supportingText = "1 a 100. Se enviará porcentaje=<valor> y precio=0"
                )
                Spacer(Modifier.height(10.dp))
            }
            PromotionType.PRECIO_FIJO -> {
                AppTextField(
                    value = ui.precioTxt,
                    onValueChange = { vm.onEvent(CreatePromotionEvent.PrecioChanged(it)) },
                    label = "Precio fijo (MXN)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    supportingText = "Monto en MXN. Se enviará precio=<valor> y porcentaje=0"
                )
                Spacer(Modifier.height(10.dp))
            }
            else -> Unit
        }

        AppTextField(
            value = ui.descripcion,
            onValueChange = { vm.onEvent(CreatePromotionEvent.DescripcionChanged(it)) },
            label = when (ui.tipo) {
                PromotionType.DESCUENTO, PromotionType.PRECIO_FIJO -> "Descripción"
                PromotionType.DOSxUNO -> "Descripción / Mecánica (2x1)"
                PromotionType.TRAE_AMIGO -> "Descripción / Mecánica (Trae un amigo)"
                PromotionType.OTRA -> "Descripción / Mecánica"
            },
            singleLine = false
        )

        Spacer(Modifier.height(10.dp))

        // Límites
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) {
                AppTextField(
                    value = ui.limiteTotalTxt,
                    onValueChange = { vm.onEvent(CreatePromotionEvent.LimiteTotalChanged(it)) },
                    label = "Límite total (opcional)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    supportingText = "0 o vacío = sin límite"
                )
            }
            Column(Modifier.weight(1f)) {
                AppTextField(
                    value = ui.limitePorUsuarioTxt,
                    onValueChange = { vm.onEvent(CreatePromotionEvent.LimitePorUsuarioChanged(it)) },
                    label = "Límite por usuario (opcional)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
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
                focus.clearFocus()
                vm.onEvent(CreatePromotionEvent.Submit)
            }
        )

        Spacer(Modifier.height(12.dp))
        AppThinDividerWithDot()
        Spacer(Modifier.height(12.dp))

        ui.successMessage?.let { Text("✔ $it", color = MaterialTheme.colorScheme.primary) }
        ui.errorMessage?.let { Text("✖ $it", color = MaterialTheme.colorScheme.error) }
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

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = etiqueta,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de promoción") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { (value, label) ->
                DropdownMenuItem(text = { Text(label) }, onClick = {
                    expanded = false
                    onSelected(value)
                })
            }
        }
    }
}
