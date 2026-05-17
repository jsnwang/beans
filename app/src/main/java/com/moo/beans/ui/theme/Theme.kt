package com.moo.beans.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.rememberDynamicColorScheme
import com.moo.beans.data.ThemeMode
import com.moo.beans.data.ThemeRepository

@Composable
fun BeansTheme(
    mode: ThemeMode,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = when (mode) {
        ThemeMode.System -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                rememberDynamicColorScheme(Color(ThemeRepository.DEFAULT_SEED_ARGB), useDarkTheme)
            }
        }
        is ThemeMode.Seeded -> rememberDynamicColorScheme(mode.color, useDarkTheme)
    }
    MaterialTheme(colorScheme = colors, content = content)
}
