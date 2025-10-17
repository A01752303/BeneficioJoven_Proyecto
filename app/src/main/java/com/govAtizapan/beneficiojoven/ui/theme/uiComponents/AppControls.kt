package com.govAtizapan.beneficiojoven.ui.theme.uiComponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text)
    }
}

@Composable
fun AppThinDividerWithDot() {
    Surface(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {}
}
