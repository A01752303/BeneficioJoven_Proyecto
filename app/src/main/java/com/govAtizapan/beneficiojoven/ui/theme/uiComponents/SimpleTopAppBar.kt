package com.govAtizapan.beneficiojoven.ui.theme.uiComponents

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(
    // Pasamos una función lambda para manejar el evento de clic en la flecha
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { /* El título está vacío, como en tu diseño */ },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver atrás",
                    modifier = Modifier.size(35.dp),
                )
            }
        },
        // Configuramos los colores para que coincidan con tu diseño
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White, // Fondo de la barra
            navigationIconContentColor = TealPrimary// Color de la flecha
        ),
        modifier = Modifier.statusBarsPadding()
    )
}