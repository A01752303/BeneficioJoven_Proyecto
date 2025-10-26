/**

 * Autor: Tadeo Emanuel Arellano Conde
 *
 * Descripción:
 * Este archivo define el componente Compose `PromoImagenSection`, encargado de manejar
 * toda la UI relacionada con la imagen de una promoción mientras se está creando o editando.
 *
 * Funcionalidad principal:
 * * Permite al usuario seleccionar una imagen desde la galería usando
 * `rememberLauncherForActivityResult(ActivityResultContracts.GetContent)`.
 * * Muestra el estado actual de la imagen seleccionada (nombre del botón cambia entre
 * "Elegir imagen" / "Cambiar imagen").
 * * Ofrece un botón "Quitar" para eliminar la imagen elegida.
 * * Renderiza una vista previa (`Image`) con esquinas redondeadas si hay imagen seleccionada.
 *
 * Integración con ViewModel:
 * * Se conecta al `CreatePromotionViewModel` y observa su estado (`vm.ui.collectAsState()`).
 * * Envía eventos al ViewModel usando `CreatePromotionEvent`, por ejemplo:
 * * `ImagenSeleccionada(uri)` cuando se elige una imagen.
 * * `QuitarImagen` para limpiar la selección.
 * * `SetContentResolver` vía `LaunchedEffect`, para que el VM pueda construir
 * ```
el multipart/form-data al momento de hacer el POST al backend.
```
 *
 * Uso:
 * Esta sección se utiliza dentro del flujo de creación de promoción de un comercio para
 * adjuntar una imagen opcional que será enviada al backend junto con el resto de los datos.
 */



package com.govAtizapan.beneficiojoven.view.comercioVistas.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.govAtizapan.beneficiojoven.ui.theme.PoppinsFamily
import com.govAtizapan.beneficiojoven.ui.theme.TealPrimary
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionEvent
import com.govAtizapan.beneficiojoven.viewmodel.createPromotionVM.CreatePromotionViewModel


@Composable
fun PromoImagenSection(
vm: CreatePromotionViewModel,
modifier: Modifier = Modifier
) {
val ui by vm.ui.collectAsState()
val context = LocalContext.current

// Inyectamos ContentResolver al VM (opción simple para multipart en VM)
LaunchedEffect(Unit) {
vm.onEvent(CreatePromotionEvent.SetContentResolver(context.contentResolver))
}

val launcher = rememberLauncherForActivityResult(
contract = ActivityResultContracts.GetContent()
) { uri: Uri? ->
uri?.let { vm.onEvent(CreatePromotionEvent.ImagenSeleccionada(it.toString())) }
}

Column(modifier = modifier) {
Text(
text = "Imagen promocional (opcional)",
style = MaterialTheme.typography.titleSmall,
fontFamily = PoppinsFamily,
fontWeight = FontWeight.SemiBold
)
Spacer(Modifier.height(8.dp))

Row(
verticalAlignment = Alignment.CenterVertically,
horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
Button(
onClick = { launcher.launch("image/*") },
shape = RoundedCornerShape(12.dp),
colors = ButtonDefaults.buttonColors(
containerColor = TealPrimary,
contentColor = MaterialTheme.colorScheme.onPrimary
)
) {
Text(if (ui.imagenUri == null) "Elegir imagen" else "Cambiar imagen", fontFamily = PoppinsFamily)
}

if (ui.imagenUri != null) {
OutlinedButton(
onClick = { vm.onEvent(CreatePromotionEvent.QuitarImagen) },
shape = RoundedCornerShape(12.dp)
) {
Text("Quitar", fontFamily = PoppinsFamily)
}
}
}

// Vista previa
ui.imagenUri?.let { uriStr ->
Spacer(Modifier.height(12.dp))
val painter = rememberAsyncImagePainter(model = uriStr)
Image(
painter = painter,
contentDescription = "Vista previa de la imagen",
modifier = Modifier
.fillMaxWidth()
.height(180.dp)
.clip(RoundedCornerShape(16.dp)),
contentScale = ContentScale.Crop
)
}
}
}
