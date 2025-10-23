package com.govAtizapan.beneficiojoven.view.mapa

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.*
import com.govAtizapan.beneficiojoven.R // Asegúrate que este import sea correcto
import com.govAtizapan.beneficiojoven.model.obtenerDatosUsuario.ObtenerUsuarioResponseGET
import com.govAtizapan.beneficiojoven.model.obtenerDatosUsuario.UserRepository
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily // Importa PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

data class Business(val name: String, val latLng: LatLng, val address: String?)

@OptIn(ExperimentalMaterial3Api::class) // Añadido para ModalBottomSheet
@Composable
fun ComerciosCercanosScreen(navController: NavController) {
    // Propiedades del mapa (sin cambios)
    val defaultLocation = LatLng(19.4326, -99.1332)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    // Estado para permisos, negocios y Google Play Services (sin cambios)
    var hasLocationPermission by remember { mutableStateOf(checkLocationPermission(context)) }
    var businesses by remember { mutableStateOf<List<Business>>(emptyList()) }
    var isGooglePlayServicesAvailable by remember { mutableStateOf(checkGooglePlayServices(context)) }
    var isPlacesInitialized by remember { mutableStateOf(Places.isInitialized()) }
    val placesClient = remember { Places.createClient(context) }

    // Estado para manejar la navegación a Google Maps (sin cambios)
    var selectedBusiness by remember { mutableStateOf<Business?>(null) }

    // --- INICIO DE CAMBIOS ---
    // 1. Estados para el BottomSheet y UserData
    var showBottomSheet by remember { mutableStateOf(false) }
    val userRepository = remember { UserRepository() }
    var userData by remember { mutableStateOf<ObtenerUsuarioResponseGET?>(null) }
    // --- FIN DE CAMBIOS ---

    // Lanzador para permisos (sin cambios)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // ... (lógica sin cambios)
        hasLocationPermission = isGranted
        if (isGranted) {
            Log.d("ComerciosCercanos", "Permiso concedido, intentando obtener ubicación")
            coroutineScope.launch(Dispatchers.IO) {
                fetchUserLocationAndBusinesses(
                    context,
                    fusedLocationClient,
                    placesClient,
                    cameraPositionState,
                    { newBusinesses ->
                        businesses = newBusinesses
                        Log.d("ComerciosCercanos", if (newBusinesses.isNotEmpty()) {
                            "Mostrando ${newBusinesses.size} negocios cercanos."
                        } else {
                            "No se encontraron negocios cercanos."
                        })
                    }
                )
            }
        } else {
            Log.w("ComerciosCercanos", "Permiso de ubicación denegado por el usuario")
            cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
        }
    }

    // Lanzador para abrir Google Maps (sin cambios)
    val mapsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        Log.d("ComerciosCercanos", "Google Maps intent launched")
    }

    // Efecto para manejar la selección de un negocio y abrir Google Maps (sin cambios)
    LaunchedEffect(selectedBusiness) {
        // ... (lógica sin cambios)
        selectedBusiness?.let { business ->
            try {
                val gmmIntentUri = Uri.parse("google.navigation:q=${business.latLng.latitude},${business.latLng.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    mapsLauncher.launch(mapIntent)
                    Log.d("ComerciosCercanos", "Iniciando navegación a ${business.name}")
                } else {
                    Log.e("ComerciosCercanos", "Google Maps no está instalado")
                }
            } catch (e: Exception) {
                Log.e("ComerciosCercanos", "Error al abrir Google Maps: ${e.message}")
            }
            selectedBusiness = null
        }
    }

    // Verificar servicios y permisos al cargar (lógica de mapa sin cambios)
    LaunchedEffect(Unit) {
        if (!isGooglePlayServicesAvailable) {
            Log.e("ComerciosCercanos", "Google Play Services no está disponible o está desactualizado")
            return@LaunchedEffect
        }

        if (!isPlacesInitialized) {
            Log.e("ComerciosCercanos", "Places API no está inicializado")
            return@LaunchedEffect
        }

        Log.d("ComerciosCercanos", "Iniciando verificación de permisos")
        if (!hasLocationPermission) {
            Log.d("ComerciosCercanos", "Solicitando permiso de ubicación")
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            Log.d("ComerciosCercanos", "Permiso ya concedido, obteniendo ubicación")
            coroutineScope.launch(Dispatchers.IO) {
                fetchUserLocationAndBusinesses(
                    context,
                    fusedLocationClient,
                    placesClient,
                    cameraPositionState,
                    { newBusinesses ->
                        businesses = newBusinesses
                        Log.d("ComerciosCercanos", if (newBusinesses.isNotEmpty()) {
                            "Mostrando ${newBusinesses.size} negocios cercanos."
                        } else {
                            "No se encontraron negocios cercanos."
                        })
                    }
                )
            }
        }
    }

    // --- INICIO DE CAMBIOS ---
    // 2. LaunchedEffect para cargar userData
    LaunchedEffect(userRepository) {
        userData = userRepository.fetchUserData()
    }

    // 3. Envolvemos el Scaffold en un Box para mostrar el ModalBottomSheet
    Box(modifier = Modifier.fillMaxSize()) {
        // --- FIN DE CAMBIOS ---

        Scaffold(
            // --- INICIO DE CAMBIOS ---
            // 4. REEMPLAZO DEL BOTTOMBAR
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    // 1. Botón Home
                    NavigationBarItem(
                        selected = false, // Home no está seleccionado
                        onClick = { navController.navigate(AppScreens.HomeView.route) },
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontFamily = PoppinsFamily) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            selectedTextColor = TealPrimary,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    // 2. Botón Tarjeta
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            showBottomSheet = true // Abre el panel
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.logo_sinnombre), // Tu logo
                                contentDescription = "Tarjeta",
                                modifier = Modifier.size(24.dp), // Tamaño
                            )
                        },
                        label = { Text("Tarjeta", fontFamily = PoppinsFamily) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            selectedTextColor = TealPrimary,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )

                    // 3. Botón Mapa
                    NavigationBarItem(
                        selected = true, // Mapa SÍ está seleccionado
                        onClick = { Log.d("ComerciosCercanos", "Ya en Mapa") },
                        icon = {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Mapa"
                            )
                        },
                        label = { Text("Mapa", fontFamily = PoppinsFamily) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            selectedTextColor = TealPrimary,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
            // --- FIN DE CAMBIOS ---
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // El padding del Scaffold se aplica aquí
            ) {
                Text(
                    text = "Mapa de Ubicación",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )

                if (isGooglePlayServicesAvailable && isPlacesInitialized) {
                    Box(modifier = Modifier.weight(1f)) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            // Marcador de la ubicación actual
                            if (hasLocationPermission) {
                                cameraPositionState.position.target.let { latLng ->
                                    Marker(
                                        state = MarkerState(position = latLng),
                                        title = "Ubicación Actual",
                                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                                    )
                                }
                            }
                            // Marcadores de negocios cercanos
                            businesses.forEach { business ->
                                Marker(
                                    state = MarkerState(position = business.latLng),
                                    title = business.name,
                                    snippet = business.address,
                                    onClick = {
                                        selectedBusiness = business
                                        true
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (!isGooglePlayServicesAvailable) {
                                "Google Play Services no disponible. Por favor, actualiza o instala."
                            } else {
                                "Error al inicializar Places API. Verifica la clave API."
                            },
                            color = Color.Red,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Panel inferior con controles (sin cambios)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!hasLocationPermission) {
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Text("Permiso de ubicación")
                            }
                        }
                        Button(
                            onClick = {
                                if (!hasLocationPermission) {
                                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                } else {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        try {
                                            val location = fusedLocationClient.lastLocation.await()
                                            if (location != null) {
                                                val userLocation = LatLng(location.latitude, location.longitude)
                                                withContext(Dispatchers.Main) {
                                                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation, 15f)
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Log.e("ComerciosCercanos", "Error al recentrar: ${e.message}")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = if (hasLocationPermission) 0.dp else 8.dp)
                        ) {
                            Text("Mi ubicación")
                        }
                    }
                }
            }
        } // Fin del Scaffold

        // --- INICIO DE CAMBIOS ---
        // 5. AÑADIMOS EL MODALBOTTOMSHEET (dentro del Box, después del Scaffold)
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false // Para que se cierre al tocar afuera
                }
            ) {
                // Contenido del panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Título
                    Text(
                        text = "MI TARJETA BENEFICIO JOVEN",
                        fontSize = 18.sp,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Imagen de la Tarjeta
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1.586f),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tarjeta),
                            contentDescription = "Mi Tarjeta",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. Código Generado
                    // ¡OJO! Asegúrate que 'userData?.id' sea el campo correcto (ej: userData?.id_usuario)
                    val userId = userData?.id ?: 0
                    val cardNumber = generateCardNumber(userId)

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = cardNumber,
                            fontSize = 18.sp,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            letterSpacing = 0.1.em,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. Datos de Contacto
                    Text(
                        text = "Atención a Clientes",
                        fontSize = 16.sp,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tel: 55-16-68-17-48",
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        color = Color.Gray
                    )
                    Text(
                        text = "Avenida del parque SN, Jardines de Atizapán, Atizapán de Zaragoza",
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }
        // --- FIN DE CAMBIOS ---

    } // Fin del Box
}

