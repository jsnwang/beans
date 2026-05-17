package com.moo.beans.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val TIP_PERCENT = intPreferencesKey("tip_percent")
    val LOCK = booleanPreferencesKey("locked")
    val TIP_MIN = intPreferencesKey("tip_min")
    val TIP_MAX = intPreferencesKey("tip_max")
    val ACCENT_COLOR = longPreferencesKey("accent_color")
    val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
    val DARK_MODE = stringPreferencesKey("dark_mode")
    val SAVED_NAMES = stringPreferencesKey("saved_names")
}
