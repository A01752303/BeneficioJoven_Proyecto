package com.govAtizapan.beneficiojoven.view.validarqr

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.govAtizapan.beneficiojoven.viewmodel.validaqr.QrResultType
import com.govAtizapan.beneficiojoven.viewmodel.validaqr.ValidarQRViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ValidarQRView(viewModel: ValidarQRViewModel = viewModel()) {

    val permissions = rememberMultiplePermissionsState(listOf(Manifest.permission.CAMERA))
    val uiState by viewModel.uiState.collectAsState()

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        val contenido = result.contents ?: ""
        if (contenido.isNotEmpty()) {
            Log.d("ValidarQRView", "QR escaneado: $contenido")
            viewModel.validarCodigoQR(contenido)
        }
    }

    LaunchedEffect(Unit) {
        permissions.launchMultiplePermissionRequest()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Validar Código QR", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0096A6))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(uiState.colorFondo),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.tipoResultado) {
                QrResultType.NONE -> {
                    // 🕹 Pantalla inicial
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Escanear",
                            tint = Color(0xFF0096A6),
                            modifier = Modifier.size(120.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val options = ScanOptions()
                                options.setBeepEnabled(true)
                                options.setOrientationLocked(true)
                                options.setCaptureActivity(CaptureActivityPortrait::class.java)
                                scanLauncher.launch(options)
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0096A6))
                        ) {
                            Text("Escanear QR", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                QrResultType.SUCCESS -> {
                    // 🟩 Pantalla verde con palomita
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Canje válido",
                            tint = Color.White,
                            modifier = Modifier.size(160.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Canje válido",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = { viewModel.resetearEstado() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Volver", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                QrResultType.ERROR -> {
                    // 🟥 Pantalla roja con X
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cupón inválido",
                            tint = Color.White,
                            modifier = Modifier.size(160.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Cupón inválido",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = { viewModel.resetearEstado() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Volver", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}