package com.govAtizapan.beneficiojoven.view.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi // <-- Importante
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.decode.SvgDecoder
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer // <-- Import corregido
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import com.govAtizapan.beneficiojoven.model.obtenerDatosUsuario.ObtenerUsuarioResponseGET
import com.govAtizapan.beneficiojoven.model.obtenerDatosUsuario.UserRepository

val TealPrimary = Color(0xFF5d548f)
val TealLight = Color(0xFF5d548f)
val BackgroundGray = Color(0xFFF5F5F5)
val CardBackground = Color(0xFFFFFFFF)

enum class SortOption(val label: String) {
    NEWEST("Más nuevos"),
    EXPIRING_SOON("Por caducar")
}

val couponTypes = listOf(
    "Todos",
    "Descuento",
    "Precio fijo",
    "2x1",
    "Trae un amigo",
    "Otra"
)

const val EXPIRATION_THRESHOLD_DAYS: Long = 3

// --- AÑADIMOS ExperimentalFoundationApi ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

    // --- ESTADO PARA DATOS DE USUARIO ---
    val userRepository = remember { UserRepository() }
    var userData by remember { mutableStateOf<ObtenerUsuarioResponseGET?>(null) }
    // --- FIN DE ESTADO ---

// val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()) // <-- ELIMINADO

// --- Estado para el Navigation Drawer ---
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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

    LaunchedEffect(userRepository) {
        userData = userRepository.fetchUserData()
    }

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
                        "Descuento" -> apiType == "porcentaje"
                        "Precio fijo" -> apiType == "precio"
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

