package com.govAtizapan.beneficiojoven.view.splashScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionManager
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionRole
import com.govAtizapan.beneficiojoven.view.loadingScreen.LoadingScreen
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {

    LaunchedEffect(key1 = true) {
        delay(2000)
        val userType = SessionManager.fetchUserType()
        val rutaDestino = when (userType) {
            SessionRole.Usuario -> AppScreens.HomeView.route
            SessionRole.Colaborador -> AppScreens.ComercioHome.route
            null -> AppScreens.BienvenidaView.route // No hay sesión de ningún tipo
        }
        navController.navigate(rutaDestino) {
            popUpTo(AppScreens.SplashScreen.route) {
                inclusive = true
            }
        }
    }
    LoadingScreen()
}