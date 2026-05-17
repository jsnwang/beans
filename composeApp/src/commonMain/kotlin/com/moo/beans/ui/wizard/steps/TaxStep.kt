package com.moo.beans.ui.wizard.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.moo.beans.ui.wizard.components.MoneyTextField
import com.moo.beans.ui.wizard.components.RuleToggle
import com.moo.beans.ui.wizard.components.SplitPreview
import com.moo.beans.viewmodel.WizardViewModel

@Composable
fun TaxStep(
    viewModel: WizardViewModel,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WizardScaffold(
        step = WizardStep.Tax,
        onClose = onClose,
        onBack = onBack,
        onContinue = onContinue,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Enter the tax from the receipt.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            MoneyTextField(
                value = state.tax,
                onValueChange = viewModel::setTax,
                label = "Tax amount",
                modifier = Modifier.fillMaxWidth(),
            )
            Text("How should the tax be split?", style = MaterialTheme.typography.titleSmall)
            RuleToggle(rule = state.taxRule, onRuleChange = viewModel::setTaxRule)
            SplitPreview(
                title = "Tax per person",
                totals = state.totals,
                amount = { it.taxShare },
            )
        }
    }
}
