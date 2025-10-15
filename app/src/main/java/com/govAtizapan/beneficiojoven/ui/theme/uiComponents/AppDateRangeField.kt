@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.govAtizapan.beneficiojoven.ui.theme.uiComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Selector de rango de fechas con calendario (Material3) que devuelve ISO "YYYY-MM-DD".
 * 100% Kotlin con kotlinx-datetime (no usa java.time ni desugaring).
 *
 * Uso:
 * AppDateRangeField(
 *   onChange = { startIso, endIso -> ... },
 *   label = "Rango de fechas (YYYY-MM-DD)"
 * )
 */
@Composable
fun AppDateRangeField(
    onChange: (startIso: String, endIso: String) -> Unit,   // requerido primero
    modifier: Modifier = Modifier,                          // primer opcional
    label: String = "Rango de fechas (YYYY-MM-DD)"          // resto de opcionales
) {
    var open by remember { mutableStateOf(false) }
    val state = rememberDateRangePickerState() // Material3 1.2.x+

    Column(modifier = modifier) {
        OutlinedButton(
            onClick = { open = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            val startText = state.selectedStartDateMillis?.let { millisToIsoKtx(it) } ?: "—"
            val endText = state.selectedEndDateMillis?.let { millisToIsoKtx(it) } ?: "—"
            Text("$label:  $startText  →  $endText")
        }

        if (open) {
            // ✅ En Material3 el diálogo es DatePickerDialog (sirve para single o range)
            DatePickerDialog(
                onDismissRequest = { open = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val s = state.selectedStartDateMillis
                            val e = state.selectedEndDateMillis
                            if (s != null && e != null) {
                                onChange(millisToIsoKtx(s), millisToIsoKtx(e))
                            }
                            open = false
                        }
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    TextButton(onClick = { open = false }) { Text("Cancelar") }
                }
            ) {
                DateRangePicker(
                    state = state,
                    title = { Text("Selecciona el rango") },
                    headline = {
                        val s = state.selectedStartDateMillis?.let { millisToIsoKtx(it) } ?: "—"
                        val e = state.selectedEndDateMillis?.let { millisToIsoKtx(it) } ?: "—"
                        Text("$s  →  $e")
                    }
                )
            }
        }
    }
}

/** 100% Kotlin: convierte millis a "YYYY-MM-DD" */
private fun millisToIsoKtx(millis: Long): String {
    val date = Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    return date.toString() // ISO "yyyy-MM-dd"
}
