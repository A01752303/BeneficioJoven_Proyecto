package com.govAtizapan.beneficiojoven.view.registro

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

@Composable
fun EmailRegistro(navController: NavController, viewModel: EmailVerificationVM) {

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
        viewModel = viewModel
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
    viewModel: EmailVerificationVM
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
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String = "",
    isPasswordField: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {},
    textStyle: TextStyle = LocalTextStyle.current
) {
    val lightCyan = Color(0xFFE0F7FA)
    val borderCyan = Color(0xFFB2EBF2)

    Column(modifier = modifier) {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 16.sp,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Light,
            color = Color.Black
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = borderCyan, shape = RoundedCornerShape(16.dp))
                .defaultMinSize(minHeight = 48.dp),
            textStyle = textStyle,
            placeholder = { Text(placeholderText) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            visualTransformation = if (isPasswordField && !isPasswordVisible) {
                PasswordVisualTransformation() // Oculta el texto si es campo de contraseña y no es visible
            } else {
                VisualTransformation.None // Muestra el texto en cualquier otro caso
            },
            keyboardOptions = if (isPasswordField) {
                KeyboardOptions(keyboardType = KeyboardType.Password) // Teclado de contraseña
            } else {
                KeyboardOptions.Default // Teclado normal
            },
            trailingIcon = {
                if (isPasswordField) { // Muestra el ícono solo si es un campo de contraseña
                    val image = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = onVisibilityChange) {
                        Icon(imageVector = image,
                            contentDescription = "Toggle password visibility",
                            tint = TealPrimary)
                    }
                }
            },

            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = lightCyan,
                focusedContainerColor = lightCyan,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(
    // Pasamos una función lambda para manejar el evento de clic en la flecha
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { /* El título está vacío, como en tu diseño */ },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver atrás",
                    modifier = Modifier.size(35.dp),
                )
            }
        },
        // Configuramos los colores para que coincidan con tu diseño
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White, // Fondo de la barra
            navigationIconContentColor = TealPrimary// Color de la flecha
        ),
        modifier = Modifier.statusBarsPadding()
    )
}

