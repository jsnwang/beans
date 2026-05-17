package com.moo.beans.util

import kotlin.math.abs
import kotlin.math.round

/** Round half-up to 2 decimal places. */
fun Double.roundToCents(): Double = round(this * 100.0) / 100.0

/** Format as a fixed two-decimal string ("12.34", "-3.50", "0.00"). */
fun Double.toFixed2(): String {
    val cents = round(this * 100.0).toLong()
    val sign = if (cents < 0) "-" else ""
    val absCents = abs(cents)
    val whole = absCents / 100
    val frac = (absCents % 100).toString().padStart(2, '0')
    return "$sign$whole.$frac"
}

/** Format as a money string with leading $ ("$12.34"). */
fun Double.toMoney(): String = "$" + this.toFixed2()
