package com.moo.beans.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SplitterRepository(private val dataStore: DataStore<Preferences>) {

    val savedNames: Flow<List<String>> = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.SAVED_NAMES]
            ?.split(NAME_DELIMITER)
            ?.filter { it.isNotBlank() }
            .orEmpty()
    }

    suspend fun saveNames(names: List<String>) {
        val cleaned = names.map { it.trim() }.filter { it.isNotBlank() }.distinct()
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.SAVED_NAMES] = cleaned.joinToString(NAME_DELIMITER.toString())
        }
    }

    companion object {
        private const val NAME_DELIMITER = '\n'
    }
}
