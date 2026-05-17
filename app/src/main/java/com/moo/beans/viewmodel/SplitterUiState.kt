package com.moo.beans.viewmodel

import com.moo.beans.data.LineItem
import com.moo.beans.data.Person
import com.moo.beans.data.PersonTotal

data class SplitterUiState(
    val items: List<LineItem> = emptyList(),
    val people: List<Person> = emptyList(),
    val savedNames: List<String> = emptyList(),
    val tax: String = "",
    val tip: String = "",
    val parsing: Boolean = false,
    val parseError: String? = null,
) {
    val taxValue: Double get() = tax.toDoubleOrNull() ?: 0.0
    val tipValue: Double get() = tip.toDoubleOrNull() ?: 0.0
    val itemsSubtotal: Double get() = items.sumOf { it.price }
    val unassignedItems: List<LineItem> get() = items.filter { it.assignedTo.isEmpty() }
    val totals: List<PersonTotal> get() = computeTotals(items, people, taxValue, tipValue)
    val grandTotal: Double get() = itemsSubtotal + taxValue + tipValue
}

internal fun computeTotals(
    items: List<LineItem>,
    people: List<Person>,
    tax: Double,
    tip: Double,
): List<PersonTotal> {
    val subtotals: Map<String, Double> = people.associate { person ->
        person.id to items
            .filter { person.id in it.assignedTo }
            .sumOf { it.price / it.assignedTo.size }
    }
    val assignedSum = subtotals.values.sum()
    return people.map { person ->
        val subtotal = subtotals[person.id] ?: 0.0
        val share = if (assignedSum > 0.0) subtotal / assignedSum else 0.0
        PersonTotal(
            person = person,
            subtotal = subtotal,
            taxShare = tax * share,
            tipShare = tip * share,
        )
    }
}
