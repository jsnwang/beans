package com.moo.beans

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


class BeansApplication: Application() {
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

    override fun onCreate() {
        super.onCreate()
    }

}