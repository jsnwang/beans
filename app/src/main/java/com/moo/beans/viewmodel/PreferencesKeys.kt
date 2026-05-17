package com.moo.beans.viewmodel

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val TIP_PERCENT = intPreferencesKey("tip_percent")
    val LOCK = booleanPreferencesKey("locked")
    val TIP_MIN = intPreferencesKey("tip_min")
    val TIP_MAX = intPreferencesKey("tip_max")
    val ACCENT_COLOR = intPreferencesKey("accent_color")
    val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
    val SAVED_NAMES = stringPreferencesKey("saved_names")
}