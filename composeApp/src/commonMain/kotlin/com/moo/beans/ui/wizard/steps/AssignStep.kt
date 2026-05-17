package com.moo.beans.ui.wizard.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moo.beans.data.WizardStep
import com.moo.beans.ui.wizard.WizardScaffold
import com.moo.beans.ui.wizard.components.AssignableItemRow
import com.moo.beans.viewmodel.WizardViewModel

@Composable
fun AssignStep(
    viewModel: WizardViewModel,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WizardScaffold(
        step = WizardStep.Assign,
        onClose = onClose,
        onBack = onBack,
        continueLabel = "See the split",
        onContinue = onContinue,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    "Tap a name on each item to mark who shared it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(state.realItems, key = { it.id }) { item ->
                AssignableItemRow(
                    item = item,
                    people = state.people,
                    onToggle = { personId -> viewModel.toggleAssignment(item.id, personId) },
                    onAssignAll = { viewModel.assignItemToAll(item.id) },
                    onClear = { viewModel.clearAssignments(item.id) },
                )
            }
            if (state.unassignedItems.isNotEmpty()) {
                item {
                    Text(
                        "${state.unassignedItems.size} item(s) still unassigned — " +
                            "they won't be billed to anyone.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}
