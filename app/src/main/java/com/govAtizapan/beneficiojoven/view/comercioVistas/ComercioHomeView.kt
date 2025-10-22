package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.view.comercioVistas.components.ComercioPromotionCard
import com.govAtizapan.beneficiojoven.view.comercioVistas.components.ComercioFilter
import com.govAtizapan.beneficiojoven.view.comercioVistas.components.ComercioFiltersRow
import com.govAtizapan.beneficiojoven.viewmodel.comercio.ComercioHomeViewModel
// Colores ya usados en HomeView para mantener el mismo look&feel
import com.govAtizapan.beneficiojoven.view.home.homeViews.TealPrimary
import com.govAtizapan.beneficiojoven.view.home.homeViews.CardBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComercioHome(
    onCrearPromo: () -> Unit,
    onValidarQR: () -> Unit,
    vm: ComercioHomeViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    // --- Estado UI local para búsqueda y filtros (mismo patrón que Home) ---
    var searchQuery by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf(ComercioFilter.TODAS) }

    // --- Filtrado en memoria (por nombre y estado activo/inactivo) ---
    val promocionesFiltradas = remember(ui.promociones, searchQuery, filtro) {
        ui.promociones
            .filter { p ->
                val matchSearch = if (searchQuery.isBlank()) true
                else p.nombre.contains(searchQuery, ignoreCase = true) ||
                        p.descripcion.contains(searchQuery, ignoreCase = true)
                val matchEstado = when (filtro) {
                    ComercioFilter.TODAS -> true
                    ComercioFilter.ACTIVAS -> p.activo
                    ComercioFilter.INACTIVAS -> !p.activo
                }
                matchSearch && matchEstado
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis promociones",
                        color = Color.White,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { vm.refrescar() }, enabled = !ui.isLoading) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TealPrimary)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            // --- Búsqueda (idéntico layout a HomeView) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TealPrimary)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        color = Color.White
                    ),
                    placeholder = {
                        Text(
                            "Buscar promoción…",
                            color = Color.White.copy(alpha = 0.7f),
                            fontFamily = PoppinsFamily,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Refresh, // (puedes poner Search si ya lo tienes en tus imports)
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color.White)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.White,
                        focusedTrailingIconColor = Color.White,
                        unfocusedTrailingIconColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(50)
                )
            }

            // --- Chips de filtro (mismo estilo que Home) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {
                Text(
                    text = "Filtros",
                    fontSize = 18.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = TealPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                ComercioFiltersRow(
                    selected = filtro,
                    onSelected = { filtro = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // --- Contenido scrollable (lista + estados) ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = 12.dp,
                        bottom = innerPadding.calculateBottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // CTA superiores (coinciden con tu requerimiento)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onCrearPromo,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                            ) { Text("Promoción Nueva", fontFamily = PoppinsFamily) }

                            OutlinedButton(
                                onClick = onValidarQR,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary),
                                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                            ) { Text("Validar QR", fontFamily = PoppinsFamily) }
                        }
                    }

                    // Estados
                    when {
                        ui.isLoading && ui.promociones.isEmpty() -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(Modifier.height(12.dp))
                                    Text("Cargando promociones…", fontFamily = PoppinsFamily)
                                }
                            }
                        }

                        ui.error != null && ui.promociones.isEmpty() -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        ui.error ?: "Error al obtener promociones",
                                        color = MaterialTheme.colorScheme.error,
                                        fontFamily = PoppinsFamily
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedButton(onClick = { vm.refrescar() }) {
                                        Text("Reintentar", fontFamily = PoppinsFamily)
                                    }
                                }
                            }
                        }

                        promocionesFiltradas.isEmpty() -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CardBackground, RoundedCornerShape(16.dp))
                                        .padding(16.dp),
                                ) {
                                    Text(
                                        "Sin resultados para el filtro seleccionado.",
                                        fontFamily = PoppinsFamily,
                                        color = Color.Gray
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Prueba limpiando la búsqueda o cambiando el filtro.",
                                        fontFamily = PoppinsFamily,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        else -> {
                            items(promocionesFiltradas, key = { it.id }) { promo ->
                                ComercioPromotionCard(
                                    promo = promo,
                                    onClick = { /* TODO: navegar a detalle/edición */ }
                                )
                            }
                        }
                    }
                }

                // Indicador discreto si refresca con lista ya cargada
                if (ui.isLoading && ui.promociones.isNotEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
