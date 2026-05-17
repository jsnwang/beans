package com.moo.beans.data

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
private fun newId(): String = Uuid.random().toString()

data class Person(
    val id: String = newId(),
    val name: String,
)

data class LineItem(
    val id: String = newId(),
    val description: String,
    val price: Double,
    val assignedTo: Set<String> = emptySet(),
)

data class PersonTotal(
    val person: Person,
    val subtotal: Double,
    val taxShare: Double,
    val tipShare: Double,
) {
    val total: Double get() = subtotal + taxShare + tipShare
}
