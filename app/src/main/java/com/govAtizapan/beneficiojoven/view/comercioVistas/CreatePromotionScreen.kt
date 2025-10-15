package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionRequest
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePromotionScreen(vm: CreatePromotionViewModel = viewModel()) {
    val ui = vm.ui.collectAsState()
    val focus = LocalFocusManager.current

    var idNegocio by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fechaInicio by rememberSaveable { mutableStateOf("") } // YYYY-MM-DD
    var fechaFin by rememberSaveable { mutableStateOf("") }    // YYYY-MM-DD
    var porcentaje by rememberSaveable { mutableStateOf("") }
    var precio by rememberSaveable { mutableStateOf("") }
    var activo by rememberSaveable { mutableStateOf(true) }

    val idNegocioInt = idNegocio.toIntOrNull()
    val porcentajeInt = porcentaje.toIntOrNull()
    val precioInt = precio.toIntOrNull()
    val ymd = Regex("""^\d{4}-\d{2}-\d{2}$""")

    fun toIso(date: String) = "${date}T00:00:00Z"

    val valido = idNegocioInt != null &&
            nombre.isNotBlank() &&
            descripcion.isNotBlank() &&
            fechaInicio.matches(ymd) &&
            fechaFin.matches(ymd) &&
            porcentajeInt != null && porcentajeInt >= 0 &&
            precioInt != null && precioInt >= 0 &&
            ((porcentajeInt ?: 0) > 0 || (precioInt ?: 0) > 0)

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
            onValueChange = { idNegocio = it.filter(Char::isDigit) },
            label = { Text("ID del negocio") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            isError = idNegocio.isNotBlank() && idNegocioInt == null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
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

        OutlinedTextField(
            value = fechaInicio,
            onValueChange = { fechaInicio = it },
            label = { Text("Fecha inicio (YYYY-MM-DD)") },
            singleLine = true,
            isError = fechaInicio.isNotBlank() && !fechaInicio.matches(ymd),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = fechaFin,
            onValueChange = { fechaFin = it },
            label = { Text("Fecha fin (YYYY-MM-DD)") },
            singleLine = true,
            isError = fechaFin.isNotBlank() && !fechaFin.matches(ymd),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = porcentaje,
            onValueChange = { porcentaje = it.filter(Char::isDigit) },
            label = { Text("Porcentaje (0-100)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            isError = porcentaje.isNotBlank() && porcentajeInt == null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it.filter(Char::isDigit) },
            label = { Text("Precio (MXN)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            isError = precio.isNotBlank() && precioInt == null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Activo")
            Switch(checked = activo, onCheckedChange = { activo = it })
        }

        Spacer(Modifier.height(16.dp))

        Button(
            enabled = valido && !ui.value.isLoading,
            onClick = {
                focus.clearFocus()
                val body = PromotionRequest(
                    idNegocio = idNegocioInt!!,
                    nombre = nombre.trim(),
                    descripcion = descripcion.trim(),
                    fechaInicio = toIso(fechaInicio.trim()),
                    fechaFin = toIso(fechaFin.trim()),
                    porcentaje = porcentajeInt ?: 0,
                    precio = precioInt ?: 0,
                    activo = activo
                )
                vm.createPromotion(body)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (ui.value.isLoading) "Enviando..." else "Enviar promoción")
        }

        Spacer(Modifier.height(12.dp))

        ui.value.success?.let { Text("✔ $it", color = MaterialTheme.colorScheme.primary) }
        ui.value.error?.let { Text("✖ $it", color = MaterialTheme.colorScheme.error) }
    }
}
