package com.govAtizapan.beneficiojoven.view.comercioVistas


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.R
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.govAtizapan.beneficiojoven.model.userLogin.LoginUserRequest
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.viewmodel.auth.AuthVM
import com.govAtizapan.beneficiojoven.viewmodel.auth.LoginNavigationState
import com.govAtizapan.beneficiojoven.viewmodel.loginUserVM.LoginUserVM
import com.govAtizapan.beneficiojoven.viewmodel.loginUserVM.UserRole

@Composable
fun InicioSesionComercio(
    navController: NavController,
    authViewModel: AuthVM = viewModel(),
    loginViewModel: LoginUserVM
) {
    val navigationState by authViewModel.navigationState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = navigationState) {
        when (navigationState) {
            is LoginNavigationState.NavigateToNewUserProfile -> {
                navController.navigate(AppScreens.NuevaCuentaVista.route) {
                    popUpTo(AppScreens.LoginView.route) { inclusive = true }
                }
                authViewModel.resetNavigationState()
            }
            is LoginNavigationState.NavigateToHome -> {
                navController.navigate(AppScreens.HomeView.route) {
                    popUpTo(AppScreens.LoginView.route) { inclusive = true }
                }
                authViewModel.resetNavigationState()
            }
            is LoginNavigationState.Idle -> { }
        }
    }

    InicioSesionComercioView(
        onLoginClicked = { email, pass ->
            val requestBody = LoginUserRequest(email = email, contrasena = pass)

            loginViewModel.attemptLogin(
                body = requestBody,
                expectedRole = UserRole.Colaborador,
                navController = navController)
        },
    )
}

@Composable
fun InicioSesionComercioView(onLoginClicked: (String, String) -> Unit) {
    val scrollState = rememberScrollState()

    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Entendido", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Información",
                    tint = TealPrimary,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text = "Aviso",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            },
            text = {
                Text(
                    text = "Para iniciar sesión necesitas tus credenciales que utilizaste para hacer tu solicitud.",
                    fontFamily = PoppinsFamily,
                    fontSize = 15.sp
                )
            },
            containerColor = White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .safeDrawingPadding()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }

        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_sinnombre),
            contentDescription = "Logo de la aplicación",
            modifier = Modifier
                .size(90.dp)
        )
        Text(
            text = "¡BIENVENID@!",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = TealPrimary,
            fontSize = 24.sp
        )
        Text(
            text = "Inicia sesión como comercio para continuar",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = TealPrimary,
            fontSize = 16.sp
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
            onValueChange = { password = it }, // <- AQUÍ ESTABA EL ERROR
            textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light) ,
            isPasswordField = true,
            isPasswordVisible = isPasswordVisible,
            onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
        )
        TextButton(
            onClick = { },
            modifier = Modifier.align(Alignment.End).height(36.dp)
        ) {
            Text("Olvidé mi contraseña",
                fontSize = 10.sp)
        }
        Button(
            onClick = {
                onLoginClicked(email, password)
            },
            enabled = email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .height(36.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TealPrimary,
                contentColor = White
            )
        ) {
            val buttonText = "Iniciar Sesión"
            Text(buttonText,
                fontSize = 14.sp,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        val businessAnnotatedString = buildAnnotatedString {
            append("¿Quieres empezar a crear? ")
            withLink(
                link = LinkAnnotation.Url(
                    url = "BUSINESS_LOGIN",
                    linkInteractionListener = { showInfoDialog = true }
                )
            ) {
                withStyle(style = SpanStyle(
                    color = TealPrimary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.None)
                ) {
                    append("Obtener información")
                }
            }
        }
        Text(text = businessAnnotatedString,
            fontSize = 12.sp,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Light)
        Spacer(modifier = Modifier.height(4.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.logos_pie),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
            )
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
                .defaultMinSize(minHeight = 36.dp),
            textStyle = textStyle,
            placeholder = { Text(placeholderText) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            visualTransformation = if (isPasswordField && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = if (isPasswordField) {
                KeyboardOptions(keyboardType = KeyboardType.Password)
            } else {
                KeyboardOptions.Default
            },
            trailingIcon = {
                if (isPasswordField) {
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