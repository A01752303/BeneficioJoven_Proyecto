package com.govAtizapan.beneficiojoven.view.registro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.govAtizapan.beneficiojoven.R
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.EmailVerificationVM
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.RegistrationUiState

@Composable
fun EmailVerificacion(navController: NavController) {
    EmailVerificacionView(onUserVerified = {
        navController.popBackStack()
        navController.navigate(AppScreens.NombreRegistro.route)},
        modifier = Modifier)
}

@Composable
fun EmailVerificacionView(
    // 1. Cambios en la firma: añadimos el evento de navegación y el ViewModel
    onUserVerified: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmailVerificationVM = viewModel()
) {
    // 2. Obtenemos los estados del ViewModel
    // uiState nos dirá cuándo navegar
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // registrationData nos da el email del usuario para mostrarlo
    val registrationData by viewModel.registrationData.collectAsStateWithLifecycle()

    // 3. Efectos para controlar la lógica de verificación
    // Este se encarga de la navegación cuando el estado cambia a UserVerified
    LaunchedEffect(uiState) {
        if (uiState is RegistrationUiState.UserVerified) {
            onUserVerified()
            viewModel.resetUiState()
        }
    }

    // Este inicia la comprobación periódica cuando la pantalla se muestra
    LaunchedEffect(Unit) {
        viewModel.startVerificationCheck()
    }

    // Este detiene la comprobación si el usuario sale de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopVerificationCheck()
        }
    }

    // Tu UI, ahora con datos dinámicos y un indicador de progreso
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ilustracioncupones_email_01),
            contentDescription = "Ilustración de email",
            modifier = Modifier.height(150.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "¡Revisa tu bandeja de entrada!",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = TealPrimary,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            // 4. Mostramos el email real del usuario
            text = "Te enviamos un enlace de verificación a ${registrationData.email} para continuar.",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.loading_animation) // Usa el nombre de tu archivo
        )
        // 2. Muestra la animación
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever, // Para que se repita infinitamente
            modifier = Modifier.size(800.dp) // Ajusta el tamaño como quieras
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmailVerificacionPreview() {
    EmailVerificacionView(onUserVerified = {})
}