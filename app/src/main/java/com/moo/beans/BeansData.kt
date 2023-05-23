package com.moo.beans

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class BeansData(private val dataStore: DataStore<Preferences>) {


}

private object PreferencesKeys {
    val TIP_PERCENT = floatPreferencesKey("tip_percent")
}