package com.govAtizapan.beneficiojoven.view.bienvenidaView

import androidx.annotation.DrawableRes

data class OnboardingDataClass(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)

