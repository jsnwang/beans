package com.moo.beans.data

/** Ordered steps of the add-receipt wizard. Drives the progress indicator. */
enum class WizardStep {
    People,
    Source,
    Items,
    Tip,
    Tax,
    Assign,
    Totals;

    fun next(): WizardStep = entries.getOrElse(ordinal + 1) { this }

    fun previous(): WizardStep = entries.getOrElse(ordinal - 1) { this }
}
