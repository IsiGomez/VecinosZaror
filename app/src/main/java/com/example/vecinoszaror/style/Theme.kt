package com.example.vecinoszaror.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val EsquemaClaro = lightColorScheme(
    primary = VerdePrincipal,
    onPrimary = Blanco,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeOscuro,

    secondary = VerdeOscuro,
    onSecondary = Blanco,
    secondaryContainer = VerdeSuave,
    onSecondaryContainer = VerdeOscuro,

    tertiary = AmbarPendiente,
    onTertiary = Blanco,
    tertiaryContainer = AmbarPendienteClaro,
    onTertiaryContainer = AmbarPendiente,

    error = RojoError,
    onError = Blanco,
    errorContainer = RojoErrorClaro,
    onErrorContainer = RojoError,

    background = FondoClaro,
    onBackground = TextoOscuro,

    surface = FondoClaro,
    onSurface = TextoOscuro,
    surfaceVariant = Color(0xFFDDE5DB),
    onSurfaceVariant = TextoSecundario,

    outline = BordeSuave,
    outlineVariant = BordeClaro,

    surfaceContainerLowest = Blanco,
    surfaceContainerLow = Color(0xFFF3F7F3),
    surfaceContainer = Color(0xFFEFF4EF),
    surfaceContainerHigh = Color(0xFFEAF0EA),
    surfaceContainerHighest = Color(0xFFE4EBE4)
)

// Esquinas redondeadas más suaves
private val Formas = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun VecinosZarorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}