package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ComercioHome(
    onCrearPromo: () -> Unit,
    onValidarQR: () -> Unit
) {
    Column{
        Button(onClick = onCrearPromo) {
            Text("Registrar promoción")
        }

        Button(onClick = onValidarQR) {
            Text("Validar QR")
        }
    }
}