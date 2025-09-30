package com.govAtizapan.beneficiojoven.view.navigation

sealed class AppScreens(val route: String) {
    object SplashScreen: AppScreens("splash_screen")
    object BienvenidaView: AppScreens("main_screen")
}