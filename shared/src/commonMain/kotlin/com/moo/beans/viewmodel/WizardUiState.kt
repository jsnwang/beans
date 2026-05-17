package com.moo.beans.viewmodel

import com.moo.beans.data.LineItem
import com.moo.beans.data.Person
import com.moo.beans.data.PersonTotal
import com.moo.beans.data.ReceiptSource
import com.moo.beans.data.SplitRule

data class WizardUiState(
    val people: List<Person> = emptyList(),
    val savedNames: List<String> = emptyList(),
    val source: ReceiptSource? = null,
    val items: List<LineItem> = emptyList(),
    val tax: String = "",
    val taxRule: SplitRule = SplitRule.Proportional,
    val tip: String = "",
    val tipRule: SplitRule = SplitRule.Proportional,
) {
    val taxValue: Double get() = tax.toDoubleOrNull() ?: 0.0
    val tipValue: Double get() = tip.toDoubleOrNull() ?: 0.0

    /** Items the user has actually entered — excludes the trailing blank editing row. */
    val realItems: List<LineItem>
        get() = items.filter { it.description.isNotBlank() || it.price > 0.0 }

    val itemsSubtotal: Double get() = realItems.sumOf { it.price }
    val unassignedItems: List<LineItem> get() = realItems.filter { it.assignedTo.isEmpty() }
    val totals: List<PersonTotal>
        get() = computeTotals(realItems, people, taxValue, tipValue, tipRule, taxRule)
    val grandTotal: Double get() = itemsSubtotal + taxValue + tipValue
}

internal fun computeTotals(
    items: List<LineItem>,
    people: List<Person>,
    tax: Double,
    tip: Double,
    tipRule: SplitRule,
    taxRule: SplitRule,
): List<PersonTotal> {
    val subtotals: Map<String, Double> = people.associate { person ->
        person.id to items
            .filter { person.id in it.assignedTo }
            .sumOf { it.price / it.assignedTo.size }
    }
    val assignedSum = subtotals.values.sum()
    val evenShare = 1.0 / people.size.coerceAtLeast(1)

    fun share(rule: SplitRule, personId: String): Double = when (rule) {
        SplitRule.Even -> evenShare
        SplitRule.Proportional ->
            if (assignedSum > 0.0) (subtotals[personId] ?: 0.0) / assignedSum else evenShare
    }

    return people.map { person ->
        val subtotal = subtotals[person.id] ?: 0.0
        PersonTotal(
            person = person,
            subtotal = subtotal,
            taxShare = tax * share(taxRule, person.id),
            tipShare = tip * share(tipRule, person.id),
        )
    }
}
