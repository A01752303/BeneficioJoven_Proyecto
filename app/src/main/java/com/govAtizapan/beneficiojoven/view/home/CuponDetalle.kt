package com.govAtizapan.beneficiojoven.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.view.home.homeViews.TealPrimary
import com.govAtizapan.beneficiojoven.viewmodel.cupondetalle.CuponDetalleViewModel
import com.govAtizapan.beneficiojoven.viewmodel.generarqr.GenerarQRViewModel

val PrimaryColor = Color(0xFF5d548f)
val BackgroundLight = Color(0xFFF8F9FA)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuponDetalleView(
    navController: NavController,
    promo: PromotionResponseGET,
    detalleViewModel: CuponDetalleViewModel = viewModel(),
    qrViewModel: GenerarQRViewModel = viewModel()
) {
    val isIdLoading by detalleViewModel.isLoading.collectAsState()
    val snackbarMessage by detalleViewModel.snackbarMessage.collectAsState()
    val idCanje by detalleViewModel.idCanje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }


    val qrBitmap by qrViewModel.qrBitmap.collectAsState()
    val isQrLoading by qrViewModel.isqrLoading.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            detalleViewModel.clearSnackbar()
        }
    }

    LaunchedEffect(idCanje) {
        idCanje?.let {
            qrViewModel.generarQR(it)
            isSheetOpen = true
            detalleViewModel.clearIdCanje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle del cupón", fontWeight = FontWeight.Bold, color = Color.White,
                        fontFamily = PoppinsFamily
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundLight
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(PrimaryColor),
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(
                    model = promo.imagen,
                    contentDescription = "Imagen de la promoción",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 300f
                            )
                        )
                )
                Text(
                    text = promo.nombre,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PoppinsFamily,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                )
            }


            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                InfoCard(
                    icon = Icons.Default.Description,
                    title = "Descripción del cupón",
                    content = {
                        Text(
                            text = promo.descripcion,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = PoppinsFamily,
                            color = Color(0xFF444444)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                InfoCard(
                    icon = Icons.Default.CalendarToday,
                    title = "Vigencia",
                    content = {
                        Text(
                            text = "Del: ${promo.fecha_inicio.take(10)}\nAl: ${promo.fecha_fin.take(10)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = PoppinsFamily,
                            color = Color(0xFF444444),
                            lineHeight = 24.sp
                        )
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Button(
                    onClick = { detalleViewModel.generarQr(promo.id) },
                    enabled = !isIdLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    if (isIdLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Obteniendo folio...")
                    } else {
                        Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Generar código QR", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, fontFamily = PoppinsFamily)
                    }
                }
            }
        }
    }

    if (isSheetOpen) {

        val sheetContainerColor = TealPrimary

        ModalBottomSheet(
            onDismissRequest = {
                isSheetOpen = false
                qrViewModel.clearQrData()
            },
            sheetState = sheetState,
            containerColor = sheetContainerColor,
            modifier = Modifier.navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isQrLoading -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Generando QR...", color = Color.White, fontFamily = PoppinsFamily)
                        }
                    }
                    qrBitmap != null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "¡Tu QR está listo!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PoppinsFamily,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Image(
                                bitmap = qrBitmap!!.asImageBitmap(),
                                contentDescription = "Código QR",
                                modifier = Modifier
                                    .size(250.dp)
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .padding(12.dp) // Padding interno
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Muestra este código en el establecimiento para validar tu canje.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                fontFamily = PoppinsFamily,
                                lineHeight = 24.sp
                            )

                        }
                    }
                    else -> {
                        Text(
                            "No se pudo generar el código QR.",
                            color = Color.White,
                            fontFamily = PoppinsFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = PoppinsFamily,
                    color = Color(0xFF212121)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                color = Color.Gray.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}