// Las funciones auxiliares del mapa (sin cambios)
fun checkLocationPermission(context: Context): Boolean {
    // ... (lógica sin cambios)
    val result = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    Log.d("ComerciosCercanos", "Permiso de ubicación: $result")
    return result
}

fun checkGooglePlayServices(context: Context): Boolean {
    // ... (lógica sin cambios)
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
    val isAvailable = resultCode == ConnectionResult.SUCCESS
    Log.d("ComerciosCercanos", "Google Play Services disponible: $isAvailable, Código: $resultCode")
    return isAvailable
}

suspend fun fetchUserLocationAndBusinesses(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    placesClient: PlacesClient,
    cameraPositionState: CameraPositionState,
    updateBusinesses: (List<Business>) -> Unit
) {
    // ... (lógica sin cambios)
    if (!checkLocationPermission(context)) {
        Log.w("ComerciosCercanos", "No hay permiso de ubicación. Abortando.")
        updateBusinesses(emptyList())
        return
    }

    try {
        Log.d("ComerciosCercanos", "Intentando obtener ubicación")
        val location = fusedLocationClient.lastLocation.await()
        if (location != null) {
            val userLocation = LatLng(location.latitude, location.longitude)
            withContext(Dispatchers.Main) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation, 15f)
            }
            Log.d("ComerciosCercanos", "Ubicación obtenida: $userLocation")
            val businesses = fetchNearbyBusinesses(placesClient, userLocation)
            updateBusinesses(businesses)
        } else {
            Log.w("ComerciosCercanos", "Ubicación nula. Servicios de ubicación desactivados?")
            updateBusinesses(emptyList())
        }
    } catch (e: Exception) {
        Log.e("ComerciosCercanos", "Error al obtener ubicación: ${e.message}")
        updateBusinesses(emptyList())
    }
}

