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
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppLoadingOverlay
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionEvent
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoResumenView(
    onBack: () -> Unit,
    vm: CreatePromotionViewModel
) {
    val ui by vm.ui.collectAsState()
    val focus = LocalFocusManager.current

    // ====== Construimos una "previsualización" de lo que enviará el backend ======
    val porcentajeToSend = when (ui.tipo) {
        PromotionType.DESCUENTO -> ui.porcentajeTxt.toIntOrNull() ?: 0
        else -> 0
    }
    val precioToSend = when (ui.tipo) {
        PromotionType.PRECIO_FIJO -> ui.precioTxt.toIntOrNull() ?: 0
        else -> 0
    }
    val tipoLabel = when (ui.tipo) {
        PromotionType.DESCUENTO -> "Descuento (%)"
        PromotionType.PRECIO_FIJO -> "Precio fijo (MXN)"
        PromotionType.DOSxUNO -> "2x1"
        PromotionType.TRAE_AMIGO -> "Trae un amigo"
        PromotionType.OTRA -> "Otra"
    }
    val descripcionPreview = when (ui.tipo) {
        PromotionType.DESCUENTO, PromotionType.PRECIO_FIJO -> ui.descripcion.trim()
        PromotionType.DOSxUNO -> "2x1: ${ui.descripcion.trim()}"
        PromotionType.TRAE_AMIGO -> "Trae un amigo: ${ui.descripcion.trim()}"
        PromotionType.OTRA -> "Otra: ${ui.descripcion.trim()}"
    }
    val limiteTotalTxt = (ui.limiteTotalTxt.toIntOrNull() ?: 0).let { if (it > 0) "$it" else "Sin límite" }
    val limiteUsuarioTxt = (ui.limitePorUsuarioTxt.toIntOrNull() ?: 0).let { if (it > 0) "$it" else "Sin límite" }
    val fechasTxt = buildString {
        append(if (ui.startDate.isBlank()) "—" else ui.startDate)
        append("  –  ")
        append(if (ui.endDate.isBlank()) "—" else ui.endDate)
    }

    // ====== Diálogos de éxito / error ======
    if (ui.successMessage != null) {
        AlertDialog(
            onDismissRequest = { vm.onEvent(CreatePromotionEvent.ConsumeMessages) },
            title = { Text("Promoción enviada") },
            text = { Text(ui.successMessage ?: "Se envió correctamente.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.onEvent(CreatePromotionEvent.ClearForm)
                    vm.onEvent(CreatePromotionEvent.ConsumeMessages)
                }) { Text("Aceptar") }
            }
        )
    }
    if (ui.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { vm.onEvent(CreatePromotionEvent.ConsumeMessages) },
            title = { Text("No se pudo enviar") },
            text = { Text(ui.errorMessage ?: "Intenta de nuevo.") },
            confirmButton = {
                TextButton(onClick = { vm.onEvent(CreatePromotionEvent.ConsumeMessages) }) {
                    Text("Entendido")
                }
            }
        )
    }

    // ====== UI ======
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
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Revisa y confirma",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                color = TealPrimary,
                fontSize = 18.sp
            )
            Text(
                text = "Verifica que la información sea correcta antes de enviar.",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = PoppinsFamily,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            InfoRow("ID negocio", ui.idNegocio.ifBlank { "—" })
            InfoRow("Título", ui.titulo.ifBlank { "—" })
            InfoRow("Tipo", tipoLabel)

            when (ui.tipo) {
                PromotionType.DESCUENTO -> InfoRow("Porcentaje a enviar", "$porcentajeToSend %")
                PromotionType.PRECIO_FIJO -> InfoRow("Precio a enviar", "$precioToSend MXN")
                else -> {
                    InfoRow("Porcentaje a enviar", "0")
                    InfoRow("Precio a enviar", "0 MXN")
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Descripción / Mecánica (como se enviará):",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                text = if (descripcionPreview.isBlank()) "—" else descripcionPreview,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(12.dp))
            InfoRow("Vigencia", fechasTxt)
            InfoRow("Límite total", limiteTotalTxt)
            InfoRow("Límite por usuario", limiteUsuarioTxt)
            InfoRow("Activo", "Sí (siempre)")

            Spacer(Modifier.weight(1f))

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
                        vm.onEvent(CreatePromotionEvent.Submit)
                    },
                    enabled = ui.isValid && !ui.isLoading,
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
                        if (ui.isLoading) "Enviando..." else "Enviar",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
    AppLoadingOverlay(visible = ui.isLoading)
}

/** Fila simple de etiqueta + valor para el resumen. */
@Composable
private fun InfoRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = Color.Black
        )
        Text(
            text = value,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Light,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
        Spacer(Modifier.height(10.dp))
    }
}
