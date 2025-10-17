package com.govAtizapan.beneficiojoven.ui.theme.uiComponents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDateRangeField(
    onChange: (startIso: String, endIso: String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var open by remember { mutableStateOf(false) }
    val state = rememberDateRangePickerState()

    Column(modifier) {
        OutlinedButton(
            onClick = { open = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            val s = state.selectedStartDateMillis?.let { millisToIso(it) } ?: "—"
            val e = state.selectedEndDateMillis?.let { millisToIso(it) } ?: "—"
            Text("$label:  $s  –  $e")
        }

        if (open) {
            // Fullscreen-style para evitar recortes; SIN verticalScroll externo
            Dialog(
                onDismissRequest = { open = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.94f)
                            .wrapContentHeight() // el alto lo controla el picker con heightIn
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Selecciona el rango de fechas",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(8.dp))

                            // 👇 Clave: dar ALTURA FINITA al DateRangePicker (sin verticalScroll).
                            DateRangePicker(
                                state = state,
                                title = null,
                                showModeToggle = false,
                                headline = {
                                    val s = state.selectedStartDateMillis?.let { millisToIso(it) } ?: "—"
                                    val e = state.selectedEndDateMillis?.let { millisToIso(it) } ?: "—"
                                    Text("$s  –  $e", style = MaterialTheme.typography.bodyMedium)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 360.dp, max = 520.dp) // <- altura acotada
                            )

                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(
                                Modifier,
                                DividerDefaults.Thickness,
                                DividerDefaults.color
                            )
                            Spacer(Modifier.height(8.dp))

                            Row(Modifier.fillMaxWidth()) {
                                TextButton(onClick = { open = false }) { Text("Cancelar") }
                                Spacer(Modifier.weight(1f))

                                val canConfirm =
                                    state.selectedStartDateMillis != null &&
                                            state.selectedEndDateMillis != null

                                ElevatedButton(
                                    onClick = {
                                        val start = state.selectedStartDateMillis!!
                                        val end   = state.selectedEndDateMillis!!
                                        onChange(millisToIso(start), millisToIso(end))
                                        open = false
                                    },
                                    enabled = canConfirm
                                ) { Text("Confirmar") }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Convierte millis a "YYYY-MM-DD" usando kotlinx-datetime (seguro en API < 26). */
private fun millisToIso(millis: Long): String {
    val date = Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    return date.toString()
}
