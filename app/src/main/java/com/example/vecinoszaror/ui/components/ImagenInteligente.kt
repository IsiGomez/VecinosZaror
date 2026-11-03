package com.example.vecinoszaror.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.vecinoszaror.style.VecinosZarorTheme

/**
 * Muestra la imagen que está en [uri], si es null muestra un ícono en su lugar.
 * Tamaño con [modifier] y el
 * Recorte con [forma] (círculo por defecto).
 */
@Composable
fun ImagenInteligente(
    uri: Uri?,
    modifier: Modifier = Modifier.size(120.dp),
    forma: Shape = CircleShape,
    iconoVacio: ImageVector = Icons.Filled.Person,
    descripcion: String = "Imagen seleccionada"
) {
    Box(
        modifier = modifier
            .clip(forma)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = descripcion,
                contentScale = ContentScale.Crop, // llena el marco recortando lo que sobra
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = iconoVacio,
                contentDescription = "Sin imagen",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImagenInteligenteSinImagenPreview() {
    VecinosZarorTheme {
        Box {
            ImagenInteligente(uri = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImagenInteligenteRectangularPreview() {
    VecinosZarorTheme {
        Box {
            ImagenInteligente(
                uri = null,
                modifier = Modifier.size(width = 200.dp, height = 120.dp),
                forma = RoundedCornerShape(12.dp)
            )
        }
    }
}
