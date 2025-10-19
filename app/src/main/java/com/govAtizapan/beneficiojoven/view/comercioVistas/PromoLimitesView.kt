package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppLoadingOverlay
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppStandardTextField
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionEvent
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoLimitesView(
    onBack: () -> Unit,
    onNext: () -> Unit,
    vm: CreatePromotionViewModel
) {
    val ui by vm.ui.collectAsState()
    val focus = LocalFocusManager.current

    // Parseos (0 o vacío = sin límite)
    val total = ui.limiteTotalTxt.toIntOrNull() ?: 0
    val porUsuario = ui.limitePorUsuarioTxt.toIntOrNull() ?: 0

    // Regla: si ambos > 0, porUsuario <= total
    val relacionOk = !((total > 0) && (porUsuario > 0) && (porUsuario > total))
    val canContinue = relacionOk // en este paso no exigimos llenarlos


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Límites de la promoción",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                color = TealPrimary,
                fontSize = 18.sp
            )
            Text(
                text = "Puedes definir cuántas veces aplica en total y por usuario. Déjalo vacío o 0 para no limitar.",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = PoppinsFamily,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    AppStandardTextField(
                        value = ui.limiteTotalTxt,
                        onValueChange = { vm.onEvent(CreatePromotionEvent.LimiteTotalChanged(it)) },
                        label = "Límite total (opcional)",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        supportingText = "0 o vacío = sin límite",
                        textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
                    )
                }
                Column(Modifier.weight(1f)) {
                    AppStandardTextField(
                        value = ui.limitePorUsuarioTxt,
                        onValueChange = { vm.onEvent(CreatePromotionEvent.LimitePorUsuarioChanged(it)) },
                        label = "Límite por usuario (opcional)",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        supportingText = "0 o vacío = sin límite",
                        isError = !relacionOk, // resalta si viola la regla
                        textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
                    )
                }
            }

            if (!relacionOk) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "El límite por usuario no puede ser mayor al límite total.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = PoppinsFamily
                )
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        focus.clearFocus()
                        onBack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
                ) {
                    Text(
                        "Atrás",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                Button(
                    onClick = {
                        focus.clearFocus()
                        onNext()
                    },
                    enabled = canContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Siguiente",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
    AppLoadingOverlay(visible = ui.isLoading)
}
