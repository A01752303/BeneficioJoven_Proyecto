package com.govAtizapan.beneficiojoven.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.EmailVerificationVM


// Es una buena práctica definir las rutas como constantes
const val REGISTRATION_GRAPH_ROUTE = "registration_graph"

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val registrationViewModel: EmailVerificationVM = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreen.route
    ) {
        // --- Pantallas que no son parte del flujo de registro ---
        composable(route = AppScreens.LoadingScreen.route) {
            LoadingScreen()
        }
        composable(route = AppScreens.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(route = AppScreens.BienvenidaView.route) {
            BienvenidaView(navController = navController)
        }
        composable(route = AppScreens.LoginView.route) {
            LoginView(navController = navController)
        }
        composable(route = AppScreens.HomeView.route) {
            HomeView()
        }
        composable(route = AppScreens.NuevaCuentaVista.route) {
            NuevaCuentaVista()
        }
        composable(route = AppScreens.InicioSesionComercio.route) {
            InicioSesionComercio()
        }

        // --- INICIA EL GRAFO DE NAVEGACIÓN PARA EL REGISTRO ---
        navigation(
            startDestination = AppScreens.EmailRegistro.route,
            route = REGISTRATION_GRAPH_ROUTE
        ) {
            // Cada composable ahora recibe la misma instancia 'registrationViewModel'
            composable(route = AppScreens.EmailRegistro.route) {
                EmailRegistro(
                    navController = navController,
                    viewModel = registrationViewModel)
            }

            composable(
                // 1. La ruta ahora usa '?' para hacer el argumento opcional.
                route = AppScreens.EmailVerificacion.route + "?email={email}",
                arguments = listOf(navArgument("email") {
                    type = NavType.StringType
                    // 2. Se marca explícitamente como que puede ser nulo.
                    nullable = true
                })
            ) { navBackStackEntry ->
                val email = navBackStackEntry.arguments?.getString("email") ?: ""
                EmailVerificacion(
                    navController = navController,
                    viewModel = registrationViewModel,
                    email = email
                )
            }

            composable(route = AppScreens.NombreRegistro.route) {
                NombreRegistro(
                    navController = navController,
                    viewModel = registrationViewModel
                )
            }
        } // --- 👆 TERMINA EL GRAFO DE NAVEGACIÓN ---
    }
}
