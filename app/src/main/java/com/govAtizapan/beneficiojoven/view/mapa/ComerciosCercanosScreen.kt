package com.govAtizapan.beneficiojoven.view.mapa

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

data class Business(val name: String, val latLng: LatLng, val address: String?)

@Composable
fun ComerciosCercanosScreen(navController: NavController) {
    // Propiedades del mapa
    val defaultLocation = LatLng(19.4326, -99.1332) // Ciudad de México por defecto
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    // Estado para permisos, negocios y Google Play Services
    var hasLocationPermission by remember { mutableStateOf(checkLocationPermission(context)) }
    var businesses by remember { mutableStateOf<List<Business>>(emptyList()) }
    var isGooglePlayServicesAvailable by remember { mutableStateOf(checkGooglePlayServices(context)) }
    var isPlacesInitialized by remember { mutableStateOf(Places.isInitialized()) }
    val placesClient = remember { Places.createClient(context) }

    // Estado para manejar la navegación a Google Maps
    var selectedBusiness by remember { mutableStateOf<Business?>(null) }

    // Lanzador para permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
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

    // Lanzador para abrir Google Maps
    val mapsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        Log.d("ComerciosCercanos", "Google Maps intent launched")
    }

    // Efecto para manejar la selección de un negocio y abrir Google Maps
    LaunchedEffect(selectedBusiness) {
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

    // Verificar servicios y permisos al cargar
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

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { navController.navigate(AppScreens.HomeView.route) }) {
                        Icon(Icons.Default.Home, contentDescription = "Inicio", tint = TealPrimary)
                    }
                    IconButton(onClick = { navController.navigate(AppScreens.ComerciosCercanosScreen.route) }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa", tint = TealPrimary)
                    }
                    IconButton(onClick = { navController.navigate(AppScreens.BienvenidaView.route) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = TealPrimary)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
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
                            Text("Reintentar permiso de ubicación")
                        }
                    }
                    Button(
                        onClick = {
                            if (!hasLocationPermission) {
                                Log.d("ComerciosCercanos", "Solicitando permiso para recentrar")
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            } else {
                                Log.d("ComerciosCercanos", "Intentando recentrar mapa")
                                coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        val location = fusedLocationClient.lastLocation.await()
                                        if (location != null) {
                                            val userLocation = LatLng(location.latitude, location.longitude)
                                            withContext(Dispatchers.Main) {
                                                cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation, 15f)
                                            }
                                            Log.d("ComerciosCercanos", "Mapa recentrado a: $userLocation")
                                        } else {
                                            Log.w("ComerciosCercanos", "Ubicación nula al recentrar")
                                        }
                                    } catch (e: SecurityException) {
                                        Log.e("ComerciosCercanos", "SecurityException al recentrar: ${e.message}")
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
                        Text("Volver a mi ubicación")
                    }
                }
            }
        }
    }
}

fun checkLocationPermission(context: Context): Boolean {
    val result = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    Log.d("ComerciosCercanos", "Permiso de ubicación: $result")
    return result
}

fun checkGooglePlayServices(context: Context): Boolean {
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
            // Obtener negocios cercanos
            val businesses = fetchNearbyBusinesses(placesClient, userLocation)
            updateBusinesses(businesses)
        } else {
            Log.w("ComerciosCercanos", "Ubicación nula. Servicios de ubicación desactivados?")
            updateBusinesses(emptyList())
        }
    } catch (e: SecurityException) {
        Log.e("ComerciosCercanos", "SecurityException al obtener ubicación: ${e.message}")
        updateBusinesses(emptyList())
    } catch (e: Exception) {
        Log.e("ComerciosCercanos", "Error al obtener ubicación: ${e.message}")
        updateBusinesses(emptyList())
    }
}

suspend fun fetchNearbyBusinesses(placesClient: PlacesClient, location: LatLng): List<Business> {
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
            .take(10) // Limitar a 10 negocios
        Log.d("ComerciosCercanos", "Negocios encontrados: ${businesses.size}")
        businesses
    } catch (e: SecurityException) {
        Log.e("ComerciosCercanos", "SecurityException en Places API: ${e.message}")
        emptyList()
    } catch (e: com.google.android.gms.common.api.ApiException) {
        Log.e("ComerciosCercanos", "ApiException en Places API: ${e.statusCode} - ${e.message}")
        emptyList()
    } catch (e: Exception) {
        Log.e("ComerciosCercanos", "Error en Places API: ${e.message}")
        emptyList()
    }
}