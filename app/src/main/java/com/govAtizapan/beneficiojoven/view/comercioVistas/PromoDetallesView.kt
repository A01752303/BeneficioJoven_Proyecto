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
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppStandardTextField
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionEvent
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoDetallesView(
    onBack: () -> Unit,
    onNext: () -> Unit,
    vm: CreatePromotionViewModel
) {
    val ui by vm.ui.collectAsState()
    val focus = LocalFocusManager.current

    // ---- Validaciones específicas por tipo ----
    val pctValid = ui.porcentajeTxt.toIntOrNull()?.let { it in 1..100 } == true
    val priceValid = ui.precioTxt.toIntOrNull()?.let { it > 0 } == true
    val descValid = ui.descripcion.isNotBlank()

    val canContinue = when (ui.tipo) {
        PromotionType.DESCUENTO   -> pctValid && descValid
        PromotionType.PRECIO_FIJO -> priceValid && descValid
        else                      -> descValid
    }



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
                text = "Detalles de la promoción",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                color = TealPrimary,
                fontSize = 18.sp
            )
            Text(
                text = when (ui.tipo) {
                    PromotionType.DESCUENTO ->
                        "Indica el porcentaje de descuento y describe la mecánica."
                    PromotionType.PRECIO_FIJO ->
                        "Indica el precio fijo y describe la mecánica."
                    PromotionType.DOSxUNO ->
                        "Describe cómo aplica la promoción 2x1."
                    PromotionType.TRAE_AMIGO ->
                        "Describe cómo aplica la promoción \"Trae un amigo\"."
                    PromotionType.OTRA ->
                        "Describe claramente la mecánica de la promoción."
                },
                style = MaterialTheme.typography.bodySmall,
                fontFamily = PoppinsFamily,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            // ----- Campos condicionales -----
            if (ui.tipo == PromotionType.DESCUENTO) {
                AppStandardTextField(
                    value = ui.porcentajeTxt,
                    onValueChange = { vm.onEvent(CreatePromotionEvent.PorcentajeChanged(it)) },
                    label = "Porcentaje (%)",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = ui.porcentajeTxt.isNotBlank() && !pctValid,
                    supportingText = "1 a 100. Se enviará porcentaje=<valor> y precio=0",
                    textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
                )
                Spacer(Modifier.height(16.dp))
            }

            if (ui.tipo == PromotionType.PRECIO_FIJO) {
                AppStandardTextField(
                    value = ui.precioTxt,
                    onValueChange = { vm.onEvent(CreatePromotionEvent.PrecioChanged(it)) },
                    label = "Precio fijo (MXN)",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = ui.precioTxt.isNotBlank() && !priceValid,
                    supportingText = "Monto en MXN. Se enviará precio=<valor> y porcentaje=0",
                    textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
                )
                Spacer(Modifier.height(16.dp))
            }

            // Descripción / Mecánica (siempre)
            AppStandardTextField(
                value = ui.descripcion,
                onValueChange = { vm.onEvent(CreatePromotionEvent.DescripcionChanged(it)) },
                label = when (ui.tipo) {
                    PromotionType.DESCUENTO, PromotionType.PRECIO_FIJO -> "Descripción"
                    PromotionType.DOSxUNO -> "Descripción / Mecánica (2x1)"
                    PromotionType.TRAE_AMIGO -> "Descripción / Mecánica (Trae un amigo)"
                    PromotionType.OTRA -> "Descripción / Mecánica"
                },
                singleLine = false,
                minHeightDp = 120,
                textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
            )

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
                    Text("Atrás",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp)
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
                    Text("Siguiente",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
