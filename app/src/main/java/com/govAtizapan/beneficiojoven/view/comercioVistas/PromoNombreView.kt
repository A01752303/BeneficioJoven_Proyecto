package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.govAtizapan.beneficiojoven.model.promotionpost.PromotionType
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppLoadingOverlay
import com.govAtizapan.beneficiojoven.ui.theme.uiComponents.AppStandardTextField
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionEvent
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoNombreView(
    onBack: () -> Unit,          // 👈 nuevo callback de regreso
    onNext: () -> Unit,
    vm: CreatePromotionViewModel
) {
    val ui by vm.ui.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TealPrimary
                        )
                    }
                },
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
                .safeDrawingPadding()
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Registro de promoción",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                color = TealPrimary,
                fontSize = 18.sp
            )
            Text(
                text = "Empecemos con el título y el tipo de promoción.",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(20.dp))

            // Título
            AppStandardTextField(
                value = ui.titulo,
                onValueChange = { vm.onEvent(CreatePromotionEvent.TituloChanged(it)) },
                label = "Título (nombre)",
                textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
            )
            Spacer(Modifier.height(16.dp))

            // Tipo de promoción
            TipoPromocionSelector(
                selected = ui.tipo,
                onSelected = { vm.onEvent(CreatePromotionEvent.TipoChanged(it)) }
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onNext,
                enabled = ui.titulo.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
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
                    fontSize = 18.sp
                )
            }
        }
    }
    AppLoadingOverlay(visible = ui.isLoading)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TipoPromocionSelector(
    selected: PromotionType,
    onSelected: (PromotionType) -> Unit
) {
    val opciones = listOf(
        PromotionType.DESCUENTO to "Descuento (%)",
        PromotionType.PRECIO_FIJO to "Precio fijo (MXN)",
        PromotionType.DOSxUNO to "2x1",
        PromotionType.TRAE_AMIGO to "Trae un amigo",
        PromotionType.OTRA to "Otra"
    )
    var expanded by remember { mutableStateOf(false) }
    val etiqueta = opciones.first { it.first == selected }.second

    val lightCyan = Color(0xFFE0F7FA)
    val borderCyan = Color(0xFFB2EBF2)
    val shape = RoundedCornerShape(16.dp)

    Text(
        text = "Tipo de promoción",
        modifier = Modifier.padding(bottom = 8.dp),
        fontSize = 16.sp,
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Light,
        color = Color.Black
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = etiqueta,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = shape,
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .border(1.dp, borderCyan, shape),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = lightCyan,
                focusedContainerColor = lightCyan,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded = false
                        onSelected(value)
                    }
                )
            }
        }
    }
}