// --- CORRECCIÓN: Envolver Scaffold en ModalNavigationDrawer ---
    // --- CORRECCIÓN: Envolver Scaffold en ModalNavigationDrawer ---
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // --- REEMPLAZAMOS EL 'Box' ANTERIOR POR UN 'Column' CON LOS DATOS ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TealPrimary)
                        .padding(16.dp),
                    // Alineamos el contenido a la izquierda (inicio)
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(Modifier.height(24.dp)) // Espacio superior

                    // Círculo de Perfil
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            // Un fondo semi-transparente para el círculo
                            .background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    val currentData = userData
                    Log.d("HomeView", "Valor de userData en el Drawer: $currentData")

                    val nombreCompleto = if (currentData != null) {
                        "${currentData.nombre} ${currentData.apellido_paterno} ${currentData.apellido_materno}".trim()
                    } else {
                        "Cargando..."
                    }

                    Text(
                        text = nombreCompleto,
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = userData?.correo ?: "...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f)),
                        fontFamily = PoppinsFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(16.dp)) // Espacio inferior
                }
                // --- FIN DEL NUEVO ENCABEZADO DEL DRAWER ---

                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil") },
                    label = { Text("Mi Perfil", fontFamily = PoppinsFamily) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        // TODO: Navegar a la pantalla de perfil si es necesario
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    label = { Text("Ajustes", fontFamily = PoppinsFamily) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Acerca de") },
                    label = { Text("Acerca de", fontFamily = PoppinsFamily) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión") },
                    label = { Text("Cerrar Sesión", fontFamily = PoppinsFamily) },
                    selected = false,
                    onClick = {
                        navController.navigate(AppScreens.BienvenidaView.route) {
                            // Limpia la pila de navegación para que el usuario no pueda volver
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) { // --- El contenido del Drawer es el Scaffold ---

        Scaffold(
// modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // <-- ELIMINADO
            topBar = {
// ... (TopAppBar sin cambios)
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú",
                                tint = Color.White
                            )
                        }
                    },
                    title = {
                        Text(
                            "Home",
                            color = Color.White,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = TealPrimary,
                    ),
                )
            },
            bottomBar = {
// ... (BottomAppBar sin cambios)
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

// --- NUEVA ESTRUCTURA DE COLUMNA ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()) // <-- Padding del TopAppBar
            ) {

// --- 1. BARRA DE BÚSQUEDA (FIJA) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TealPrimary) // Fondo para que combine con el TopAppBar
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = PoppinsFamily,
                            color = White,
                        ),
                        placeholder = {
                            Text(
                                "Busca tu cupón...",
                                color = Color.White.copy(alpha = 0.7f),
                                fontFamily = PoppinsFamily,
                                fontSize = 14.sp,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Limpiar",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
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
// --- FIN DE LA BARRA DE BÚSQUEDA ---

// --- 2. FILTROS (FIJOS) ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White, // Fondo blanco para los filtros
                            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                        )
                        .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                        .padding(top = 16.dp) // <-- AÑADIDO: Espacio superior
                ) {
                    SectionTitle("Filtros")
                    FilterChipsRow(
                        selectedType = selectedCouponType,
                        onTypeSelected = { selectedCouponType = it },
                        selectedOption = sortOption,
                        onOptionSelected = { sortOption = it }
                    )
                }
// --- 👆 FIN DEL CONTENIDO FIJO ---

// --- 3. CONTENIDO DESLIZABLE (SCROLLABLE) ---
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f) // <-- Ocupa todo el espacio restante
                ) {
                    when {
                        isLoading -> {
                            SkeletonList(
                                PaddingValues(bottom = innerPadding.calculateBottomPadding())
                            )
                        }
                        errorState != null && promociones.isEmpty() -> {
                            ErrorStateView(
                                message = errorState ?: "Ocurrió un error desconocido.",
                                onRetry = { viewModel.cargarPromociones() },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = innerPadding.calculateBottomPadding())
                            )
                        }

// 'else' AHORA CUBRE EL ESTADO VACÍO Y EL ESTADO CON DATOS
                        else -> {
                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
// top = 16.dp, // <-- ELIMINADO
// start = 16.dp, // <-- ELIMINADO
// end = 16.dp, // <-- ELIMINADO
                                    bottom = innerPadding.calculateBottomPadding() + 12.dp
                                ),
// verticalArrangement = Arrangement.spacedBy(16.dp) // <-- ELIMINADO
                            ) {

// --- 1. CATEGORÍAS (SIEMPRE VISIBLES) ---
                                item(key = "category_row") {
                                    CategoryRow(
                                        categories = categorias,
                                        selectedTitulo = selectedCategoryTitulo,
                                        onCategorySelected = { categoryTitulo ->
                                            selectedCategoryTitulo = categoryTitulo
                                        }
                                    )
                                }

// --- 2. CONTENIDO CONDICIONAL ---
                                if (filteredAndSortedPromos.isEmpty()) {
// --- ESTADO VACÍO (DENTRO DE LA LISTA) ---
                                    item(key = "empty_state") {
                                        Spacer(modifier = Modifier.height(16.dp)) // <-- AÑADIDO
                                        EmptyStateView(
                                            searchQuery,
                                            selectedCategoryTitulo,
                                            modifier = Modifier
                                                .fillParentMaxHeight(0.7f) // Ocupa 70% del espacio
                                                .padding(horizontal = 16.dp) // <-- AÑADIDO
                                        )
                                    }
                                } else {
// --- LISTA DE RESULTADOS ---
                                    item(key = "results_count") {
                                        Spacer(modifier = Modifier.height(16.dp)) // <-- AÑADIDO
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
                                            modifier = Modifier
                                                .padding(bottom = 4.dp)
                                                .padding(horizontal = 16.dp) // <-- AÑADIDO
                                        )
                                    }

                                    items(filteredAndSortedPromos, key = { it.id }) { promo ->
                                        Spacer(modifier = Modifier.height(16.dp)) // <-- AÑADIDO

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

// --- ENVOLTURA AÑADIDA ---
                                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                            PromoCard(
                                                promo = promo,
                                                isExpiringSoon = isExpiringSoon,
                                                daysRemaining = daysRemaining,
                                                onClick = {
                                                    navController.navigate("detalleCupon/${promo.id}")
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } // --- Aquí cierra el ModalNavigationDrawer ---
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

// --- INICIO DE LÓGICA DE PRECIO/DESCUENTO ---
                val tipo = promo.tipo.lowercase().trim()
                val precioDouble = promo.precio.toDoubleOrNull() ?: 0.0
                val precioValido = precioDouble > 0.0
                var showSpacer = false

                when (tipo) {
                    "precio" -> {
                        if (precioValido) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Precio: ",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF666666),
                                    fontFamily = PoppinsFamily,
                                )
                                val precioFormateado = String.format("%.2f", precioDouble)
                                Text(
                                    text = "$$precioFormateado",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary,
                                    fontFamily = PoppinsFamily
                                )
                            }
                            showSpacer = true
                        }
                    }
                    "porcentaje" -> {
// Convertimos el string de porcentaje a Double
                        val porcentajeDouble = promo.porcentaje.toDoubleOrNull() ?: 0.0
                        val porcentajeValido = porcentajeDouble > 0.0

                        if (porcentajeValido) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Descuento: ",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF666666),
                                    fontFamily = PoppinsFamily,
                                )

// Formatear el porcentaje (ej. 20.0 -> "20", 20.5 -> "20.5")
                                val descuentoFormateado = if (porcentajeDouble % 1.0 == 0.0) {
// Si es un número entero (termina en .0)
                                    porcentajeDouble.toInt().toString()
                                } else {
// Si tiene decimales
                                    String.format("%.1f", porcentajeDouble)
                                }

                                Text(
                                    text = "$descuentoFormateado%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary,
                                    fontFamily = PoppinsFamily
                                )
                            }
                            showSpacer = true
                        }
                    }
                    "2x1" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Promoción: ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF666666),
                                fontFamily = PoppinsFamily,
                            )
                            Text(
                                text = "2x1",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                fontFamily = PoppinsFamily
                            )
                        }
                        showSpacer = true
                    }
                    "trae un amigo" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Promoción: ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF666666),
                                fontFamily = PoppinsFamily,
                            )
                            Text(
                                text = "Trae un amigo",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                fontFamily = PoppinsFamily,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        showSpacer = true
                    }
                    "otra" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Promoción: ",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF666666),
                                fontFamily = PoppinsFamily,
                            )
                            Text(
                                text = "Especial",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                fontFamily = PoppinsFamily
                            )
                        }
                        showSpacer = true
                    }
                    else -> {
// Fallback: Si el tipo no se reconoce, pero hay un precio, mostrarlo.
                        if (precioValido) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Precio: ",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF666666),
                                    fontFamily = PoppinsFamily,
                                )
                                val precioFormateado = String.format("%.2f", precioDouble)
                                Text(
                                    text = "$$precioFormateado",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary,
                                    fontFamily = PoppinsFamily
                                )
                            }
                            showSpacer = true
                        }
                    }
                }

                if (showSpacer) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
// .fillMaxSize() // <-- MODIFICADO: Ya no llena toda la pantalla
            .padding(16.dp)
            .height(200.dp), // Damos una altura fija para centrarlo
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
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
// Simula el espacio de las categorías
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // Altura aproximada de la fila de categorías
                    .background(Color.LightGray.copy(alpha = 0.6f), shape = RoundedCornerShape(16.dp))
            )
        }
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
    categories: List<CategoryResponseGET>,
    selectedTitulo: String?,
    onCategorySelected: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White), // <-- Fondo blanco
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        item {
            CategoryItem(
                title = "Todos",
                imageUrl = "",
                isSelected = selectedTitulo == "Todos",
                onClick = { onCategorySelected("Todos") }
            )
        }

        items(categories) { category ->
            if (category.titulo.lowercase() != "todos" && category.titulo.lowercase() != "all") {
                CategoryItem(
                    title = category.titulo,
                    imageUrl = category.image,
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
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!imageUrl.isNullOrBlank()) {
            val imageLoader = ImageLoader.Builder(context)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()

            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                imageLoader = imageLoader,
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