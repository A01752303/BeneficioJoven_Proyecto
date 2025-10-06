package com.govAtizapan.beneficiojoven.view

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.govAtizapan.beneficiojoven.R

@Composable
fun LoadingScreen() {
    // Carga la composición desde tus recursos raw
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))

    // Muestra la animación
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}