package com.govAtizapan.beneficiojoven.ui.theme.uiComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Selector de rango con Material3 que evita usar DateRangePickerDialog
 * (algunas versiones no lo traen o cambia la firma).
 *
 * Muestra un botón con el label y, al abrir, un BasicAlertDialog con un DateRangePicker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDateRangeField(
    onChange: (startIso: String, endIso: String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var open by remember { mutableStateOf(false) }

    // Puedes configurar valores iniciales si quieres:
    val state = rememberDateRangePickerState(
        // initialSelectedStartDateMillis = null,
        // initialSelectedEndDateMillis = null
    )

    Column(modifier) {
        // Botón que abre el selector
        OutlinedButton(
            onClick = { open = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            val s = state.selectedStartDateMillis?.let { millisToIso(it) } ?: "—"
            val e = state.selectedEndDateMillis?.let { millisToIso(it) } ?: "—"
            Text("$label:  $s  –  $e")
        }

        if (open) {
            BasicAlertDialog(
                onDismissRequest = { open = false }
            ) {
                // Contenido del diálogo (tarjeta)
                androidx.compose.material3.Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 6.dp
                ) {
                    Column(
                        Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Selecciona el rango de fechas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))

                        DateRangePicker(
                            state = state,
                            title = null,         // ya ponemos nuestro título arriba
                            headline = {
                                val s = state.selectedStartDateMillis?.let { millisToIso(it) } ?: "—"
                                val e = state.selectedEndDateMillis?.let { millisToIso(it) } ?: "—"
                                Text("$s  –  $e", style = MaterialTheme.typography.bodyMedium)
                            },
                            showModeToggle = false
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = { open = false }
                            ) { Text("Cancelar") }

                            Spacer(Modifier.weight(1f))

                            val enabled = state.selectedStartDateMillis != null &&
                                    state.selectedEndDateMillis != null

                            ElevatedButton(
                                onClick = {
                                    val start = state.selectedStartDateMillis
                                    val end = state.selectedEndDateMillis
                                    if (start != null && end != null) {
                                        onChange(millisToIso(start), millisToIso(end))
                                    }
                                    open = false
                                },
                                enabled = enabled,
                                colors = ButtonDefaults.elevatedButtonColors()
                            ) { Text("Confirmar") }
                        }
                    }
                }
            }
        }
    }
}

/** Convierte millis a "YYYY-MM-DD" con kotlinx-datetime (sin java.time). */
private fun millisToIso(millis: Long): String {
    val date = Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    return date.toString() // ISO yyyy-MM-dd
}
