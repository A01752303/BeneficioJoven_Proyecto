package com.govAtizapan.beneficiojoven.view.registro

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.govAtizapan.beneficiojoven.R
import com.govAtizapan.beneficiojoven.ui.theme.*
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.view.navigation.REGISTRATION_GRAPH_ROUTE
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.EmailVerificationVM
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.RegistrationUiState
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.CustomTextField
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.SimpleTopAppBar
import com.govAtizapan.beneficiojoven.viewmodel.registerUserVM.RegisterUserVM

@Composable
fun DireccionRegistroView(
    navController: NavController,
    viewModel: EmailVerificationVM,
    viewModel2: RegisterUserVM
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ui by viewModel2.ui.collectAsState()

    // Estado local para los campos de texto de la dirección
    var calle by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var colonia by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }

    // Este efecto se encarga de la navegación final cuando el registro se completa
    LaunchedEffect(uiState) {
        if (uiState is RegistrationUiState.RegistrationComplete) {
            navController.navigate(AppScreens.HomeView.route) {
                // Limpia el historial de registro para que el usuario no pueda volver atrás
                popUpTo(REGISTRATION_GRAPH_ROUTE) {
                    inclusive = true
                }
            }
            viewModel.resetUiState() // Resetea el estado para futuros usos
        }
    }

    Scaffold(
        topBar = { SimpleTopAppBar(onBackClick = { navController.popBackStack() }) },
        containerColor = White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Por último, ¿cuál es tu dirección?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary,
                    fontSize = 18.sp
                )
                Text(
                    text = "Usamos estos datos para validar tu cuenta. Solo se aceptan direcciones de Atizapán de Zaragoza.",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Left,
                    color = Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                // --- CAMPOS DE TEXTO CORREGIDOS PARA LA DIRECCIÓN ---
                CustomTextField(label = "Calle", value = calle, onValueChange = { calle = it })
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(label = "Número (ext. o int.)", value = numero, onValueChange = { numero = it })
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(label = "Colonia", value = colonia, onValueChange = { colonia = it })
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    label = "Código Postal",
                    value = codigoPostal,
                    onValueChange = { if (it.length <= 5) codigoPostal = it }
                )

                Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

                Button(
                    onClick = {
                        viewModel2.updateAddressUser(calle, numero, colonia, codigoPostal)
                        val body = viewModel2.registerUserData.value
                        Log.d("RegistroDebug", "Datos actuales: ${viewModel2.registerUserData.value}")
                        // 2. Llama a la función final para "guardar en SQL"
                        viewModel2.createBusinessUser(body, navController)
                    },
                    // El botón se habilita solo si todos los campos están llenos y no está cargando
                    enabled = calle.isNotBlank() && numero.isNotBlank() && colonia.isNotBlank() && codigoPostal.length == 5 && uiState !is RegistrationUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Finalizar Registro", fontSize = 18.sp)
                }
            }

            // Muestra un indicador de carga mientras se guardan los datos finales
            if (ui.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(White.copy(alpha = 0.5f))
                        .clickable(enabled = false, onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.loading_animation)
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(800.dp)
                    )
                }
            }
        }
    }
}
