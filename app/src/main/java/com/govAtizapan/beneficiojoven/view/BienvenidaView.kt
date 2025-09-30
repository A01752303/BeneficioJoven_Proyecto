package com.govAtizapan.beneficiojoven.view

import androidx.benchmark.traceprocessor.Row
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.govAtizapan.beneficiojoven.R
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.view.OnboardingDataClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val onboardingPages = listOf(
    OnboardingDataClass(
        title = "¡Ahorra en los mejores lugares del municipio!",
        description = " Con Beneficio Joven, canjea descuentos exclusivos en tus restaurantes, tiendas y servicios favoritos.",
        imageRes = R.drawable.onboarding_00
    ),
    OnboardingDataClass(
        title = "¡Canjear es más fácil que nunca!",
        description = "1. Explora las ofertas cerca de ti. \n" +
                "2. Guarda los cupones que más te gusten. \n" +
                "3. Muestra el código en el negocio y ¡listo!",
        imageRes = R.drawable.onboarding_00
    ),
    OnboardingDataClass(
        title = "¡Hecha para la banda de Atizapán!",
        description = "¿Tienes entre 12 y 29? Esta app es para ti. Encuentra promos para tus planes, invitar a tu crush o para darte un gusto.",
        imageRes = R.drawable.onboarding_00
    )
)
@Composable
fun BienvenidaView(navController: NavController){

    OnboardingScreen(pageItems = onboardingPages,
        onFinish = {println("Listo")}
    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    pageItems: List<OnboardingDataClass>,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Necesitamos un CoroutineScope para poder llamar a animateScrollToPage desde el botón
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = { pageItems.size })

// --- INICIO DE LA CORRECCIÓN ---

        // 1. Obtenemos el estado de arrastre del usuario
        val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

        // 2. Usamos 'isDragged' como la clave (key) del LaunchedEffect.
        //    - Cuando el usuario NO arrastra (isDragged = false), el efecto se activa.
        //    - Cuando el usuario EMPIEZA a arrastrar (isDragged = true), el efecto se cancela.
        //    - Cuando el usuario DEJA de arrastrar (isDragged = false), el efecto se reinicia.
        LaunchedEffect(isDragged) {
            // Solo ejecutamos el bucle si el usuario no está arrastrando
            if (!isDragged) {
                while (true) {
                    delay(3000L) // Esperamos 3 segundos
                    // Calculamos la siguiente página de forma segura
                    val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                    // La animación ahora se completará sin interrupciones
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }

        // --- FIN DE LA CORRECCIÓN --

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPage(item = pageItems[pageIndex])
        }

        // --- INICIO DE LOS CAMBIOS DE LAYOUT Y LÓGICA ---

        // 1. Agrupamos el indicador y el botón en una Columna
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PagerIndicator(
                pageCount = pageItems.size,
                currentPageIndex = pagerState.currentPage,
            )

            Spacer(modifier = Modifier.height(24.dp)) // Espacio entre el indicador y el botón

            Button(
                onClick = {
                    onFinish()
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                // El texto del botón también cambia según la página
                val buttonText = "¡COMENZAR!"
                Text(buttonText)
            }
            Spacer(modifier = Modifier.height(24.dp)) // Espacio entre el indicador y el botón
            Image(
                painter = painterResource(id = R.drawable.logos_pie), // <-- CAMBIA ESTO
                contentDescription = null, // Es decorativa, puede ser null
                modifier = Modifier // ⬅️ La pega hasta abajo
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }

    }
}
@Composable
fun OnboardingPage(item: OnboardingDataClass, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                bottom = 130.dp,
                start = 30.dp,
                top = 30.dp,
                end = 30.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = TealPrimary,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontFamily = PoppinsFamily,
            fontSize = 14.sp
        )
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            modifier = Modifier.size(200.dp)
        )
    }
}

// Tu PagerIndicator se puede mantener exactamente igual,
// pero lo coloco aquí para que el ejemplo esté completo.
@Composable
fun PagerIndicator(pageCount: Int, currentPageIndex: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 24.dp), // Un poco más de padding
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { iteration ->
            val color = if (currentPageIndex == iteration) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(14.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BienvenidaPreview(modifier: Modifier = Modifier) {
    BienvenidaView(navController = rememberNavController())
}






