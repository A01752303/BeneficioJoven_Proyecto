package com.govAtizapan.beneficiojoven.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.govAtizapan.beneficiojoven.view.BienvenidaView
import com.govAtizapan.beneficiojoven.view.LoginView
import com.govAtizapan.beneficiojoven.view.SplashScreen
import com.govAtizapan.beneficiojoven.view.LoadingScreen
import com.govAtizapan.beneficiojoven.view.home.HomeView
import com.govAtizapan.beneficiojoven.view.registro.NuevaCuentaVista
import com.govAtizapan.beneficiojoven.view.comercioVistas.InicioSesionComercio
import com.govAtizapan.beneficiojoven.view.registro.EmailRegistro
import com.govAtizapan.beneficiojoven.view.registro.EmailVerificacion
import com.govAtizapan.beneficiojoven.view.registro.NombreRegistro


@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = AppScreens.SplashScreen.route)
    {
        composable(route = AppScreens.LoadingScreen.route){
            LoadingScreen()
        }
        composable(route = AppScreens.SplashScreen.route){
            SplashScreen(navController = navController)
        }
        composable(route = AppScreens.BienvenidaView.route){
            BienvenidaView(navController = navController)
        }
        composable(route= AppScreens.LoginView.route){
            LoginView(navController = navController)
        }
        composable(route = AppScreens.HomeView.route){
            HomeView()
        }
        composable(route= AppScreens.NuevaCuentaVista.route){
            NuevaCuentaVista()
        }
        composable(route = AppScreens.InicioSesionComercio.route){
            InicioSesionComercio()
        }
        composable(route = AppScreens.EmailRegistro.route){
            EmailRegistro(navController = navController)
        }
        composable(route = AppScreens.EmailVerificacion.route){
            EmailVerificacion(navController = navController)
        }
        composable(route = AppScreens.NombreRegistro.route){
            NombreRegistro(navController = navController)
        }
    }
}