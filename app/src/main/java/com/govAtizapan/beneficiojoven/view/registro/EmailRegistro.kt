package com.govAtizapan.beneficiojoven.view.registro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.EmailVerificationVM
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.RegistrationUiState
import com.govAtizapan.beneficiojoven.R


@Composable
fun EmailRegistro(navController: NavController) {
    EmailRegistroView( onVerificationSent = {navController.popBackStack()
                                              navController.navigate(AppScreens.EmailVerificacion.route)}, backClick = {
        navController.popBackStack()
        navController.navigate(AppScreens.LoginView.route)})
}

@Composable
fun EmailRegistroView(
    // Parámetros para manejar la navegación
    onVerificationSent: () -> Unit,
    backClick: () -> Unit,
    // Obtenemos la instancia del ViewModel
    viewModel: EmailVerificationVM = viewModel()
) {
    // Observamos el estado de la UI del ViewModel. La vista se reconstruirá cuando cambie.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // --- Manejo de efectos secundarios (eventos de un solo uso) ---
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is RegistrationUiState.VerificationEmailSent -> {
                // Navegamos cuando el correo ha sido enviado
                onVerificationSent()
                viewModel.resetUiState() // Reseteamos el estado para no volver a navegar
            }
            is RegistrationUiState.Error -> {
                // Mostramos un Snackbar cuando hay un error
                snackbarHostState.showSnackbar(message = state.message)
                viewModel.resetUiState() // Reseteamos para no volver a mostrar el error
            }
            else -> { /* No hacemos nada en otros estados */ }
        }
    }

    Scaffold(
        topBar = { SimpleTopAppBar(onBackClick = backClick) },
        containerColor = White,
        // Añadimos el SnackbarHost para mostrar errores
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        // Usamos un Box para poder superponer el indicador de carga
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // El estado de los TextFields se mantiene local a la vista
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var isPasswordVisible by remember { mutableStateOf(false) }

                // --- El resto de tu UI se mantiene casi igual ---
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
                    value = email,
                    onValueChange = { email = it },
                    textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    label = "Contraseña",
                    value = password,
                    onValueChange = { password = it },
                    textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light),
                    isPasswordField = true,
                    isPasswordVisible = isPasswordVisible,
                    onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- CAMBIO EN EL BOTÓN ---
                Button(
                    onClick = {
                        // El botón ahora llama a la función del ViewModel
                        viewModel.registerUserAndSendVerification(email, password)
                    },
                    // El botón se deshabilita si los campos están vacíos O si está cargando
                    enabled = email.isNotBlank() && password.isNotBlank() && uiState != RegistrationUiState.Loading,
                    modifier = Modifier
                        .height(46.dp)
                        .fillMaxSize(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = White
                    )
                ) {
                    Text("Siguiente", fontSize = 18.sp)
                }
            }

            // --- UI DE CARGA ---
            // Si el estado es 'Loading', mostramos un indicador de carga centrado
            if (uiState == RegistrationUiState.Loading) {
                // Box para centrar la animación y poner un fondo semi-transparente
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(White.copy(alpha = 0.5f)) // Fondo opcional para dar foco
                        .clickable(enabled = false, onClick = {}), // Bloquea clics detrás
                    contentAlignment = Alignment.Center
                ) {
                    // 1. Carga la composición de tu archivo JSON
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
                    contentDescription = "Volver atrás"
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


@Preview(showBackground = true)
@Composable
fun EmailRegistroPreview() {
    EmailRegistroView(onVerificationSent = {}, backClick = {})
}

