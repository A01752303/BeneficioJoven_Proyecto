package com.govAtizapan.beneficiojoven.view.home.favoritos



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.govAtizapan.beneficiojoven.view.home.homeViews.PromoCard
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.viewmodel.favoritosvm.FavoritosViewModel
import com.govAtizapan.beneficiojoven.view.home.homeViews.HomeView // 👈 Usa tu mismo componente de Home
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosView(
    navController: NavController,
    viewModel: FavoritosViewModel = viewModel()
) {
    val favoritos by viewModel.favoritos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Cargar las promociones apartadas al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarFavoritos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TealPrimary)
                }
            }

            favoritos.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tienes promociones apartadas aún.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoritos, key = { it.id }) { promo ->
                        PromoCard(
                            promo = promo,
                            isExpiringSoon = false,
                            daysRemaining = null,
                            onClick = {
                                navController.navigate("detalleCupon/${promo.id}")
                            },
                            esFavorito = true,
                            onToggleFavorito = { /* Aquí no hace falta volver a apartar */ }
                        )
                    }
                }
            }
        }
    }
}
