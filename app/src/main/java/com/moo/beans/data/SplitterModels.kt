package com.moo.beans.data

import java.util.UUID

data class Person(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
)

data class LineItem(
    val id: String = UUID.randomUUID().toString(),
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
