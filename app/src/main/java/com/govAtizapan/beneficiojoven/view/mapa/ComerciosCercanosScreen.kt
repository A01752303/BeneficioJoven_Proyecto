package com.govAtizapan.beneficiojoven.view.mapa

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens

@Composable
fun ComerciosCercanosScreen(navController: NavController) {
    // Propiedades del mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(19.4326, -99.1332), 10f) // Por defecto: Ciudad de México
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

            Box(modifier = Modifier.weight(1f)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                )
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