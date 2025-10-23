package com.govAtizapan.beneficiojoven.view.comercioVistas

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel

// Raíz del sub-grafo de "registro de promociones"
const val PROMO_GRAPH_ROUTE = "promo_graph"

sealed class PromoScreens(val route: String) {
    data object PromoNombre : PromoScreens("promo_nombre")       // Título + Tipo
    data object PromoDetalles : PromoScreens("promo_detalles")   // Descripción / mecánica
    data object PromoFechas : PromoScreens("promo_fechas")       // Rango de fechas
    data object PromoLimites : PromoScreens("promo_limites")     // Límite total / por usuario
    data object PromoResumen : PromoScreens("promo_resumen")     // Confirmar y enviar
}

/** Úsalo desde tu NavHost principal: addPromoGraph(navController) */
fun NavGraphBuilder.addPromoGraph(navController: NavHostController) {
    navigation(
        startDestination = PromoScreens.PromoNombre.route,
        route = PROMO_GRAPH_ROUTE
    ) {
        composable(PromoScreens.PromoNombre.route) { entry ->
            // 1) Obtenemos el owner del sub-grafo
            val parentEntry = remember(entry) {
                navController.getBackStackEntry(PROMO_GRAPH_ROUTE)
            }
            // 2) Scopemos el VM al sub-grafo (UNO para todas las pantallas)
            val vm: CreatePromotionViewModel = viewModel(parentEntry)

            PromoNombreView(
                onBack = { navController.popBackStack() }, // ← NUEVO: regresa a ComercioHome
                onNext = { navController.navigate(PromoScreens.PromoDetalles.route) },
                vm = vm
            )
        }

        composable(PromoScreens.PromoDetalles.route) { entry ->
            val parentEntry = remember(entry) { navController.getBackStackEntry(PROMO_GRAPH_ROUTE) }
            val vm: CreatePromotionViewModel = viewModel(parentEntry)

            PromoDetallesView(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(PromoScreens.PromoFechas.route) },
                vm = vm
            )
        }

        composable(PromoScreens.PromoFechas.route) { entry ->
            val parentEntry = remember(entry) { navController.getBackStackEntry(PROMO_GRAPH_ROUTE) }
            val vm: CreatePromotionViewModel = viewModel(parentEntry)

            PromoFechasView(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(PromoScreens.PromoLimites.route) },
                vm = vm
            )
        }

        composable(PromoScreens.PromoLimites.route) { entry ->
            val parentEntry = remember(entry) { navController.getBackStackEntry(PROMO_GRAPH_ROUTE) }
            val vm: CreatePromotionViewModel = viewModel(parentEntry)

            PromoLimitesView(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(PromoScreens.PromoResumen.route) },
                vm = vm
            )
        }

        composable(PromoScreens.PromoResumen.route) { entry ->
            val parentEntry = remember(entry) { navController.getBackStackEntry(PROMO_GRAPH_ROUTE) }
            val vm: CreatePromotionViewModel = viewModel(parentEntry)

            PromoResumenView(
                onBack = { navController.popBackStack() },
                onFinish = {
                    // Ir a la pantalla principal de comercio y sacar del back stack el sub-grafo del flujo
                    navController.navigate(AppScreens.ComercioHome.route) {
                        popUpTo(PROMO_GRAPH_ROUTE) { inclusive = true } // cierra el flujo de registro
                        launchSingleTop = true
                    }
                },
                vm = vm
            )
        }
    }
}
