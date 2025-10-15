package com.govAtizapan.beneficiojoven.ui.theme.uiComponents


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

// ======= Paleta y forma "pill" (ajústalas si quieres) =======
private val PillBg = Color(0xFFDDF7F7)        // celeste suave
private val PillStroke = Color(0x33000000)    // borde sutil (20% negro)
private val DisabledGray = Color(0xFFE6E6E6)  // fondo deshabilitado
private val LabelHint = Color(0x99000000)     // label apagado

private val PillShape = RoundedCornerShape(20.dp)

// ============================================================
// AppTextField: TextField con estilo "pill + celeste"
// ============================================================
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    supportingText: String? = null,
    textStyle: TextStyle = LocalTextStyle.current
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        isError = isError,
        supportingText = { if (supportingText != null) Text(supportingText) },
        shape = PillShape,
        textStyle = textStyle.copy(color = MaterialTheme.colorScheme.onSurface),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = PillBg,
            unfocusedContainerColor = PillBg,
            disabledContainerColor = DisabledGray,
            focusedBorderColor = PillStroke,
            unfocusedBorderColor = PillStroke,
            disabledBorderColor = PillStroke,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            unfocusedLabelColor = LabelHint
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

// ============================================================
// AppPrimaryButton: Botón primario con estados habilitado/deshabilitado
// ============================================================
@Composable
fun AppPrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (enabled) MaterialTheme.colorScheme.primary else DisabledGray
    val fg = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bg,
            contentColor = fg
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text)
    }
}

// ============================================================
// AppThinDividerWithDot: divisor fino con punto centrado
// ============================================================
@Composable
fun AppThinDividerWithDot(
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth()) {
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        Spacer(Modifier.height(6.dp))

        // Usamos contentAlignment para evitar el error de align() en BoxScope
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            Surface(
                modifier = Modifier.size(6.dp),
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            ) {}
        }
    }
}
