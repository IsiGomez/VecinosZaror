package com.example.vecinoszaror.ui.utils

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

enum class TipoPantalla { COMPACTA, MEDIANA, EXPANDIDA }

/** Traduce el [WindowSizeClass] a [TipoPantalla]. */
fun WindowSizeClass.tipoPantalla(): TipoPantalla = when (widthSizeClass) {
    WindowWidthSizeClass.Compact -> TipoPantalla.COMPACTA
    WindowWidthSizeClass.Medium -> TipoPantalla.MEDIANA
    else -> TipoPantalla.EXPANDIDA
}
