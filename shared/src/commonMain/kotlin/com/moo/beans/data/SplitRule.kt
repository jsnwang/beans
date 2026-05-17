package com.moo.beans.data

/** How a shared amount (tip or tax) is divided between people. */
enum class SplitRule {
    /** Each person pays an equal share. */
    Even,

    /** Each person pays in proportion to their assigned item subtotal. */
    Proportional,
}
