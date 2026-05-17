package com.moo.beans.ui.wizard.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moo.beans.data.WizardStep
import com.moo.beans.ui.wizard.WizardScaffold
import com.moo.beans.ui.wizard.components.TotalsCard
import com.moo.beans.viewmodel.WizardViewModel

@Composable
fun TotalsStep(
    viewModel: WizardViewModel,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WizardScaffold(
        step = WizardStep.Totals,
        onClose = onClose,
        onBack = onBack,
        continueLabel = "Done",
        onContinue = onDone,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Here's what everyone owes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TotalsCard(
                totals = state.totals,
                grandTotal = state.grandTotal,
                unassignedItems = state.unassignedItems,
            )
        }
    }
}
