package com.govAtizapan.beneficiojoven.view.registro

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.ui.theme.*
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.SimpleTopAppBar
import com.govAtizapan.beneficiojoven.viewmodel.registerUserVM.RegisterUserVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaNacimientoRegistro(
    navController: NavController,
    viewModel2: RegisterUserVM
) {

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateText by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    val isDateValid = selectedDateText != null && dateError == null

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
                text = "¿Cuál es tu fecha de nacimiento?",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                color = TealPrimary,
                fontSize = 18.sp
            )
            Text(
                text = "Esta información nos permite validar tu edad, para saber si puedes acceder a nuestros beneficios.",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                color = Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Campo de texto falso que abre el DatePickerDialog
            DatePickerTriggerField(
                dateText = selectedDateText,
                onClick = { showDatePicker = true }
            )

            // Muestra el mensaje de error si la validación falla
            if (dateError != null) {
                Text(
                    text = dateError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    selectedDateText?.let {
                        viewModel2.updateBirthdayUser(it)
                        Log.d("RegistroDebug", "Datos actuales: ${viewModel2.registerUserData.value}")
                        navController.navigate(AppScreens.DireccionRegistro.route)
                    }
                },
                enabled = isDateValid,
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

    // --- Lógica del DatePickerDialog ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            // Valida la edad y actualiza el estado
                            dateError = validateAge(millis)
                            // Formatea la fecha para mostrarla en la UI
                            selectedDateText = millis.toFormattedDateString()
                        }
                        showDatePicker = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun DatePickerTriggerField(dateText: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFB2EBF2), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = dateText ?: "Selecciona tu fecha",
            color = if (dateText != null) Black else Gray
        )
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "Abrir calendario",
            tint = TealPrimary
        )
    }
}

/**
 * Valida si la edad del usuario está entre 12 y 29 años, usando java.util.Calendar.
 * Esta versión es compatible con todas las versiones de Android sin necesidad de "desugaring".
 * @param birthDateMillis La fecha de nacimiento en milisegundos.
 * @return Un String con el mensaje de error, o null si la edad es válida.
 */
private fun validateAge(birthDateMillis: Long): String? {
    val today = Calendar.getInstance()
    val birthDate = Calendar.getInstance()
    birthDate.timeInMillis = birthDateMillis

    var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

    // Ajusta la edad si el cumpleaños de este año aún no ha pasado
    if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    return when {
        age < 12 -> "Debes tener al menos 12 años para registrarte."
        age > 29 -> "Este beneficio es para personas de hasta 29 años."
        else -> null // La edad es válida
    }
}

/**
 * Convierte un Long (milisegundos) a un String con formato "dd/MM/yyyy", usando SimpleDateFormat.
 * Esta versión es compatible con todas las versiones de Android.
 */
private fun Long.toFormattedDateString(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(date)
}