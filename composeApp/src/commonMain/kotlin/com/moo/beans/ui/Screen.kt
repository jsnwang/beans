package com.moo.beans.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** A top-level destination shown as a tab in the bottom navigation bar. */
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Calculator : Screen("calculator", "Calculator", Icons.Filled.Calculate)
    data object People : Screen("people", "People", Icons.Filled.Group)
    data object Receipts : Screen("receipts", "Receipts", Icons.Filled.ReceiptLong)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings)

    companion object {
        /** Parent route of the add-receipt wizard nested graph. */
        const val WIZARD_ROUTE = "wizard"
        const val WIZARD_PEOPLE_ROUTE = "wizard/people"
        const val WIZARD_SOURCE_ROUTE = "wizard/source"
        const val WIZARD_ITEMS_ROUTE = "wizard/items"
        const val WIZARD_TIP_ROUTE = "wizard/tip"
        const val WIZARD_TAX_ROUTE = "wizard/tax"
        const val WIZARD_ASSIGN_ROUTE = "wizard/assign"
        const val WIZARD_TOTALS_ROUTE = "wizard/totals"

        /** Full-screen camera capture — sibling of the wizard graph, not nested. */
        const val WIZARD_CAMERA_ROUTE = "wizard_camera"
    }
}
