package com.govAtizapan.beneficiojoven.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.govAtizapan.beneficiojoven.R
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import androidx.compose.ui.text.TextStyle
@Composable
fun LoginView(modifier: Modifier = Modifier, navController: NavController, onLoginClicked: (String, String) -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isPasswordVisible by remember { mutableStateOf(false) }


        Image(
            painter = painterResource(id = R.drawable.logo_sinnombre), // <-- CAMBIA ESTO por tu imagen
            contentDescription = "Logo de la aplicación", // Descripción para accesibilidad
            modifier = Modifier
                .size(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
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
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Olvidé mi contraseña")
        }
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = {
                onLoginClicked(email, password)
            },
            enabled = email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .height(46.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TealPrimary,
                contentColor = White
            )
        ) {
            val buttonText = "Iniciar Sesión"
            Text(buttonText,
                fontSize = 18.sp,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold)
        }
        OrSeparator()
        Spacer(modifier = Modifier.height(4.dp))
        CustomOutlinedButton(modifier = Modifier.fillMaxWidth(), text = "Continuar con Google", onClick = {}, icon = painterResource(id = R.drawable.google_icon))
        Spacer(modifier = Modifier.height(16.dp))
        CustomOutlinedButton(modifier = Modifier.fillMaxWidth(), text = "Continuar con Facebook", onClick = {}, icon = painterResource(id = R.drawable.facebook_icon))

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

@Composable
fun OrSeparator() {
    Row(
        modifier = Modifier
            .fillMaxWidth() // Ocupa todo el ancho
            .padding(vertical = 16.dp), // Espacio vertical
        verticalAlignment = Alignment.CenterVertically // Centra la línea y el texto verticalmente
    ) {
        Divider(
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

        Divider(
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
            .height(46.dp)
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
            fontSize = 16.sp,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview(modifier: Modifier = Modifier) {
    LoginView(navController = rememberNavController(), onLoginClicked = { _, _ -> })
}