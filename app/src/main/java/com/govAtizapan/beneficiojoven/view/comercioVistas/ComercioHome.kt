package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ComercioHome(
    onCrearPromo: () -> Unit
) {
    // ... tu UI ...

    Button(onClick = onCrearPromo) {
        Text("Registrar promoción")
    }
}