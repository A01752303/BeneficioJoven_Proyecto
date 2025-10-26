/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define el componente Compose `ComercioPromotionCard`, que muestra la información
 * de una promoción de un comercio con el mismo estilo visual usado en Home:
 *
 * * Usa `ElevatedCard` con esquinas redondeadas de 16.dp.
 * * Tipografía Poppins en todos los textos.
 * * Chip de beneficio (porcentaje, precio fijo o tipo de promo) con colores `TealLight` y `TealPrimary`.
 * * Imagen opcional en la parte superior de la tarjeta (si la promo tiene `imagenUrl`).
 *
 * Contenido mostrado:
 * * Nombre de la promoción.
 * * Beneficio principal (ej. "30% OFF", "$85 MXN", "2x1", etc.).
 * * Descripción (truncada a 3 líneas).
 * * Vigencia de la promoción (`fechaInicioIso – fechaFinIso`).
 * * Número de canjes (`numeroCanjeados`).
 * * Estado actual ("Activa" / "Inactiva").
 *
 * Interacción:
 * * `onClick`: callback opcional si la tarjeta debe ser presionable completa (por ejemplo para ver detalles o editar).
 * * `actionLabel` + `onActionClick`: botón de acción secundaria alineado a la derecha (por ejemplo "Editar", "Desactivar").
 *
 * Uso:
 * Se utiliza en listados de promociones dentro de la vista de comercio para que el dueño/colaborador
 * pueda visualizar rápidamente el rendimiento y estado de cada promoción.
 */



package com.govAtizapan.beneficiojoven.view.comercioVistas.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.govAtizapan.beneficiojoven.model.comercio.ComercioPromotion
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.view.home.homeViews.TealLight
import com.govAtizapan.beneficiojoven.view.home.homeViews.TealPrimary
import androidx.compose.foundation.BorderStroke


/**
 * Tarjeta de promoción con el mismo estilo visual que Home:
 * - Esquinas 16dp, ElevatedCard
 * - Tipografía Poppins
 * - Chips con TealPrimary/TealLight
 */
@Composable
fun ComercioPromotionCard(
    promo: ComercioPromotion,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {

            // Imagen superior (opcional)
            promo.imagenUrl?.takeIf { it.isNotBlank() }?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(model = url),
                    contentDescription = "Imagen de la promoción",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(Modifier.padding(14.dp)) {

                // Título + chip de tipo/beneficio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = promo.nombre,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val chipText = when {
                        promo.porcentaje > 0 -> "${promo.porcentaje.toInt()}% OFF"
                        promo.precio > 0     -> "$${promo.precio.toInt()} MXN"
                        else                  -> promo.tipo.ifBlank { "Promo" }
                    }

                    AssistChip(
                        onClick = { /* decorativo */ },
                        label = {
                            Text(
                                chipText,
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = TealLight,
                            labelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, TealPrimary)
                    )
                }

                Spacer(Modifier.height(6.dp))

                // Descripción
                Text(
                    text = promo.descripcion,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(10.dp))

                // Metadatos
                ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                    Text(
                        text = "Vigencia: ${promo.fechaInicioIso} – ${promo.fechaFinIso}",
                        fontFamily = PoppinsFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Canjeados: ${promo.numeroCanjeados}",
                        fontFamily = PoppinsFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (promo.activo) "Estado: Activa" else "Estado: Inactiva",
                        fontFamily = PoppinsFamily,
                        color = if (promo.activo) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Acción opcional (p.ej. Editar/Desactivar)
                if (actionLabel != null && onActionClick != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onActionClick) {
                            Text(
                                actionLabel,
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
