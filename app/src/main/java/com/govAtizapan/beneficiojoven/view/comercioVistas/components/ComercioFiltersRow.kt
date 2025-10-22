package com.govAtizapan.beneficiojoven.view.comercioVistas.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.view.home.homeViews.TealLight
import com.govAtizapan.beneficiojoven.view.home.homeViews.TealPrimary

/**
 * Filtros para "Todas / Activas / Inactivas", con el MISMO look&feel que Home:
 * - selectedContainerColor = TealLight
 * - selectedLabelColor = Color.White
 * - borde con TealPrimary y shape 16.dp
 * (mismo patrón de FilterChip que usas en HomeView).  :contentReference[oaicite:1]{index=1}
 */
enum class ComercioFilter(val label: String) {
    TODAS("Todas"),
    ACTIVAS("Activas"),
    INACTIVAS("Inactivas")
}

@Composable
fun ComercioFiltersRow(
    selected: ComercioFilter,
    onSelected: (ComercioFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val opciones = ComercioFilter.entries

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(opciones) { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                enabled = true,
                label = {
                    Text(
                        text = option.label,
                        fontFamily = PoppinsFamily,
                        fontSize = 14.sp,
                        fontWeight = if (selected == option) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected == option) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TealLight,    // igual que Home
                    selectedLabelColor = Color.White       // igual que Home
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = TealPrimary,              // igual que Home
                    selectedBorderColor = Color.Transparent,
                    enabled = true,
                    selected = selected == option
                ),
                shape = RoundedCornerShape(16.dp)           // igual que Home
            )
        }
    }
}
