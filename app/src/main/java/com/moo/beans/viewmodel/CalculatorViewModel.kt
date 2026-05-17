package com.moo.beans.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode

class CalculatorViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                val min = preferences[PreferencesKeys.TIP_MIN] ?: 10
                val max = preferences[PreferencesKeys.TIP_MAX] ?: 20
                val saved = preferences[PreferencesKeys.TIP_PERCENT] ?: 15
                _uiState.update {
                    it.copy(
                        tipPercent = saved.coerceIn(min, max),
                        locked = preferences[PreferencesKeys.LOCK] ?: false,
                        tipMin = min,
                        tipMax = max,
                    )
                }
                recalculate()
            }
        }
    }

    fun setTotal(s: String) {
        val dotCount = s.count { it == '.' }
        if (s.length <= 7 && s.all { it.isDigit() || it == '.' } && dotCount <= 1) {
            _uiState.update { it.copy(total = s) }
            recalculate()
        }
    }

    fun setTipPercent(f: Float) {
        _uiState.update { it.copy(tipPercent = f.toInt()) }
        recalculate()
        savePercent()
    }

    fun toggleLock() {
        _uiState.update { it.copy(locked = !it.locked) }
        saveLocked()
    }

    private fun recalculate() {
        val state = _uiState.value
        val amount = state.total.toFloatOrNull()
        if (amount == null || amount <= 0f) {
            _uiState.update { it.copy(tip = "0", tipPlusTotal = "0") }
            return
        }
        val tip = (amount * (state.tipPercent / 100f)).toBigDecimal()
            .setScale(2, RoundingMode.HALF_UP)
        val total = (amount.toBigDecimal() + tip).setScale(2, RoundingMode.HALF_UP)
        _uiState.update { it.copy(tip = tip.toString(), tipPlusTotal = total.toString()) }
    }

    private fun saveLocked() {
        viewModelScope.launch {
            dataStore.edit { it[PreferencesKeys.LOCK] = _uiState.value.locked }
        }
    }

    private fun savePercent() {
        viewModelScope.launch {
            dataStore.edit { it[PreferencesKeys.TIP_PERCENT] = _uiState.value.tipPercent }
        }
    }
}
