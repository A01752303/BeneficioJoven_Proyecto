package com.govAtizapan.beneficiojoven.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border // Sigue aquí por si lo usas en otro lado, aunque no en la cabecera
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape // Sigue aquí por si lo usas en otro lado
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.govAtizapan.beneficiojoven.viewmodel.cupondetalle.CuponDetalleViewModel

// Definición de colores
val PrimaryColor = Color(0xFF5d548f)
val LightColor = Color(0xFF4DB8C4) // Se mantiene, aunque el degradado principal ya no lo usa
val BackgroundLight = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuponDetalleView(
    navController: NavController,
    promo: PromotionResponseGET,
    viewModel: CuponDetalleViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val idCanje by viewModel.idCanje.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val idUsuario = 1 // Considera obtener esto de un DataStore/ViewModel

    // Mostrar Snackbar cuando haya mensaje
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Navegar cuando haya idCanje generado
    LaunchedEffect(idCanje) {
        idCanje?.let {
            navController.navigate("generarQR/$it")
            viewModel.clearIdCanje()
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundLight
    ) { innerPadding ->

        // Estructura principal: Cabecera, Contenido (scroll), Botón (fijo)
        Column(
            modifier = Modifier
                .padding(innerPadding) // Aplica el padding del Scaffold aquí
                .fillMaxSize()
        ) {

            // 📸 Sección superior (Cabecera con imagen completa)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // Altura fija para la cabecera
                    .background(PrimaryColor), // Color de fondo mientras carga la imagen
                contentAlignment = Alignment.BottomCenter // Alinea el texto abajo
            ) {
                // Imagen de fondo que llena el Box
                AsyncImage(
                    model = promo.imagen,
                    contentDescription = "Imagen de la promoción",
                    modifier = Modifier.fillMaxSize(), // Llena el Box
                    contentScale = ContentScale.Crop // Recorta para llenar sin distorsionar
                )

                // Degradado sutil sobre la imagen para legibilidad del texto
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 300f // Inicia el degradado más abajo
                            )
                        )
                )

                // Texto del título superpuesto
                Text(
                    text = promo.nombre,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PoppinsFamily,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp) // Espaciado
                )
            }


            // 📋 Info (Contenido desplazable)
            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa el espacio restante
                    .verticalScroll(rememberScrollState()) // Permite el scroll
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                // Tarjeta de Descripción
                InfoCard(
                    icon = Icons.Default.Description,
                    title = "En qué consiste",
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

                // Tarjeta de Vigencia
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

            // 🎟️ Botón (Fijo en la parte inferior)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Button(
                    onClick = { viewModel.generarQr(idUsuario, promo.id) },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Generando QR...")
                    } else {
                        Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Generar código QR", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, fontFamily = PoppinsFamily)
                    }
                }
            }
        }
    }
}

/**
 * Un Composable reutilizable para mostrar secciones de información
 * de manera estructurada y visualmente atractiva.
 */
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
            // Fila para Icono y Título
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
            Divider(color = Color.Gray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            // Contenido (inyectado)
            content()
        }
    }
}