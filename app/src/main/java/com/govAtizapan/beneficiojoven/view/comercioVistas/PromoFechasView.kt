/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define la pantalla/formulario `PromoFechasView`, que corresponde
 * al paso donde el comercio especifica:
 * 1. El rango de vigencia de la promoción.
 * 2. (Opcional) La imagen promocional.
 *
 * Funcionalidad principal:
 * * Usa `AppDateRangeField` para permitir seleccionar fecha de inicio y fin con un
 * `DateRangePicker` de Material 3. Cuando el usuario confirma el rango, se manda
 * `CreatePromotionEvent.StartEndChanged(start, end)` al ViewModel.
 *
 * * Valida que:
 * * Ambas fechas tengan formato `YYYY-MM-DD`.
 * * La fecha de inicio no sea posterior a la fecha de fin.
 * Esta validación controla si el botón "Siguiente" está habilitado.
 *
 * * Muestra la sección `PromoImagenSection`, que permite al usuario elegir una imagen
 * opcional desde la galería y previsualizarla antes de hacer el POST final.
 *
 * Navegación:
 * * Botón "Atrás": ejecuta `onBack()`.
 * * Botón "Siguiente": ejecuta `onNext()` sólo si las fechas son válidas.
 *
 * Integración con estado:
 * * Consume el `CreatePromotionViewModel` (inyectado externamente; no se crea aquí).
 * * Observa `vm.ui.collectAsState()` para leer valores actuales como `startDate`,
 * `endDate` e `imagenUri`.
 *
 * Estilo:
 * * Uso de tipografía Poppins y color `TealPrimary` para mantener consistencia visual
 * con las demás pantallas del módulo de comercio.
 */




package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppDateRangeField
import com.govAtizapan.beneficiojoven.view.comercioVistas.components.PromoImagenSection
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionEvent
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoFechasView(
    onBack: () -> Unit,
    onNext: () -> Unit,
    // IMPORTANTE: este VM debe venir inyectado desde el sub-grafo (NO crear uno nuevo aquí)
    vm: CreatePromotionViewModel
) {
    val ui by vm.ui.collectAsState()
    val focus = LocalFocusManager.current

    // Validación simple: ambos presentes y start <= end (formato ISO permite comparación lexicográfica)
    val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
    val bothSelected = ui.startDate.matches(dateRegex) && ui.endDate.matches(dateRegex)
    val orderOk = bothSelected && ui.startDate <= ui.endDate
    val canContinue = bothSelected && orderOk

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Fechas e imagen",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                color = TealPrimary,
                fontSize = 18.sp
            )
            Text(
                text = "Selecciona el rango de vigencia y, si deseas, agrega una imagen promocional.",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = PoppinsFamily,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            // --------- Selector de rango de fechas ----------
            AppDateRangeField(
                onChange = { start, end -> vm.onEvent(CreatePromotionEvent.StartEndChanged(start, end)) },
                label = "Rango de fecha (YYYY-MM-DD)",
                modifier = Modifier.fillMaxWidth()
            )

            if (ui.startDate.isNotBlank() || ui.endDate.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Inicio: ${ui.startDate.ifBlank { "—" }}   •   Fin: ${ui.endDate.ifBlank { "—" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    fontFamily = PoppinsFamily
                )
            }

            if (bothSelected && !orderOk) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "La fecha de inicio no puede ser posterior a la fecha de fin.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = PoppinsFamily
                )
            }

            Spacer(Modifier.height(24.dp))

            // --------- Imagen promocional (opcional) ----------
            PromoImagenSection(
                vm = vm,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            // --------- Navegación ----------
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        focus.clearFocus()
                        onBack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
                ) {
                    Text(
                        "Atrás",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                Button(
                    onClick = {
                        focus.clearFocus()
                        onNext()
                    },
                    enabled = canContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Siguiente",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
