/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define el sub-grafo de navegación `promo_graph`, que controla
 * todo el flujo paso a paso para registrar una nueva promoción dentro del módulo
 * de comercio.
 *
 * Flujo de pantallas:
 * 1. `PromoNombre`   -> Captura nombre/título y tipo de promoción.
 * 2. `PromoDetalles` -> Captura mecánica, porcentaje/precio según tipo.
 * 3. `PromoFechas`   -> Captura rango de vigencia e imagen opcional.
 * 4. `PromoLimites`  -> Captura límites de canje (totales / por usuario).
 * 5. `PromoResumen`  -> Muestra resumen final y confirma el envío.
 *
 * Aspectos clave:
 * * `PROMO_GRAPH_ROUTE` es la ruta raíz del sub-grafo.
 * * Cada pantalla se registra con `composable(...)` dentro de `navigation(...)`.
 * * Se usa `remember { navController.getBackStackEntry(PROMO_GRAPH_ROUTE) }`
 * para obtener siempre el mismo `NavBackStackEntry` padre.
 * * Con ese `parentEntry`, se instancia el mismo `CreatePromotionViewModel`
 * vía `viewModel(parentEntry)` en TODAS las pantallas.
 *
 * ¿Por qué eso importa?
 * * El ViewModel (`CreatePromotionViewModel`) vive a nivel del sub-grafo,
 * no de cada pantalla individual. Eso significa que los datos capturados
 * en pantallas anteriores (por ejemplo, nombre, fechas, imagen) se conservan
 * mientras el usuario navega adelante/atrás en el flujo.
 *
 * Navegación de salida:
 * * En `PromoResumenView.onFinish`, se navega a `AppScreens.ComercioHome`
 * y se hace `popUpTo(PROMO_GRAPH_ROUTE) { inclusive = true }` para sacar
 * todo el flujo del back stack, de modo que al terminar el registro de promoción
 * ya no se pueda volver al wizard con "Back".
 *
 * Uso:
 * Llamar `addPromoGraph(navController)` dentro del `NavHost` principal para
 * montar este flujo de creación de promociones en la app.
 */




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
