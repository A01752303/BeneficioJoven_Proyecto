package com.govAtizapan.beneficiojoven.view.loginView

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.painter.Painter
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
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.govAtizapan.beneficiojoven.model.userLogin.LoginUserRequest
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.viewmodel.auth.AuthEvent
import com.govAtizapan.beneficiojoven.viewmodel.auth.AuthVM
import com.govAtizapan.beneficiojoven.viewmodel.auth.LoginNavigationState
import com.govAtizapan.beneficiojoven.view.navigation.REGISTRATION_GRAPH_ROUTE
import com.govAtizapan.beneficiojoven.viewmodel.loginUserVM.LoginUserVM
import com.govAtizapan.beneficiojoven.viewmodel.loginUserVM.UserRole

@Composable
fun LoginView(
    navController: NavController,
    authViewModel: AuthVM = viewModel(),
    loginViewModel: LoginUserVM
) {
    val navigationState by authViewModel.navigationState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    authViewModel.onEvent(AuthEvent.SignInWithGoogle(credential))
                } catch (e: ApiException) {
                    Toast.makeText(context, "Error de Google: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.google_web_client_id))
                .requestEmail()
                .build()
        )
    }

    // 5. `LaunchedEffect` reacciona a los cambios en el estado de navegación del ViewModel
    LaunchedEffect(key1 = navigationState) {
        when (navigationState) {
            is LoginNavigationState.NavigateToNewUserProfile -> {
                navController.navigate(AppScreens.NuevaCuentaVista.route) {
                    popUpTo(AppScreens.LoginView.route) { inclusive = true }
                }
                authViewModel.resetNavigationState() // Resetea el evento para no volver a navegar
            }
            is LoginNavigationState.NavigateToHome -> {
                navController.navigate(AppScreens.HomeView.route) {
                    popUpTo(AppScreens.LoginView.route) { inclusive = true }
                }
                authViewModel.resetNavigationState()
            }
            is LoginNavigationState.Idle -> { /* No hacer nada */ }
        }
    }

    Login(
        onLoginClicked = { email, pass ->
            val requestBody = LoginUserRequest(email = email, contrasena = pass)
            loginViewModel.attemptLogin(
                body = requestBody,
                expectedRole = UserRole.Usuario, // Asumimos que esta vista es para el rol "Usuario".
                navController = navController)
            authViewModel.onEvent(AuthEvent.SignInWithEmail(email, pass))
        },
        onGoogleClick = {
            googleLauncher.launch(googleSignInClient.signInIntent)
        },
        onRegisterClick = {
            navController.navigate(REGISTRATION_GRAPH_ROUTE)
        },
        onBusinessLoginClick = {
            navController.navigate(AppScreens.InicioSesionComercio.route)
        }
    )
}

@Composable
fun Login(onLoginClicked: (String, String) -> Unit,
          onGoogleClick: () -> Unit,
          onRegisterClick: () -> Unit = {},
          onBusinessLoginClick: () -> Unit = {}) {
    val scrollState = rememberScrollState()
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
            text = "Inicia sesión para continuar",
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
            onValueChange = { password = it },
            textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light) ,
            isPasswordField = true,
            isPasswordVisible = isPasswordVisible,
            onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
        )
        TextButton(
            onClick = { /* Lógica para recuperar contraseña */ },
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
        // Texto para registrarse
        val registerAnnotatedString = buildAnnotatedString {
            append("¿No tienes una cuenta? ")
            // 1. Usa withLink en lugar de pushStringAnnotation
            withLink(
                link = LinkAnnotation.Url(
                    url = "REGISTER", // Usamos la URL como un identificador único, igual que el tag
                    linkInteractionListener = { onRegisterClick() } // La acción onClick va aquí directamente
                )
            ) {
                withStyle(style = SpanStyle(
                    color = TealPrimary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.None)
                ) {
                    append("Regístrate aquí")
                }
            }
        }

        // 2. Usa un Text normal en lugar de ClickableText
        Text(text = registerAnnotatedString,
            fontSize = 12.sp,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Light)

        // Texto para el inicio de sesión de comercios
        val businessAnnotatedString = buildAnnotatedString {
            append("¿Eres comercio? ")
            withLink(
                link = LinkAnnotation.Url(
                    url = "BUSINESS_LOGIN", // Identificador único
                    linkInteractionListener = { onBusinessLoginClick() } // La acción va aquí
                )
            ) {
                withStyle(style = SpanStyle(
                    color = TealPrimary,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.None)
                ) {
                    append("Inicia sesión aquí")
                }
            }
        }
        Text(text = businessAnnotatedString,
            fontSize = 12.sp,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Light)
        OrSeparator()
        Spacer(modifier = Modifier.height(4.dp))
        CustomOutlinedButton(modifier = Modifier
            .fillMaxWidth(),
            text = "Continuar con Google",
            onClick = onGoogleClick, icon = painterResource(id = R.drawable.google_icon))
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

@Composable
fun OrSeparator() {
    Row(
        modifier = Modifier
            .fillMaxWidth() // Ocupa todo el ancho
            .padding(vertical = 16.dp), // Espacio vertical
        verticalAlignment = Alignment.CenterVertically // Centra la línea y el texto verticalmente
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f), // Ocupa el espacio disponible
            color = Color.Gray,
            thickness = 1.dp
        )

        Text(
            text = "o",
            modifier = Modifier.padding(horizontal = 8.dp), // Espacio a los lados del texto
            fontSize = 16.sp,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            color = Color.DarkGray
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f), // Ocupa el espacio disponible
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}

@Composable
fun CustomOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    icon: Painter? = null // The icon is correctly defined as an optional Painter
) {
    OutlinedButton(
        // 1. Simplified onClick assignment
        onClick = onClick,
        // 2. Correctly chain the passed modifier with local modifiers
        modifier = modifier
            .height(38.dp)
            .fillMaxWidth(), // Use fillMaxWidth for more predictable behavior
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color.Gray),
        colors = ButtonDefaults.buttonColors(
            containerColor = White,
            contentColor = Color.Black
        )
    ) {
        // 3. This is the most critical fix: Handle the nullable icon
        // Without this check, your app would crash if 'icon' is null
        icon?.let {
            Icon(
                painter = it,
                contentDescription = null, // Should be descriptive if the icon isn't decorative
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = Color.Unspecified

            )
            // 5. Add a spacer for better visual separation
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
        }

        Text(
            text = text,
            fontSize = 14.sp,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal
        )
    }
}


