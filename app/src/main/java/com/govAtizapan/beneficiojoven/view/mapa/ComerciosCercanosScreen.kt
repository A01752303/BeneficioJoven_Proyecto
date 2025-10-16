package com.govAtizapan.beneficiojoven.view.mapa

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.viewmodel.mapa.MapaViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview // Añadido para @Preview

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ComerciosCercanosScreen(navController: NavController) {
    val viewModel: MapaViewModel = viewModel()
    val ubicacionActual by viewModel.ubicacionActual.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (locationPermissionState.status.isGranted) {
            viewModel.cargarUbicacionActual()
        } else {
            locationPermissionState.launchPermissionRequest()
            viewModel.cargarUbicacionActual()
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = if (ubicacionActual != null) {
            CameraPosition.fromLatLngZoom(ubicacionActual!!, 14f)
        } else {
            CameraPosition.fromLatLngZoom(LatLng(19.557, -99.269), 14f)
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
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                    IconButton(onClick = { /* Ya en mapa */ }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa", tint = TealPrimary)
                    }
                    IconButton(onClick = { /* Buscar */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = { navController.navigate(AppScreens.CreatePromotionScreen.route) }) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = "Cupones")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Text(
                text = "Mapa de Ubicación",
                modifier = Modifier.padding(16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )

            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is MapaViewModel.UiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is MapaViewModel.UiState.Success -> {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            ubicacionActual?.let { latLng ->
                                Marker(
                                    state = MarkerState(position = latLng),
                                    title = "Tu ubicación"
                                )
                            }
                        }
                    }
                    is MapaViewModel.UiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error)
                            Button(onClick = { viewModel.cargarUbicacionActual() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.White)
            ) {
                Column {
                    Text(
                        text = "Información",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Ubicación actual mostrada en el mapa.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComerciosCercanosScreenPreview() {
    val navController = rememberNavController()
    com.govAtizapan.beneficiojoven.ui.theme.BeneficioJovenTheme {
        ComerciosCercanosScreen(navController)
    }
}