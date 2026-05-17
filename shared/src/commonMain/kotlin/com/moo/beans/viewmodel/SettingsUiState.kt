package com.moo.beans.viewmodel

import com.moo.beans.data.DarkModePreference
import com.moo.beans.data.ThemeMode
import com.moo.beans.data.ThemeRepository

data class SettingsUiState(
    val tipMin: Int = 10,
    val tipMax: Int = 20,
    val themeMode: ThemeMode = ThemeRepository.DEFAULT_MODE,
    val darkMode: DarkModePreference = DarkModePreference.DEFAULT,
)
