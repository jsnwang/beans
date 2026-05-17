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

    enum class Named(override val displayName: String, val argb: Long) : ThemeOption {
        Purple("Purple", ThemeRepository.DEFAULT_SEED_ARGB),
        Pink("Pink", 0xFFD81B60L),
        Red("Red", 0xFFE53935L),
        Orange("Orange", 0xFFEF6C00L),
        Yellow("Yellow", 0xFFF9A825L),
        Green("Green", 0xFF43A047L),
        Teal("Teal", 0xFF00897BL),
        Blue("Blue", 0xFF1976D2L),
        Indigo("Indigo", 0xFF3F51B5L);

        val seedColor: Color get() = Color(argb)

        override val mode: ThemeMode get() = ThemeMode.Seeded(argb)
    }

    companion object {
        val all: List<ThemeOption> = listOf<ThemeOption>(System) + Named.entries

        fun forMode(mode: ThemeMode): ThemeOption = when (mode) {
            ThemeMode.System -> System
            is ThemeMode.Seeded -> Named.entries.firstOrNull { it.argb == mode.argb }
                ?: Named.Purple
        }
    }
}
