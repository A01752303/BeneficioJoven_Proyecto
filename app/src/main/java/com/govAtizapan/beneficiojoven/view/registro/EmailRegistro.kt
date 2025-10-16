package com.govAtizapan.beneficiojoven.view.registro

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.EmailVerificationVM
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.RegistrationUiState
import com.govAtizapan.beneficiojoven.R
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.SimpleTopAppBar
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.CustomTextField
import com.govAtizapan.beneficiojoven.viewmodel.registerUserVM.RegisterUserVM


@Composable
fun EmailRegistro(navController: NavController, viewModel: EmailVerificationVM, viewModel2: RegisterUserVM) {

    val registrationData by viewModel.registrationData.collectAsStateWithLifecycle()

    // 1. El estado ahora vive aquí, en el composable padre.
    var email by remember { mutableStateOf(registrationData.email) }
    var password by remember { mutableStateOf("") }

    EmailRegistroScreen(
        // 2. Pasamos el estado y los eventos hacia abajo, al composable de la UI.
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        onVerificationSent = {
            // 3. Ahora tenemos acceso al email para construir la ruta correcta.
            navController.navigate(AppScreens.EmailVerificacion.route + "?email=$email")
        },
        backClick = {
            navController.popBackStack()
        },
        viewModel = viewModel,
        viewModel2 = viewModel2
    )
}

// Este es el Composable que define tu UI. Ahora no tiene estado propio.
@Composable
fun EmailRegistroScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onVerificationSent: () -> Unit,
    backClick: () -> Unit,
    viewModel: EmailVerificationVM,
    viewModel2: RegisterUserVM
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is RegistrationUiState.VerificationEmailSent -> {
                onVerificationSent()
                viewModel.resetUiState()
            }
            is RegistrationUiState.Error -> {
                snackbarHostState.showSnackbar(message = state.message)
                viewModel.resetUiState()
            }
            else -> { /* No hacer nada en otros estados */ }
        }
    }

    Scaffold(
        topBar = { SimpleTopAppBar(onBackClick = backClick) },
        containerColor = White,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .safeDrawingPadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                var isPasswordVisible by remember { mutableStateOf(false) }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Primero, ¿Cuál es tu Email?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary,
                    fontSize = 18.sp
                )
                Text(
                    text = "Continua con tu correo electrónico y contraseña para crear tu cuenta exitosamente",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    color = Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    label = "Correo Electrónico",
                    value = email, // Usa el estado pasado como parámetro
                    onValueChange = onEmailChange, // Notifica al padre del cambio
                    textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    label = "Contraseña",
                    value = password, // Usa el estado pasado como parámetro
                    onValueChange = onPasswordChange, // Notifica al padre del cambio
                    textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light),
                    isPasswordField = true,
                    isPasswordVisible = isPasswordVisible,
                    onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo
                Button(
                    onClick = {
                        viewModel.registerUserAndSendVerification(email, password)
                        viewModel2.updateUserEmailData(email, password)
                        Log.d("RegistroDebug", "Datos actuales: ${viewModel2.registerUserData.value}")
                    },
                    enabled = email.isNotBlank() && password.isNotBlank() && uiState !is RegistrationUiState.Loading,
                    modifier = Modifier
                        .height(46.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = White
                    )
                ) {
                    Text("Siguiente",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp)
                }
            }

            if (uiState is RegistrationUiState.Loading) {
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


