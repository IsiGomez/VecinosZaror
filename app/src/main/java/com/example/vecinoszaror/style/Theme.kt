package com.example.vecinoszaror.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
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

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeClaroOscuro,
    onPrimary = Color(0xFF00390A),
    primaryContainer = VerdeContenedorOscuro,
    onPrimaryContainer = Color(0xFFBFF0BF),

    secondary = VerdeContenedorOscuro,
    onSecondary = Blanco,
    secondaryContainer = Color(0xFF2F4F31),
    onSecondaryContainer = Color(0xFFD6EFD7),

    tertiary = Color(0xFFF0C14B),
    onTertiary = Color(0xFF402D00),
    tertiaryContainer = Color(0xFF5C4300),
    onTertiaryContainer = Color(0xFFFFDEA0),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = RojoErrorClaro,

    background = FondoOscuro,
    onBackground = TextoClaro,

    surface = FondoOscuro,
    onSurface = TextoClaro,
    surfaceVariant = Color(0xFF414941),
    onSurfaceVariant = Color(0xFFC1C9BF),

    outline = Color(0xFF8B938A),
    outlineVariant = Color(0xFF414941),

    surfaceContainerLowest = Color(0xFF0B0F0B),
    surfaceContainerLow = Color(0xFF181C18),
    surfaceContainer = Color(0xFF1C201C),
    surfaceContainerHigh = Color(0xFF262B26),
    surfaceContainerHighest = Color(0xFF313631)
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
fun VecinosZarorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}