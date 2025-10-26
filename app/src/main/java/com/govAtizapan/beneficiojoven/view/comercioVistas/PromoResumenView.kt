/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define la última pantalla del flujo de creación de promociones:
 * `PromoResumenView`, junto con el helper `InfoRow`.
 *
 * Objetivo de `PromoResumenView`:
 * * Mostrar un resumen legible de TODOS los datos capturados en pasos anteriores:
 * * Título de la promoción.
 * * Tipo de promoción (descuento, precio fijo, 2x1, etc.).
 * * Porcentaje o precio a enviar al backend (según el tipo).
 * * Descripción / mecánica final.
 * * Rango de fechas (vigencia).
 * * Límites totales y por usuario.
 * * Imagen seleccionada, si existe.
 *
 * * Permitir al usuario:
 * * Regresar a pantallas anteriores (`onBack`) para corregir algo.
 * * Enviar la promoción (`CreatePromotionEvent.Submit`) al backend.
 *
 * Comportamiento importante:
 * * Escucha el estado del ViewModel (`CreatePromotionViewModel`) vía `vm.ui.collectAsState()`.
 * * `ui.isValid` y `ui.isLoading` controlan el botón "Enviar".
 * * `ui.successMessage` y `ui.errorMessage` activan `AlertDialog`:
 * ```
- En caso de éxito:
```
 * ```
- Muestra mensaje.
```
 * ```
- Llama `ClearForm`.
```
 * ```
- Ejecuta `onFinish()` para regresar a la pantalla principal de comercio
```
 * ```
y cerrar el flujo de registro.
```
 * ```
- En caso de error:
```
 * ```
- Muestra mensaje.
```
 * ```
- Permite descartarlo con `ConsumeMessages`.
```
 *
 * UI:
 * * Usa `Scaffold` con `bottomBar` fija para los botones "Atrás" y "Enviar".
 * El contenido hace scroll independiente con `verticalScroll(...)`.
 * * Estilo consistente con el módulo de comercio:
 * * Tipografía Poppins.
 * * Colores `TealPrimary`.
 * * Botones redondeados de 14.dp.
 *
 * Helper `InfoRow`:
 * * Pequeño composable reutilizable para mostrar un par (etiqueta/valor) con estilo.
 *
 * Resultado:
 * Esta pantalla es la confirmación final antes del POST real de la promoción.
 */




package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionEvent
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoResumenView(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    vm: CreatePromotionViewModel
) {
    val ui by vm.ui.collectAsState()
    val focus = LocalFocusManager.current

    // ====== Preview de lo que se enviará ======
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
        PromotionType.DOSxUNO    -> "2x1: ${ui.descripcion.trim()}"
        PromotionType.TRAE_AMIGO -> "Trae un amigo: ${ui.descripcion.trim()}"
        PromotionType.OTRA       -> "Otra: ${ui.descripcion.trim()}"
    }
    val limiteTotalTxt = (ui.limiteTotalTxt.toIntOrNull() ?: 0).let { if (it > 0) "$it" else "Sin límite" }
    val limiteUsuarioTxt = (ui.limitePorUsuarioTxt.toIntOrNull() ?: 0).let { if (it > 0) "$it" else "Sin límite" }
    val fechasTxt = buildString {
        append(if (ui.startDate.isBlank()) "—" else ui.startDate)
        append("  –  ")
        append(if (ui.endDate.isBlank()) "—" else ui.endDate)
    }

    // ====== Diálogos ======
    if (ui.successMessage != null) {
        AlertDialog(
            onDismissRequest = { /* evita cerrar tocando fuera si quieres */ },
            title = { Text("¡Promoción enviada!") },
            text = { Text(ui.successMessage ?: "Se envió correctamente.") },
            confirmButton = {
                TextButton(onClick = {
                    // 1) Limpia estados si lo requieres (opcional)
                    vm.onEvent(CreatePromotionEvent.ClearForm)

                    // 2) Vuelve a la Home de comercio
                    onFinish()
                }) {
                    Text("Aceptar")
                }
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

    // ====== UI principal con botones fijos abajo ======
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White,
        bottomBar = {
            // Botonera fija inferior
            Surface(
                tonalElevation = 4.dp,
                color = Color.White
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .safeDrawingPadding()
                ) {
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
            }
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // contenido hace scroll, botones fijos
                .safeDrawingPadding()
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

            InfoRow("Título", ui.titulo.ifBlank { "—" })
            InfoRow("Tipo", tipoLabel)

            when (ui.tipo) {
                PromotionType.DESCUENTO   -> InfoRow("Porcentaje a enviar", "$porcentajeToSend %")
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

            Spacer(Modifier.height(16.dp))

            // Miniatura de imagen (si existe)
            ui.imagenUri?.let { uriStr ->
                Text(
                    text = "Imagen seleccionada:",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
                val painter = rememberAsyncImagePainter(model = uriStr)
                Image(
                    painter = painter,
                    contentDescription = "Vista previa de la imagen",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Si deseas cambiarla, regresa a la pantalla anterior.",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = PoppinsFamily,
                    color = Color.Gray
                )
            }

            // padding extra al final para que no tape contenido el bottomBar
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth()
    ) {
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
