package com.moo.beans.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moo.beans.data.DarkModePreference
import com.moo.beans.data.PreferencesKeys
import com.moo.beans.data.ThemeMode
import com.moo.beans.data.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStore: DataStore<Preferences>,
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                _uiState.update {
                    it.copy(
                        tipMin = preferences[PreferencesKeys.TIP_MIN] ?: 10,
                        tipMax = preferences[PreferencesKeys.TIP_MAX] ?: 20,
                    )
                }
            }
        }
        viewModelScope.launch {
            themeRepository.mode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            themeRepository.darkPreference.collect { darkMode ->
                _uiState.update { it.copy(darkMode = darkMode) }
            }
        }
    }

    fun decrementMin() = setTipRange(_uiState.value.tipMin - 1, _uiState.value.tipMax)
    fun incrementMin() = setTipRange(_uiState.value.tipMin + 1, _uiState.value.tipMax)
    fun decrementMax() = setTipRange(_uiState.value.tipMin, _uiState.value.tipMax - 1)
    fun incrementMax() = setTipRange(_uiState.value.tipMin, _uiState.value.tipMax + 1)

    private fun setTipRange(min: Int, max: Int) {
        val clampedMin = min.coerceIn(ABSOLUTE_MIN, ABSOLUTE_MAX - 1)
        val clampedMax = max.coerceIn(clampedMin + 1, ABSOLUTE_MAX)
        if (clampedMin == _uiState.value.tipMin && clampedMax == _uiState.value.tipMax) return
        viewModelScope.launch {
            dataStore.edit {
                it[PreferencesKeys.TIP_MIN] = clampedMin
                it[PreferencesKeys.TIP_MAX] = clampedMax
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { themeRepository.setMode(mode) }
    }

    fun setDarkMode(preference: DarkModePreference) {
        viewModelScope.launch { themeRepository.setDarkPreference(preference) }
    }

    companion object {
        const val ABSOLUTE_MIN = 0
        const val ABSOLUTE_MAX = 50
    }
}
