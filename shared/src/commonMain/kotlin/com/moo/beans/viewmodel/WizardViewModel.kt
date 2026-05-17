package com.moo.beans.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moo.beans.data.LineItem
import com.moo.beans.data.Person
import com.moo.beans.data.ReceiptSource
import com.moo.beans.data.SplitRule
import com.moo.beans.data.SplitterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WizardViewModel(
    private val repository: SplitterRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WizardUiState())
    val uiState: StateFlow<WizardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.savedNames.collect { names ->
                _uiState.update { it.copy(savedNames = names) }
            }
        }
    }

    // --- People ---

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

    fun removeSavedName(name: String) {
        viewModelScope.launch {
            repository.saveNames(
                _uiState.value.savedNames.filterNot { it.equals(name, ignoreCase = true) }
            )
        }
    }

    // --- Source ---

    fun setSource(source: ReceiptSource) {
        _uiState.update { it.copy(source = source) }
    }

    // --- Items ---

    /** Guarantees a trailing blank row so the user always has an empty cell to type into. */
    fun ensureEditableRows() {
        _uiState.update { it.copy(items = withTrailingBlankRow(it.items)) }
    }

    fun setItemDescription(id: String, description: String) {
        _uiState.update { state ->
            val items = state.items.map {
                if (it.id == id) it.copy(description = description) else it
            }
            state.copy(items = withTrailingBlankRow(items))
        }
    }

    fun setItemPrice(id: String, price: Double) {
        _uiState.update { state ->
            val items = state.items.map {
                if (it.id == id) it.copy(price = price) else it
            }
            state.copy(items = withTrailingBlankRow(items))
        }
    }

    fun removeItem(id: String) {
        _uiState.update { it.copy(items = it.items.filterNot { item -> item.id == id }) }
    }

    /** Drops blank rows and trims descriptions. Called when leaving the Items step. */
    fun commitItems() {
        _uiState.update { state ->
            state.copy(
                items = state.realItems.map { it.copy(description = it.description.trim()) }
            )
        }
    }

    /** Appends OCR-parsed items, dropping any blank editing row first. */
    fun ingestParsedItems(newItems: List<LineItem>) {
        if (newItems.isEmpty()) return
        _uiState.update { state ->
            state.copy(items = state.realItems + newItems)
        }
    }

    // --- Assignment ---

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

    // --- Tax & tip ---

    fun setTax(value: String) {
        if (!isValidMoneyInput(value)) return
        _uiState.update { it.copy(tax = value) }
    }

    fun setTip(value: String) {
        if (!isValidMoneyInput(value)) return
        _uiState.update { it.copy(tip = value) }
    }

    fun setTaxRule(rule: SplitRule) {
        _uiState.update { it.copy(taxRule = rule) }
    }

    fun setTipRule(rule: SplitRule) {
        _uiState.update { it.copy(tipRule = rule) }
    }

    // --- Lifecycle ---

    /** Clears the in-progress receipt, keeping persisted saved names. */
    fun finalizeAndReset() {
        _uiState.update { WizardUiState(savedNames = it.savedNames) }
    }

    private fun rememberName(name: String) {
        viewModelScope.launch {
            val updated = (_uiState.value.savedNames + name).distinctBy { it.lowercase() }
            repository.saveNames(updated)
        }
    }

    private fun withTrailingBlankRow(items: List<LineItem>): List<LineItem> {
        val last = items.lastOrNull()
        return if (last == null || last.description.isNotBlank() || last.price > 0.0) {
            items + LineItem(description = "", price = 0.0)
        } else {
            items
        }
    }

    private fun isValidMoneyInput(s: String): Boolean {
        if (s.isEmpty()) return true
        if (s.length > 8) return false
        return s.matches(Regex("""^\d*\.?\d{0,2}$"""))
    }
}
