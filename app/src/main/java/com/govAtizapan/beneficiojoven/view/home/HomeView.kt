package com.govAtizapan.beneficiojoven.view.home


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.govAtizapan.beneficiojoven.viewmodel.home.HomeVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(viewModel: HomeVM = viewModel()) {
    val promociones by viewModel.promociones.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarPromociones()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Promociones disponibles") })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (promociones.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(promociones) { promo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = promo.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = promo.descripcion)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Descuento: ${promo.porcentaje}%")
                                if (promo.precio.isNotEmpty())
                                    Text(text = "Precio: ${promo.precio}")
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "Válido del ${promo.fecha_inicio.take(10)} al ${promo.fecha_fin.take(10)}")
                            }
                        }
                    }
                }
            }
        }
    }
}