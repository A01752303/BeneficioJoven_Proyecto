package com.govAtizapan.beneficiojoven.view.validarqr
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import com.govAtizapan.beneficiojoven.model.qrvalidacion.QrValidateRequest
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import com.govAtizapan.beneficiojoven.view.validarqr.CaptureActivityPortrait

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ValidarQRView() {
    // 🔹 ID de establecimiento temporal (para pruebas)
    val idEstablecimientoLogueado = 1

    val permissions = rememberMultiplePermissionsState(listOf(Manifest.permission.CAMERA))
    val scope = rememberCoroutineScope()

    var mensaje by remember { mutableStateOf("") }
    var colorMensaje by remember { mutableStateOf(Color.Black) }

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        val contenido = result.contents ?: ""
        if (contenido.isNotEmpty()) {
            // 🧠 Muestra en consola el texto del QR
            android.util.Log.d("ValidarQRView", "QR escaneado: $contenido")
            scope.launch {
                try {
                    val response = RetrofitClient.ValidarQrApi.validarQr(
                        QrValidateRequest(
                            codigo = contenido,
                            idEstablecimiento = idEstablecimientoLogueado
                        )
                    )

                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.success == true) {
                            mensaje = "✅ Canje válido: ${body.message}"
                            colorMensaje = Color(0xFF009688)
                        } else {
                            mensaje = "🚫 ${body?.message ?: "Canje inválido"}"
                            colorMensaje = Color.Red
                        }
                    } else {
                        mensaje = "❌ Error del servidor (${response.code()})"
                        colorMensaje = Color.Red
                    }
                } catch (e: Exception) {
                    mensaje = "⚠️ Error de conexión: ${e.message}"
                    colorMensaje = Color.Red
                }
            }
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize(),
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
                    scanLauncher.launch(options)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0096A6))
            ) {
                Text("Escanear QR", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = colorMensaje,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}