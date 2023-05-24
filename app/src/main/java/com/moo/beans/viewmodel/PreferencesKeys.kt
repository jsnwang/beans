package com.moo.beans.viewmodel

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
object PreferencesKeys {
    val TIP_PERCENT = floatPreferencesKey("tip_percent")
    val LOCK = booleanPreferencesKey("locked")
}