package com.govAtizapan.beneficiojoven.view.registro

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.ui.theme.*
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.SimpleTopAppBar
import com.govAtizapan.beneficiojoven.viewmodel.registerUserVM.RegisterUserVM

@Composable
fun GeneroRegistro(
    navController: NavController,
    viewModel2: RegisterUserVM
) {
    val genderOptions = listOf("Masculino", "Femenino", "No binario", "Prefiero no decirlo")
    var selectedGender by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { SimpleTopAppBar(onBackClick = { navController.popBackStack() }) },
        containerColor = White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¿Con qué género te identificas?",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                color = TealPrimary,
                fontSize = 18.sp
            )
            Text(
                text = "Esta información nos ayuda a personalizar tu experiencia.",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                color = Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Creamos un RadioButton por cada opción de la lista
            genderOptions.forEach { gender ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp) // Altura fija para un mejor toque
                        .selectable(
                            selected = (gender == selectedGender),
                            onClick = { selectedGender = gender },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (gender == selectedGender),
                        onClick = null, // El clic se maneja en el Row para un área más grande
                        colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                    )
                    Text(
                        text = gender,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp),
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))
            // Empuja el botón hacia abajo
            Button(
                onClick = {
                    selectedGender?.let {
                        // 1. Actualiza el dato en el ViewModel
                        viewModel2.updateGenderUser(it)
                        // 2. Imprime el estado actual para depurar
                        Log.d("RegistroDebug", "Datos actuales: ${viewModel2.registerUserData.value}")
                        // 3. Navega al siguiente paso
                        navController.navigate(AppScreens.FechaNacimientoRegistro.route)
                    }
                },
                enabled = selectedGender != null, // El botón solo se activa si se ha seleccionado una opción
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
        }
    }
}
