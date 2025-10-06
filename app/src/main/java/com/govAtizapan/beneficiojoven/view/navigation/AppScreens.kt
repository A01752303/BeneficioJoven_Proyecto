package com.govAtizapan.beneficiojoven.view.navigation

sealed class AppScreens(val route: String) {
    object LoadingScreen: AppScreens("loading_screen")
    object SplashScreen: AppScreens("splash_screen")
    object BienvenidaView: AppScreens("main_screen")
    object LoginView: AppScreens(route = "login_view")

    // Registo
    object EmailRegistro: AppScreens("email_registro")
    object EmailVerificacion: AppScreens("email_verificacion")
    object NuevaCuentaVista: AppScreens("nueva_cuenta_vista")
    object NombreRegistro: AppScreens("nombre_registro")
    object GeneroRegistro: AppScreens("genero_registro")
    object FechaNacimientoRegistro: AppScreens("fecha_nacimiento_registro")
    object DireccionRegistro: AppScreens("direccion_registro")

    // Páginas principales
    object HomeView: AppScreens("home_view")

}