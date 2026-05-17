package com.moo.beans.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.moo.beans.viewmodel.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeRepository(private val dataStore: DataStore<Preferences>) {

    val mode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        if (prefs[PreferencesKeys.USE_SYSTEM_THEME] == true) {
            ThemeMode.System
        } else {
            ThemeMode.Seeded(Color(prefs[PreferencesKeys.ACCENT_COLOR] ?: DEFAULT_SEED_ARGB))
        }
    }

    suspend fun setMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            when (mode) {
                ThemeMode.System -> prefs[PreferencesKeys.USE_SYSTEM_THEME] = true
                is ThemeMode.Seeded -> {
                    prefs[PreferencesKeys.USE_SYSTEM_THEME] = false
                    prefs[PreferencesKeys.ACCENT_COLOR] = mode.color.toArgb()
                }
            }
        }
    }

    companion object {
        const val DEFAULT_SEED_ARGB: Int = 0xFF6650A4.toInt()
        val DEFAULT_MODE: ThemeMode = ThemeMode.Seeded(Color(DEFAULT_SEED_ARGB))
    }
}
