package com.govAtizapan.beneficiojoven.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.govAtizapan.beneficiojoven.view.BienvenidaView
import com.govAtizapan.beneficiojoven.view.LoginView
import com.govAtizapan.beneficiojoven.view.SplashScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = AppScreens.SplashScreen.route)
    {
        composable(route = AppScreens.SplashScreen.route){
            SplashScreen(navController = navController)
        }
        composable(route = AppScreens.BienvenidaView.route){
            BienvenidaView(navController = navController)
        }
        composable(route= AppScreens.LoginView.route){
            LoginView(navController = navController)
        }
    }
}