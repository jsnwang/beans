package com.moo.beans.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.runtime.Composable
import com.moo.beans.ui.components.EmptyState

@Composable
fun PeopleScreen() {
    EmptyState(
        icon = Icons.Filled.Group,
        title = "People & balances",
        message = "Once you save receipts, this is where you'll see who owes whom.",
    )
}
