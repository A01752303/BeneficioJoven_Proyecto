package com.govAtizapan.beneficiojoven.view.home


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.model.promotionget.PromotionResponseGET

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuponDetalleView(
    navController: NavController,
    promo: PromotionResponseGET
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(promo.nombre) },
                navigationIcon = {
                    // 🟢 Agregamos botón de regreso opcional
                    IconButton(onClick = { navController.popBackStack() }) {
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 🟢 Descripción
            Text(text = promo.descripcion, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))

            // 🟢 Porcentaje (String)
            if (promo.porcentaje.isNotEmpty()) {
                Text(text = "Descuento: ${promo.porcentaje}%")
            }

            // 🟢 Precio (String)
            if (promo.precio.isNotEmpty()) {
                Text(text = "Precio: $${promo.precio}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🟢 Fechas formateadas
            Text(text = "Válido del ${promo.fecha_inicio.take(10)} al ${promo.fecha_fin.take(10)}")

            Spacer(modifier = Modifier.height(32.dp))

            // 🟢 Botón para generar QR
            Button(
                onClick = {
                    navController.navigate("generarQR/${promo.id}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generar QR")
            }
        }
    }
}