package com.moo.beans.ui.theme

import androidx.compose.ui.graphics.Color
import com.moo.beans.data.ThemeMode
import com.moo.beans.data.ThemeRepository

sealed interface ThemeOption {
    val displayName: String
    val mode: ThemeMode

    data object System : ThemeOption {
        override val displayName: String = "System"
        override val mode: ThemeMode = ThemeMode.System
    }

    enum class Named(override val displayName: String, val seedColor: Color) : ThemeOption {
        Purple("Purple", Color(ThemeRepository.DEFAULT_SEED_ARGB)),
        Pink("Pink", Color(0xFFD81B60.toInt())),
        Red("Red", Color(0xFFE53935.toInt())),
        Orange("Orange", Color(0xFFEF6C00.toInt())),
        Yellow("Yellow", Color(0xFFF9A825.toInt())),
        Green("Green", Color(0xFF43A047.toInt())),
        Teal("Teal", Color(0xFF00897B.toInt())),
        Blue("Blue", Color(0xFF1976D2.toInt())),
        Indigo("Indigo", Color(0xFF3F51B5.toInt()));

        override val mode: ThemeMode get() = ThemeMode.Seeded(seedColor)
    }

    companion object {
        val all: List<ThemeOption> = listOf<ThemeOption>(System) + Named.values()

        fun forMode(mode: ThemeMode): ThemeOption = when (mode) {
            ThemeMode.System -> System
            is ThemeMode.Seeded -> Named.values().firstOrNull { it.seedColor == mode.color } ?: Named.Purple
        }
    }
}
