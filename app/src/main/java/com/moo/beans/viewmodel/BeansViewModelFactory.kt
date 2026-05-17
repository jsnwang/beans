package com.moo.beans.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.moo.beans.data.SplitterRepository
import com.moo.beans.data.ThemeRepository

fun beansViewModelFactory(
    dataStore: DataStore<Preferences>,
    themeRepository: ThemeRepository,
    splitterRepository: SplitterRepository,
): ViewModelProvider.Factory =
    viewModelFactory {
        initializer { CalculatorViewModel(dataStore) }
        initializer { SplitterViewModel(splitterRepository) }
        initializer { SettingsViewModel(dataStore, themeRepository) }
    }
