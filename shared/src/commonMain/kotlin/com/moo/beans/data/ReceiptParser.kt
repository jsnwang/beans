package com.moo.beans.data

object ReceiptParser {

    // Digit class permitting common OCR lookalikes. Only applied inside the captured price,
    // so menu words like "OK" or "Iced" elsewhere on the line are never touched.
    private const val DIGIT = "[\\dOoDIl|iSsBZz]"
    private const val DECIMAL = "[.,．·]"
    private const val CURRENCY = "[\\$€£¥]?"

    private val priceAtEnd = Regex("""$CURRENCY\s*($DIGIT+)\s*$DECIMAL\s*($DIGIT{2})\s*$""")
    private val priceOnly = Regex("""^$CURRENCY\s*($DIGIT+)\s*$DECIMAL\s*($DIGIT{2})\s*$""")

    private val skipPattern = Regex(
        """\b(?:sub[\s-]?total|total|tax|tip|gratuity|balance|change|cash|credit|debit|card|visa|mastercard|amex|discover|amount|due|paid|owe|service\s+charge|service\s+fee)\b""",
        RegexOption.IGNORE_CASE,
    )

    fun parse(lines: List<String>): List<LineItem> {
        val cleaned = stitchOrphanPrices(lines.asSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList())
        return cleaned.mapNotNull(::parseLine)
    }

    // Some receipts (or blurry OCR splits) put the price on its own line directly
    // after the description. Merge those back together before parsing.
    private fun stitchOrphanPrices(lines: List<String>): List<String> {
        val out = ArrayList<String>(lines.size)
        for (line in lines) {
            if (priceOnly.matches(line) && out.isNotEmpty()) {
                val prev = out.last()
                if (priceAtEnd.containsMatchIn(prev).not() && skipPattern.containsMatchIn(prev).not()) {
                    out[out.lastIndex] = "$prev $line"
                    continue
                }
            }
            out.add(line)
        }
        return out
    }

    private fun parseLine(raw: String): LineItem? {
        val line = raw.trim()
        if (line.isEmpty()) return null
        if (skipPattern.containsMatchIn(line)) return null
        val match = priceAtEnd.find(line) ?: return null
        val intPart = match.groupValues[1].normalizeOcrDigits()
        val fracPart = match.groupValues[2].normalizeOcrDigits()
        if (intPart.any { !it.isDigit() } || fracPart.any { !it.isDigit() }) return null
        val price = "$intPart.$fracPart".toDoubleOrNull() ?: return null
        if (price <= 0.0 || price > MAX_PLAUSIBLE_PRICE) return null
        val description = line.removeRange(match.range).trim().trimEnd('-', '.', ':', ',')
        if (description.length < 2) return null
        return LineItem(description = description, price = price)
    }

    private fun String.normalizeOcrDigits(): String = buildString(length) {
        for (c in this@normalizeOcrDigits) {
            append(
                when (c) {
                    'O', 'o', 'D' -> '0'
                    'I', 'l', '|', 'i' -> '1'
                    'Z', 'z' -> '2'
                    'S', 's' -> '5'
                    'B' -> '8'
                    else -> c
                },
            )
        }
    }

    private const val MAX_PLAUSIBLE_PRICE = 1000.0
}
