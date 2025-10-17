package com.govAtizapan.beneficiojoven.view.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.govAtizapan.beneficiojoven.view.bienvenidaView.BienvenidaView
import com.govAtizapan.beneficiojoven.view.comercioVistas.ComercioHome
import com.govAtizapan.beneficiojoven.view.loginView.LoginView
import com.govAtizapan.beneficiojoven.view.splashScreen.SplashScreen
import com.govAtizapan.beneficiojoven.view.loadingScreen.LoadingScreen
import com.govAtizapan.beneficiojoven.view.comercioVistas.CreatePromotionScreen
import com.govAtizapan.beneficiojoven.view.home.HomeView
import com.govAtizapan.beneficiojoven.view.registro.NuevaCuentaVista
import com.govAtizapan.beneficiojoven.view.comercioVistas.InicioSesionComercio
import com.govAtizapan.beneficiojoven.view.registro.EmailRegistro
import com.govAtizapan.beneficiojoven.view.registro.EmailVerificacion
import com.govAtizapan.beneficiojoven.view.registro.NombreRegistro
import com.govAtizapan.beneficiojoven.view.registro.GeneroRegistro
import com.govAtizapan.beneficiojoven.view.registro.FechaNacimientoRegistro
import com.govAtizapan.beneficiojoven.view.registro.DireccionRegistroView
import com.govAtizapan.beneficiojoven.view.registro.FinalizaRegistro
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.EmailVerificationVM
import com.govAtizapan.beneficiojoven.viewmodel.registerUserVM.RegisterUserVM


// Es una buena práctica definir las rutas como constantes
const val REGISTRATION_GRAPH_ROUTE = "registration_graph"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val registrationViewModel: EmailVerificationVM = viewModel()
    val registerUserVM: RegisterUserVM = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreen.route
    ) {
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
        composable(route = AppScreens.CreatePromotionScreen.route) {
            CreatePromotionScreen()
        }
        composable(route = AppScreens.ComercioHome.route) {
            ComercioHome()
        }

        navigation(
            startDestination = AppScreens.EmailRegistro.route,
            route = REGISTRATION_GRAPH_ROUTE
        ) {
            composable(route = AppScreens.EmailRegistro.route) {
                EmailRegistro(
                    navController = navController,
                    viewModel = registrationViewModel,
                    viewModel2 = registerUserVM)
            }

            composable(
                route = AppScreens.EmailVerificacion.route + "?email={email}",
                arguments = listOf(navArgument("email") {
                    type = NavType.StringType
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
                    viewModel = registrationViewModel,
                    viewModel2 = registerUserVM
                )
            }

            composable(route = AppScreens.GeneroRegistro.route) {
                GeneroRegistro(
                    navController = navController,
                    viewModel2 = registerUserVM
                )
            }
            composable(route = AppScreens.FechaNacimientoRegistro.route) {
                FechaNacimientoRegistro(
                    navController = navController,
                    viewModel2 = registerUserVM
                )
            }

            composable(route = AppScreens.DireccionRegistro.route) {
                DireccionRegistroView(
                    navController = navController,
                    viewModel2 = registerUserVM,
                    viewModel = registrationViewModel
                )
            }
            composable(route = AppScreens.FinalizarRegistro.route) {
                FinalizaRegistro(navController = navController)
            }
        }
    }
}
