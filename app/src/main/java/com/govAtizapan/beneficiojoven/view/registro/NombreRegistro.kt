package com.govAtizapan.beneficiojoven.view.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.EmailVerificationVM

@Composable
fun NombreRegistro(navController: NavController, viewModel: EmailVerificationVM) {
    NombreRegistroView(onNextClicked = { _, _, _ -> }, navController = navController, viewModel = viewModel)
}

@Composable
fun NombreRegistroView(
    onNextClicked: (String, String, String) -> Unit,
    navController: NavController,
    viewModel: EmailVerificationVM) {
    Scaffold(
        // 1. Se asigna la barra superior personalizada al slot 'topBar'.
        topBar = {
            SimpleTopAppBarN()
        },
        // 2. Se define el color de fondo para el área de contenido.
        containerColor = White) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .safeDrawingPadding()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            var nombre by remember { mutableStateOf("") }
            var apellidoP by remember { mutableStateOf("") }
            var apellidoM by remember { mutableStateOf("") }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Continuemos con tu registro",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                color = Gray,
                fontSize = 18.sp
            )

            Text(
                text = "¿Cuál es tu nombre?",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                color = TealPrimary,
                fontSize = 18.sp
            )
            Text(
                text = "Así va a aparecer tu nombre al crear\n" +
                        "tu perfil",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Left,
                color = Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                label = "Nombre",
                value = nombre,
                onValueChange = { nombre = it },
                textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Apellido Paterno",
                value = apellidoP,
                onValueChange = { apellidoP = it },
                textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                label = "Apellido Materno",
                value = apellidoM,
                onValueChange = { apellidoM = it },
                textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onNextClicked(nombre, apellidoM, apellidoP)
                },
                enabled = nombre.isNotBlank() && apellidoP.isNotBlank() && apellidoM.isNotBlank(),
                modifier = Modifier
                    .height(46.dp)
                    .fillMaxSize(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealPrimary,
                    contentColor = White
                )
            ) {
                val buttonText = "Siguiente"
                Text(
                    buttonText,
                    fontSize = 18.sp,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Un Spacer para dar algo de aire entre los botones y el nuevo texto
            Spacer(modifier = Modifier.height(32.dp))

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBarN() {
    TopAppBar(
        title = { /* El título está vacío, como en tu diseño */ },
        navigationIcon = {},
        // Configuramos los colores para que coincidan con tu diseño
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White, // Fondo de la barra
            navigationIconContentColor = TealPrimary// Color de la flecha
        ),
        modifier = Modifier.statusBarsPadding()
    )
}

