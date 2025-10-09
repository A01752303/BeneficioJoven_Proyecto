package com.govAtizapan.beneficiojoven.view.registro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.R
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.EmailVerificationVM
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.RegistrationUiState

@Composable
fun EmailVerificacion(navController: NavController, viewModel: EmailVerificationVM, email: String) {
    EmailVerificacionView(onUserVerified = {
        navController.popBackStack()
        navController.navigate(AppScreens.NombreRegistro.route)},
        modifier = Modifier,
        viewModel = viewModel,
        email = email)
}

@Composable
fun EmailVerificacionView(
    // 1. Cambios en la firma: añadimos el evento de navegación y el ViewModel
    onUserVerified: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmailVerificationVM,
    email: String
) {
    // 2. Obtenemos los estados del ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            text = "Te enviamos un enlace de verificación a $email para continuar.",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            // 4. Mostramos el email real del usuario
            text = "No olvides revisar tu spam",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Gray,
            fontSize = 14.sp
        )
    }
}
