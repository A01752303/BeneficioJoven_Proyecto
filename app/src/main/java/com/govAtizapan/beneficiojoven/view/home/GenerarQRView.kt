package com.govAtizapan.beneficiojoven.view.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerarQRView(navController: NavController, promoId: Int?) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Código QR") },

            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Aquí se generará el QR para el cupón ID: $promoId")
        }
    }
}