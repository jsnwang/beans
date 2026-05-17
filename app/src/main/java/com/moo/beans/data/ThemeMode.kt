package com.moo.beans.data

import androidx.compose.ui.graphics.Color

sealed interface ThemeMode {
    data object System : ThemeMode
    data class Seeded(val color: Color) : ThemeMode
}
