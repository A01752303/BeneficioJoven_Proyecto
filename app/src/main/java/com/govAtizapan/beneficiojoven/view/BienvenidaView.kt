package com.govAtizapan.beneficiojoven.view

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
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
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import kotlinx.coroutines.delay
import coil.compose.AsyncImage


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
        imageRes = R.drawable.ilustracioncupones_02_01
    ),
    OnboardingDataClass(
        title = "¡Hecha para la banda de Atizapán!",
        description = "¿Tienes entre 12 y 29? Esta app es para ti. Encuentra promos para tus planes, invitar a tu crush o para darte un gusto.",
        imageRes = R.drawable.ilustracioncupones_03_01
    )
)

// Función principal
@Composable
fun BienvenidaView(navController: NavController){
    OnboardingScreen(pageItems = onboardingPages,
        onFinish = {navController.popBackStack()
            navController.navigate(AppScreens.LoginView.route)}
    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    pageItems: List<OnboardingDataClass>,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Se cambió el Box principal por un Column.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .safeDrawingPadding(), // Padding para la barra de navegación
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val pagerState = rememberPagerState(pageCount = { pageItems.size })
        val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

        // El LaunchedEffect para el auto-scroll sigue igual y funciona perfecto.
        LaunchedEffect(isDragged) {
            if (!isDragged) {
                while (true) {
                    delay(3000L)
                    val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }

        // 2. Se añadió Modifier.weight(1f) al Pager.
        // Esto hace que ocupe todo el espacio vertical restante.
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            OnboardingPage(item = pageItems[pageIndex])
        }

        // 3. Los elementos de control ahora están directamente en la Column principal.
        // Ya no necesitan un `align` porque la Column los posiciona secuencialmente.
        Spacer(modifier = Modifier.height(8.dp))

        PagerIndicator(
            pageCount = pageItems.size,
            currentPageIndex = pagerState.currentPage,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TealPrimary,
                contentColor = White
            )
        ) {
            val buttonText = "¡COMENZAR!"
            Text(
                buttonText,
                fontSize = 20.sp,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AsyncImage(
            model = R.drawable.logos_pie,
            contentDescription = "Logos de patrocinadores",
            modifier = Modifier.height(50.dp),
            contentScale = ContentScale.Fit
        )

        // Un pequeño espacio en la parte inferior para que no quede pegado al borde.
        Spacer(modifier = Modifier.height(16.dp))
    }
}
@Composable
fun OnboardingPage(item: OnboardingDataClass, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
            .safeDrawingPadding(),
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
        AsyncImage(
            model = item.imageRes,
            contentDescription = item.title,
            modifier = Modifier.size(250.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun PagerIndicator(pageCount: Int, currentPageIndex: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .safeDrawingPadding(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { iteration ->
            val color = if (currentPageIndex == iteration) Color.DarkGray else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BienvenidaPreview() {
    BienvenidaView(navController = rememberNavController())
}






