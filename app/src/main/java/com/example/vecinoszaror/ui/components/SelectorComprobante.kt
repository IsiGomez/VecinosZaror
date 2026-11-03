package com.example.vecinoszaror.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.vecinoszaror.util.copiarAComprobantes
import com.example.vecinoszaror.util.crearUriParaFoto
import kotlinx.coroutines.launch
import androidx.core.net.toUri

/**
 * Permite agregar la foto de un comprobante con la cámara o desde la galería.
 * Muestra la foto elegida y avisa el resultado con [onUriChange] (null = se quitó la foto).
 */
@Composable
fun SelectorComprobante(
    uri: Uri?,
    onUriChange: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Dirección donde la cámara guardará la foto
    var direccionCamara by rememberSaveable { mutableStateOf<String?>(null) }

    // Cámara: devuelve true si se tomó la foto y false si se canceló
    val lanzadorCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { seTomoFoto ->
        val direccion = direccionCamara?.toUri()
        if (seTomoFoto && direccion != null) {
            onUriChange(direccion)
        } else if (direccion != null) {
            // Se cancelo: se borra el archivo vacío que se había creado
            context.contentResolver.delete(direccion, null, null)
        }
    }

    // Galería: devuelve la dirección de la imagen elegida, o null si se canceló
    val lanzadorGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { origen ->
        if (origen != null) {
            scope.launch {
                copiarAComprobantes(context, origen)?.let { onUriChange(it) }
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagenInteligente(
            uri = uri,
            modifier = Modifier.size(width = 240.dp, height = 160.dp),
            forma = RoundedCornerShape(12.dp),
            iconoVacio = Icons.Filled.AddCircle,
            descripcion = "Foto del comprobante"
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                enabled = enabled,
                onClick = {
                    val nueva = crearUriParaFoto(context)
                    direccionCamara = nueva.toString()
                    lanzadorCamara.launch(nueva)
                }
            ) { Text("Tomar foto") }
            OutlinedButton(
                enabled = enabled,
                onClick = { lanzadorGaleria.launch("image/*") }
            ) { Text("Galería") }
        }
        if (uri != null) {
            TextButton(enabled = enabled, onClick = { onUriChange(null) }) { Text("Quitar foto") }
        }
    }
}
