package com.govAtizapan.beneficiojoven.view.home

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.govAtizapan.beneficiojoven.view.home.generadorQR.QRCodeGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerarQRView(navController: NavController, idCanje: Int?) {
    // 🧩 Estado que almacenará el bitmap del QR generado
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 🧩 Generar el QR cuando la pantalla se carga
    LaunchedEffect(idCanje) {
        idCanje?.let {
            qrBitmap = QRCodeGenerator.generarQR("CANJE-$it")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu código QR") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (qrBitmap != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        bitmap = qrBitmap!!.asImageBitmap(),
                        contentDescription = "Código QR",
                        modifier = Modifier.size(250.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Muestra este código en el establecimiento para validar tu canje.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Generando QR...")
                }
            }
        }
    }
}