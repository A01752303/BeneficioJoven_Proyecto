package com.govAtizapan.beneficiojoven.view.splashScreen

import com.govAtizapan.beneficiojoven.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
// 1. IMPORTA TU SESSION MANAGER Y LAS RUTAS
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionManager
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionRole
import com.govAtizapan.beneficiojoven.view.loadingScreen.LoadingScreen
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {

    LaunchedEffect(key1 = true) {
        delay(2000)

        // 1. OBTÉN EL TIPO DE USUARIO DE LA SESIÓN GUARDADA
        val userType = SessionManager.fetchUserType()

        // 2. DECIDE LA RUTA USANDO 'when'
        val rutaDestino = when (userType) {
            SessionRole.Usuario -> AppScreens.HomeView.route
            SessionRole.Colaborador -> AppScreens.ComercioHome.route
            null -> AppScreens.BienvenidaView.route // No hay sesión de ningún tipo
        }

        // 3. NAVEGA
        navController.navigate(rutaDestino) {
            popUpTo(AppScreens.SplashScreen.route) {
                inclusive = true
            }
        }
    }
    // Esto está perfecto, muestra tu logo mientras la lógica de arriba corre
    LoadingScreen()
}

@Composable
fun Splash() {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        AsyncImage(model = R.drawable.logo,
            "Beneficio Joven",
            Modifier.size(200.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Splash()
}