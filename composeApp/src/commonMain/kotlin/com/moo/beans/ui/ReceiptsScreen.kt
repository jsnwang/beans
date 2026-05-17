package com.moo.beans.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.runtime.Composable
import com.moo.beans.ui.components.EmptyState

@Composable
fun ReceiptsScreen() {
    EmptyState(
        icon = Icons.Filled.ReceiptLong,
        title = "No receipts yet",
        message = "Tap the + button to scan or enter a receipt and split it.",
    )
}
