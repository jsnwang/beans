package com.moo.beans.data

object ReceiptParser {

    private val priceAtEnd = Regex("""\$?\s*(\d+\.\d{2})\s*$""")
    private val skipKeywords = setOf(
        "subtotal", "total", "tax", "tip", "gratuity", "balance",
        "change", "cash", "credit", "debit", "card",
        "visa", "mastercard", "amex", "discover",
        "amount", "due", "paid", "owe", "service",
    )

    fun parse(lines: List<String>): List<LineItem> = lines.mapNotNull(::parseLine)

    private fun parseLine(raw: String): LineItem? {
        val line = raw.trim()
        if (line.isEmpty()) return null
        val match = priceAtEnd.find(line) ?: return null
        val price = match.groupValues[1].toDoubleOrNull() ?: return null
        if (price <= 0.0 || price > MAX_PLAUSIBLE_PRICE) return null
        val description = line.removeRange(match.range).trim().trimEnd('-', '.', ':', ',')
        if (description.isEmpty()) return null
        val lower = description.lowercase()
        if (skipKeywords.any { lower.contains(it) }) return null
        return LineItem(description = description, price = price)
    }

    private const val MAX_PLAUSIBLE_PRICE = 1000.0
}
