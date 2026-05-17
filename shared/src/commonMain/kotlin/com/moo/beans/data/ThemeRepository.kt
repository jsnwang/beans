package com.moo.beans.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeRepository(private val dataStore: DataStore<Preferences>) {

    val mode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        if (prefs[PreferencesKeys.USE_SYSTEM_THEME] == true) {
            ThemeMode.System
        } else {
            ThemeMode.Seeded(prefs[PreferencesKeys.ACCENT_COLOR] ?: DEFAULT_SEED_ARGB)
        }
    }

    val darkPreference: Flow<DarkModePreference> = dataStore.data.map { prefs ->
        DarkModePreference.fromStorage(prefs[PreferencesKeys.DARK_MODE])
    }

    suspend fun setMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            when (mode) {
                ThemeMode.System -> prefs[PreferencesKeys.USE_SYSTEM_THEME] = true
                is ThemeMode.Seeded -> {
                    prefs[PreferencesKeys.USE_SYSTEM_THEME] = false
                    prefs[PreferencesKeys.ACCENT_COLOR] = mode.argb
                }
            }
        }
    }

    suspend fun setDarkPreference(preference: DarkModePreference) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.DARK_MODE] = preference.name
        }
    }

    companion object {
        const val DEFAULT_SEED_ARGB: Long = 0xFF6650A4L
        val DEFAULT_MODE: ThemeMode = ThemeMode.Seeded(DEFAULT_SEED_ARGB)
    }
}
