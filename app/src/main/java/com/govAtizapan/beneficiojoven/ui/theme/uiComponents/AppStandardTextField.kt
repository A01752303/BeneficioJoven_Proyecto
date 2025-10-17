package com.govAtizapan.beneficiojoven.ui.theme.uiComponents

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AppStandardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    supportingText: String? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    minHeightDp: Int = 48
) {
    val lightCyan = Color(0xFFE0F7FA)
    val borderCyan = Color(0xFFB2EBF2)
    val shape = RoundedCornerShape(16.dp)

    Column(modifier = modifier) {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            color = Color.Black
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            textStyle = textStyle,
            placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderCyan, shape)
                .defaultMinSize(minHeight = minHeightDp.dp),
            isError = isError,
            supportingText = { if (supportingText != null) Text(supportingText) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = lightCyan,
                focusedContainerColor   = lightCyan,
                disabledContainerColor  = lightCyan.copy(alpha = 0.6f),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor   = Color.Transparent,
                disabledIndicatorColor  = Color.Transparent,
                errorContainerColor     = lightCyan
            )
        )
    }
}