suspend fun fetchNearbyBusinesses(placesClient: PlacesClient, location: LatLng): List<Business> {
    // ... (lógica sin cambios)
    Log.d("ComerciosCercanos", "Buscando negocios cerca de: $location")
    val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.TYPES)
    val request = FindCurrentPlaceRequest.newInstance(placeFields)

    return try {
        val response = placesClient.findCurrentPlace(request).await()
        val businesses = response.placeLikelihoods
            .filter { it.place.types?.contains(Place.Type.STORE) == true }
            .mapNotNull { likelihood ->
                val place = likelihood.place
                place.latLng?.let { latLng ->
                    Business(
                        name = place.name ?: "Negocio sin nombre",
                        latLng = latLng,
                        address = place.address ?: "Sin dirección"
                    )
                }
            }
            .take(10)
        Log.d("ComerciosCercanos", "Negocios encontrados: ${businesses.size}")
        businesses
    } catch (e: Exception) {
        Log.e("ComerciosCercanos", "Error en Places API: ${e.message}")
        emptyList()
    }
}

// --- INICIO DE CAMBIOS ---
// 6. AÑADIMOS LA FUNCIÓN PARA GENERAR EL CÓDIGO
private fun generateCardNumber(id: Int): String {
    // 1. Define el número base
    val baseNumber = 1234567890120000L

    // 2. Suma el ID del usuario al número base
    val fullCode = baseNumber + id.toLong()

    // 3. Convierte a String, asegurando que tenga 16 dígitos (rellenando con 0s a la izquierda si fuera necesario)
    val codeString = fullCode.toString().padStart(16, '0')

    // 4. Agrupa en bloques de 4 dígitos
    return codeString.chunked(4).joinToString(" ")
}
// --- FIN DE CAMBIOS ---