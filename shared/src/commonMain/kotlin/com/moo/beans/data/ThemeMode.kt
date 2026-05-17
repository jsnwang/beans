package com.moo.beans.data

sealed interface ThemeMode {
    data object System : ThemeMode
    data class Seeded(val argb: Long) : ThemeMode
}
