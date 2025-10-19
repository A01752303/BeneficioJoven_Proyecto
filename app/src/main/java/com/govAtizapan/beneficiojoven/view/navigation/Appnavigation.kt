package com.govAtizapan.beneficiojoven.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.govAtizapan.beneficiojoven.view.home.CuponDetalleView
import com.govAtizapan.beneficiojoven.view.home.GenerarQRView
import com.govAtizapan.beneficiojoven.viewmodel.home.HomeVM
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.govAtizapan.beneficiojoven.view.bienvenidaView.BienvenidaView
import com.govAtizapan.beneficiojoven.view.loginView.LoginView
import com.govAtizapan.beneficiojoven.view.splashScreen.SplashScreen
import com.govAtizapan.beneficiojoven.view.loadingScreen.LoadingScreen
import com.govAtizapan.beneficiojoven.view.home.HomeView
import com.govAtizapan.beneficiojoven.view.registro.NuevaCuentaVista
import com.govAtizapan.beneficiojoven.view.comercioVistas.InicioSesionComercio
import com.govAtizapan.beneficiojoven.view.registro.EmailRegistro
import com.govAtizapan.beneficiojoven.view.registro.EmailVerificacion
import com.govAtizapan.beneficiojoven.view.registro.NombreRegistro
import com.govAtizapan.beneficiojoven.view.registro.GeneroRegistro
import com.govAtizapan.beneficiojoven.view.registro.FechaNacimientoRegistro
import com.govAtizapan.beneficiojoven.view.registro.DireccionRegistroView
import com.govAtizapan.beneficiojoven.viewmodel.emailVerification.EmailVerificationVM
import com.govAtizapan.beneficiojoven.viewmodel.registerUserVM.RegisterUserVM
import androidx.compose.runtime.collectAsState
import com.govAtizapan.beneficiojoven.view.comercioVistas.ComercioHome
import com.govAtizapan.beneficiojoven.view.comercioVistas.PROMO_GRAPH_ROUTE
import com.govAtizapan.beneficiojoven.view.comercioVistas.addPromoGraph
import com.govAtizapan.beneficiojoven.view.mapa.ComerciosCercanosScreen
import com.govAtizapan.beneficiojoven.view.registro.FinalizaRegistro
import com.govAtizapan.beneficiojoven.viewmodel.loginUserVM.LoginUserVM


// Es una buena práctica definir las rutas como constantes
const val REGISTRATION_GRAPH_ROUTE = "registration_graph"

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val registrationViewModel: EmailVerificationVM = viewModel()
    val registerUserVM: RegisterUserVM = viewModel()
    val loginUserVM: LoginUserVM = viewModel()
    val homeViewModel: HomeVM = viewModel()


    NavHost(
        navController = navController,
        startDestination = AppScreens.HomeView.route
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
            LoginView(navController = navController, loginViewModel = loginUserVM)
        }
        composable(AppScreens.HomeView.route) {
            HomeView(navController = navController, viewModel = homeViewModel)
        }
        composable(route = AppScreens.NuevaCuentaVista.route) {
            NuevaCuentaVista()
        }
        composable(route = AppScreens.InicioSesionComercio.route) {
            InicioSesionComercio(navController = navController, loginViewModel = loginUserVM)
        }
        //Promociones
        addPromoGraph(navController)
        composable(route = AppScreens.ComercioHome.route) {
            ComercioHome(
                onCrearPromo = { navController.navigate(PROMO_GRAPH_ROUTE) }
            )
        }
        composable(
            route = AppScreens.CuponDetalle.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            val promo = homeViewModel.promociones.collectAsState().value.find { it.id == id }
            if (promo != null) {
                CuponDetalleView(navController = navController, promo = promo)
            }
        }

        // 🟩 NUEVO → pantalla para generar QR
        composable(
            route = AppScreens.GenerarQR.route,
            arguments = listOf(navArgument("idCanje") { type = NavType.IntType })
        ) { backStackEntry ->
            val idCanje = backStackEntry.arguments?.getInt("idCanje")
            GenerarQRView(navController = navController, idCanje = idCanje)
        }

        // Ruta para Pantalla Google Maps - Comercios Cercanos
        composable(route = AppScreens.ComerciosCercanosScreen.route) {
            ComerciosCercanosScreen(navController = navController)
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
