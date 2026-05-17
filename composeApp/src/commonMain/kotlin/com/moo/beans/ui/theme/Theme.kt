package com.moo.beans.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.rememberDynamicColorScheme
import com.moo.beans.data.DarkModePreference
import com.moo.beans.data.ThemeMode
import com.moo.beans.data.ThemeRepository

@Composable
expect fun platformSystemColorScheme(useDarkTheme: Boolean): ColorScheme?

@Composable
fun BeansTheme(
    mode: ThemeMode,
    darkPreference: DarkModePreference = DarkModePreference.System,
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (darkPreference) {
        DarkModePreference.System -> isSystemInDarkTheme()
        DarkModePreference.Light -> false
        DarkModePreference.Dark -> true
    }
    val colors = when (mode) {
        ThemeMode.System -> platformSystemColorScheme(useDarkTheme)
            ?: rememberDynamicColorScheme(
                seedColor = Color(ThemeRepository.DEFAULT_SEED_ARGB),
                isDark = useDarkTheme,
                isAmoled = false,
            )
        is ThemeMode.Seeded -> rememberDynamicColorScheme(
            seedColor = Color(mode.argb),
            isDark = useDarkTheme,
            isAmoled = false,
        )
    }
    MaterialTheme(colorScheme = colors, content = content)
}
