package com.govAtizapan.beneficiojoven.ui.theme.uiComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import com.govAtizapan.beneficiojoven.view.loadingScreen.LoadingScreen // <-- importa tu pantalla

/**
 * Capa semitransparente que bloquea la UI y muestra tu LoadingScreen.
 * Usa zIndex alto para asegurarte que quede encima de todo.
 */
@Composable
fun AppLoadingOverlay(
    visible: Boolean
) {
    if (!visible) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f) // por encima de la UI
            .background(Color.Black.copy(alpha = 0.25f)), // velo
        contentAlignment = Alignment.Center
    ) {
        // Si tu LoadingScreen ya dibuja fondo, puedes quitar el .background de arriba
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            LoadingScreen() // ← tu componente existente
        }
    }
}
