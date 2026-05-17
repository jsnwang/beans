package com.moo.beans.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moo.beans.data.LineItem
import com.moo.beans.data.Person
import com.moo.beans.data.SplitterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplitterViewModel(
    private val repository: SplitterRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplitterUiState())
    val uiState: StateFlow<SplitterUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.savedNames.collect { names ->
                _uiState.update { it.copy(savedNames = names) }
            }
        }
    }

    fun addItem(description: String, price: Double) {
        val cleaned = description.trim()
        if (cleaned.isEmpty() || price <= 0.0) return
        _uiState.update { it.copy(items = it.items + LineItem(description = cleaned, price = price)) }
    }

    fun addItems(newItems: List<LineItem>) {
        if (newItems.isEmpty()) return
        _uiState.update { it.copy(items = it.items + newItems) }
    }

    fun updateItem(id: String, description: String, price: Double) {
        _uiState.update { state ->
            state.copy(items = state.items.map {
                if (it.id == id) it.copy(description = description.trim(), price = price) else it
            })
        }
    }

    fun removeItem(id: String) {
        _uiState.update { it.copy(items = it.items.filterNot { item -> item.id == id }) }
    }

    fun addPerson(name: String) {
        val cleaned = name.trim()
        if (cleaned.isEmpty()) return
        if (_uiState.value.people.any { it.name.equals(cleaned, ignoreCase = true) }) return
        val person = Person(name = cleaned)
        _uiState.update { it.copy(people = it.people + person) }
        rememberName(cleaned)
    }

    fun removePerson(id: String) {
        _uiState.update { state ->
            state.copy(
                people = state.people.filterNot { it.id == id },
                items = state.items.map { it.copy(assignedTo = it.assignedTo - id) },
            )
        }
    }

    fun toggleAssignment(itemId: String, personId: String) {
        _uiState.update { state ->
            state.copy(items = state.items.map { item ->
                if (item.id != itemId) item
                else item.copy(
                    assignedTo = if (personId in item.assignedTo) item.assignedTo - personId
                    else item.assignedTo + personId
                )
            })
        }
    }

    fun assignItemToAll(itemId: String) {
        _uiState.update { state ->
            val allIds = state.people.map { it.id }.toSet()
            state.copy(items = state.items.map { item ->
                if (item.id == itemId) item.copy(assignedTo = allIds) else item
            })
        }
    }

    fun clearAssignments(itemId: String) {
        _uiState.update { state ->
            state.copy(items = state.items.map { item ->
                if (item.id == itemId) item.copy(assignedTo = emptySet()) else item
            })
        }
    }

    fun setTax(value: String) {
        if (!isValidMoneyInput(value)) return
        _uiState.update { it.copy(tax = value) }
    }

    fun setTip(value: String) {
        if (!isValidMoneyInput(value)) return
        _uiState.update { it.copy(tip = value) }
    }

    fun removeSavedName(name: String) {
        viewModelScope.launch {
            repository.saveNames(_uiState.value.savedNames.filterNot { it.equals(name, ignoreCase = true) })
        }
    }

    fun setParsing(flag: Boolean) {
        _uiState.update { it.copy(parsing = flag, parseError = null) }
    }

    fun setParseError(message: String?) {
        _uiState.update { it.copy(parsing = false, parseError = message) }
    }

    fun resetReceipt() {
        _uiState.update { it.copy(items = emptyList(), tax = "", tip = "", parseError = null) }
    }

    private fun rememberName(name: String) {
        viewModelScope.launch {
            val updated = (_uiState.value.savedNames + name).distinctBy { it.lowercase() }
            repository.saveNames(updated)
        }
    }

    private fun isValidMoneyInput(s: String): Boolean {
        if (s.isEmpty()) return true
        if (s.length > 8) return false
        val regex = Regex("""^\d*\.?\d{0,2}$""")
        return regex.matches(s)
    }
}
