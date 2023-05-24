package com.moo.beans.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BeansViewModelFactory(private val dataStore: DataStore<Preferences>): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BeansViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeansViewModel(dataStore) as T
        }
        throw IllegalArgumentException("ViewModel not found")
    }
}