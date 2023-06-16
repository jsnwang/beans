package com.moo.beans.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.math.RoundingMode

class BeansViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {

    private val _tip =  mutableStateOf("")
    val tip: State<String> = _tip

    private var _total =  mutableStateOf("")
    var total: State<String> = _total

    private val _percent =  mutableStateOf(15f)
    var percent: State<Float> = _percent

    private val _lock = mutableStateOf(false)
    var lock: State<Boolean> = _lock

    private val _people =  mutableStateOf("")
    val people: State<String> = _people

    private val _split =  mutableStateOf("")
    val split: State<String> = _split

    init {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                _percent.value = preferences[PreferencesKeys.TIP_PERCENT] ?: 15f
                _lock.value = preferences[PreferencesKeys.LOCK] ?: false
            }
        }
    }

    fun lockorUnlock() {
        _lock.value = !lock.value
        saveLocked()
    }
    fun isLocked(): Boolean{
        return lock.value
    }

    fun setTotal(s: String) {
        _total.value = s
    }
    fun setPeople(s: String) {
        _people.value = s
    }

    fun getTipPercentage(): Float {
        return percent.value
    }

    fun setTipPercentage(f: Float) {
        _percent.value = f
        savePercent()
    }

    fun getTip(): String {
        return if (total.value > 0.toString()) {
            val calc = (total.value.toFloat().times(percent.value.div(100))).toBigDecimal()
            _tip.value = calc.setScale(2, RoundingMode.HALF_UP).toString()
            return tip.value
        } else {
            "0"
        }
    }
    fun getTipPlusTotal(): String {
        return if (total.value > 0.toString() && tip.value > 0.toString()) {
            (total.value.toBigDecimal() + tip.value.toBigDecimal()).setScale(2, RoundingMode.HALF_UP).toString()
        } else {
            "0"
        }
    }

    fun calcSplit(): String {
        return if (total.value > 0.toString() && people.value > 0.toString()) {
            _split.value = total.value.toFloat().div(people.value.toFloat()).toString()
            split.value
        } else {
            "0"
        }
    }

    private fun saveLocked() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.LOCK] = lock.value
            }
        }
    }

    private fun savePercent() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.TIP_PERCENT] = percent.value
            }
        }
    }
}