package com.moo.beans.viewmodel

data class CalculatorUiState(
    val total: String = "",
    val tipPercent: Int = 15,
    val locked: Boolean = false,
    val tip: String = "0",
    val tipPlusTotal: String = "0",
    val tipMin: Int = 10,
    val tipMax: Int = 20,
)
