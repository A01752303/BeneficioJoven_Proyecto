package com.govAtizapan.beneficiojoven.view.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.viewmodel.home.HomeVM
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.govAtizapan.beneficiojoven.viewmodel.categoriasVM.CategoriaVM
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import com.govAtizapan.beneficiojoven.model.categorias.CategoryResponseGET
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
// Imports añadidos para el TopAppBar colapsable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.input.nestedscroll.nestedScroll


val TealPrimary = Color(0xFF0096A6)
val TealLight = Color(0xFF4DB8C4)
val BackgroundGray = Color(0xFFF5F5F5)
val CardBackground = Color(0xFFFFFFFF)

enum class SortOption(val label: String) {
    NEWEST("Más nuevos"),
    EXPIRING_SOON("Por caducar")
}

val couponTypes = listOf(
    "Todos",
    "Descuento (%)",
    "Precio fijo (MXN)",
    "2x1",
    "Trae un amigo",
    "Otra"
)

const val EXPIRATION_THRESHOLD_DAYS: Long = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    navController: NavController,
    viewModel: HomeVM = viewModel(),
    viewModel2: CategoriaVM = viewModel()
) {
    val promociones by viewModel.promociones.collectAsState()
    val categorias by viewModel2.categorias.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val errorState by viewModel.errorState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryTitulo by remember { mutableStateOf<String?>("Todos") }
    var sortOption by remember { mutableStateOf(SortOption.NEWEST) }
    var selectedCouponType by remember { mutableStateOf(couponTypes.first()) }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())


    LaunchedEffect(Unit) {
        if (promociones.isEmpty()) {
            viewModel.cargarPromociones()
        }
        if (categorias.isEmpty()) {
            viewModel2.cargarCategorias()
        }
    }

    LaunchedEffect(selectedCategoryTitulo, selectedCouponType, sortOption) {
        if (promociones.isNotEmpty()) {
            coroutineScope.launch {
                lazyListState.scrollToItem(index = 0)
            }
        }
    }

    // --- Lógica de filtrado (movida aquí para estar disponible para el when) ---
    val filteredAndSortedPromos = remember(searchQuery, promociones, selectedCategoryTitulo, sortOption, selectedCouponType) {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        val now = Date()

        val filteredList = promociones.filter { promo ->
            try {
                val expirationDate = parser.parse(promo.fecha_fin)
                val isNotExpired = expirationDate?.after(now) ?: false

                val matchesSearch = if (searchQuery.isBlank()) {
                    true
                } else {
                    promo.nombre.contains(searchQuery, ignoreCase = true)
                }

                val matchesCategory = if (selectedCategoryTitulo == null || selectedCategoryTitulo == "Todos") {
                    true
                } else {
                    promo.categorias.any { it.titulo == selectedCategoryTitulo }
                }

                val matchesCouponType = if (selectedCouponType == "Todos") {
                    true
                } else {
                    val apiType = promo.tipo.lowercase().trim()
                    when (selectedCouponType) {
                        "Descuento (%)" -> apiType == "porcentaje"
                        "Precio fijo (MXN)" -> apiType == "precio"
                        "2x1" -> apiType == "2x1"
                        "Trae un amigo" -> apiType == "trae un amigo"
                        "Otra" -> apiType == "otra"
                        else -> false
                    }
                }

                isNotExpired && matchesSearch && matchesCategory && matchesCouponType

            } catch (e: Exception) {
                false
            }
        }

        when (sortOption) {
            SortOption.NEWEST -> filteredList.sortedByDescending { it.fecha_fin }
            SortOption.EXPIRING_SOON -> filteredList.sortedBy { it.fecha_fin }
        }
    }


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .safeDrawingPadding()
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Bienvenid@",
                            fontSize = 22.sp,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Busca tu cupón...",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontFamily = PoppinsFamily)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = Color.White
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Limpiar",
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedIndicatorColor = Color.White,
                                unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
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
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealPrimary,
                    scrolledContainerColor = TealPrimary
                ),
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { /* Ya en home */ }) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = TealPrimary)
                    }
                    IconButton(onClick = { navController.navigate(AppScreens.ComerciosCercanosScreen.route) }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa")
                    }
                    IconButton(onClick = { navController.navigate(AppScreens.BienvenidaView.route) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            }
        },
        containerColor = BackgroundGray
    ) { innerPadding ->

        // --- 👇 NUEVA ESTRUCTURA DE COLUMNA ---
        // Esta Column aplica el padding SUPERIOR del Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {

            // --- 1. CONTENIDO FIJO (STICKY) ---
            CategoryRow(
                categories = categorias,
                selectedTitulo = selectedCategoryTitulo,
                onCategorySelected = { categoryTitulo ->
                    selectedCategoryTitulo = categoryTitulo
                }
            )

            SectionTitle("Filtros")

            FilterChipsRow(
                selectedType = selectedCouponType,
                onTypeSelected = { selectedCouponType = it },
                selectedOption = sortOption,
                onOptionSelected = { sortOption = it }
            )

            // --- 2. CONTENIDO DESLIZABLE (SCROLLABLE) ---
            // Este Box ocupa el espacio restante
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f) // <-- Ocupa todo el espacio restante
            ) {
                when {
                    isLoading -> {
                        // El SkeletonList ahora no necesita padding superior,
                        // solo el inferior del Scaffold
                        SkeletonList(
                            PaddingValues(bottom = innerPadding.calculateBottomPadding())
                        )
                    }
                    errorState != null && promociones.isEmpty() -> {
                        // El ErrorStateView ahora no necesita padding superior
                        ErrorStateView(
                            message = errorState ?: "Ocurrió un error desconocido.",
                            onRetry = { viewModel.cargarPromociones() },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        )
                    }
                    filteredAndSortedPromos.isEmpty() -> {
                        // El EmptyStateView se mostrará en el espacio restante
                        EmptyStateView(
                            searchQuery,
                            selectedCategoryTitulo,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        )
                    }
                    else -> {
                        // La LazyColumn ahora solo contiene la lista
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxSize(),
                            // Padding: Arriba (para separar de filtros),
                            // Lados (para contenido), Abajo (para BottomBar)
                            contentPadding = PaddingValues(
                                top = 16.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = innerPadding.calculateBottomPadding() + 12.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // CONTEO DE RESULTADOS
                            item(key = "results_count") {
                                val resultsText = if (filteredAndSortedPromos.size == 1) {
                                    "Mostrando 1 resultado"
                                } else {
                                    "Mostrando ${filteredAndSortedPromos.size} resultados"
                                }
                                Text(
                                    text = resultsText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = PoppinsFamily,
                                    color = Color.Gray,
                                    // Ya no necesita padding horizontal
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            // LISTA DE PROMOCIONES
                            items(filteredAndSortedPromos, key = { it.id }) { promo ->

                                val (isExpiringSoon, daysRemaining) = remember(promo.fecha_fin) {
                                    try {
                                        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                                        val expirationDate = parser.parse(promo.fecha_fin)
                                        val now = Date()

                                        if (expirationDate != null && expirationDate.after(now)) {
                                            val timeDiffMs = expirationDate.time - now.time
                                            val daysLeft = timeDiffMs / (1000 * 60 * 60 * 24)
                                            val isExpiring = daysLeft < EXPIRATION_THRESHOLD_DAYS
                                            Pair(isExpiring, daysLeft)
                                        } else {
                                            Pair(false, null)
                                        }
                                    } catch (e: Exception) {
                                        Pair(false, null)
                                    }
                                }

                                // Ya no se necesita el Box con padding horizontal
                                PromoCard(
                                    promo = promo,
                                    isExpiringSoon = isExpiringSoon,
                                    daysRemaining = daysRemaining,
                                    onClick = {
                                        navController.navigate("detalleCupon/${promo.id}")
                                    }
                                )
                                // Ya no se necesita el Spacer vertical
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        color = TealPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.dp)
    )
}

@Composable
fun PromoCard(
    promo: PromotionResponseGET,
    isExpiringSoon: Boolean,
    daysRemaining: Long?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                AsyncImage(
                    model = promo.imagen,
                    contentDescription = "Imagen del cupón",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray)
                )

                val validityText = if (isExpiringSoon && daysRemaining != null) {
                    when (daysRemaining) {
                        0L -> "¡Vence hoy!"
                        1L -> "1 día restante"
                        else -> "$daysRemaining días restantes"
                    }
                } else {
                    "Válido hasta: ${promo.fecha_fin.take(10)}"
                }
                val backgroundColor = if (isExpiringSoon) {
                    Color.Red.copy(alpha = 0.85f)
                } else {
                    TealPrimary.copy(alpha = 0.85f)
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Fecha de vencimiento",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = validityText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        fontFamily = PoppinsFamily,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = promo.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121),
                    lineHeight = 24.sp,
                    fontFamily = PoppinsFamily
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (promo.categorias.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        promo.categorias.take(2).forEach { categoria ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFF0F0F0),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = categoria.titulo,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666),
                                    fontFamily = PoppinsFamily
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = promo.descripcion,
                    fontSize = 15.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = PoppinsFamily,
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (promo.precio.isNotEmpty() && promo.precio != "0.00000") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Precio: ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF666666),
                            fontFamily = PoppinsFamily,
                        )
                        val precioFormateado = promo.precio.toDoubleOrNull()?.let { precioDouble ->
                            String.format("%.2f", precioDouble)
                        } ?: promo.precio
                        Text(
                            text = "$$precioFormateado",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            fontFamily = PoppinsFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterChipsRow(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(couponTypes) { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                enabled = true,
                label = { Text(type, fontFamily = PoppinsFamily) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TealLight,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = TealPrimary,
                    selectedBorderColor = Color.Transparent,
                    enabled = true,
                    selected = selectedType == type
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        item {
            Divider(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp)
                    .padding(vertical = 4.dp),
                color = Color.LightGray
            )
        }


        items(SortOption.values()) { option ->
            FilterChip(
                selected = selectedOption == option,
                onClick = { onOptionSelected(option) },
                enabled = true,
                label = { Text(option.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TealLight,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = TealPrimary,
                    selectedBorderColor = Color.Transparent,
                    enabled = true,
                    selected = selectedOption == option
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun ErrorStateView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier // <-- Añadido modifier
) {
    Column(
        modifier = modifier // <-- Aplicado modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontFamily = PoppinsFamily
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun EmptyStateView(
    searchQuery: String,
    selectedCategory: String?,
    modifier: Modifier = Modifier // <-- Añadido modifier
) {
    Box(
        modifier = modifier // <-- Aplicado modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (searchQuery.isNotEmpty()) {
                "No se encontraron resultados para \"$searchQuery\""
            } else {
                "No hay promociones en la categoría \"${selectedCategory ?: "Todos"}\""
            },
            fontSize = 16.sp,
            fontFamily = PoppinsFamily,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SkeletonList(
    contentPadding: PaddingValues = PaddingValues() // <-- Añadido padding
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // Aplicar padding (para el bottom bar)
            .padding(contentPadding),
        // Padding interno de la lista
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(4) {
            PromoCardSkeleton()
        }
    }
}

@Composable
private fun PromoCardSkeleton() {
    val skeletonColor = Color.LightGray.copy(alpha = 0.6f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(skeletonColor)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(24.dp)
                    .background(skeletonColor))
                Box(modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(18.dp)
                    .background(skeletonColor))
                Box(modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(16.dp)
                    .background(skeletonColor))
                Box(modifier = Modifier
                    .fillMaxWidth(1.0f)
                    .height(16.dp)
                    .background(skeletonColor))
            }
        }
    }
}

@Composable
private fun CategoryRow(
    categories: List<CategoryResponseGET>, // Usa tu modelo
    selectedTitulo: String?,
    onCategorySelected: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {

        item {
            CategoryItem(
                title = "Todos",
                imageUrl = null,
                isSelected = selectedTitulo == "Todos",
                onClick = { onCategorySelected("Todos") }
            )
        }

        items(categories) { category ->
            if (category.titulo.lowercase() != "todos" && category.titulo.lowercase() != "all") {
                CategoryItem(
                    title = category.titulo,
                    imageUrl = category.imagen,
                    isSelected = selectedTitulo == category.titulo,
                    onClick = { onCategorySelected(category.titulo) }
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(
    title: String,
    imageUrl: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(BackgroundGray)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) TealLight.copy(alpha = 0.8f) else BackgroundGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = title,
                    tint = if (isSelected) Color.White else TealPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) TealPrimary else Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp),
            fontFamily = PoppinsFamily
        )

        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .height(3.dp)
                .width(30.dp)
                .background(if (isSelected) TealPrimary else Color.Transparent)
        )
    }
}