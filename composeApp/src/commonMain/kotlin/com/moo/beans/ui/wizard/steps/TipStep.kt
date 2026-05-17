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
import com.moo.beans.util.toMoney
import com.moo.beans.viewmodel.WizardViewModel

@Composable
fun TipStep(
    viewModel: WizardViewModel,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WizardScaffold(
        step = WizardStep.Tip,
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
                "Items subtotal so far: ${state.itemsSubtotal.toMoney()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            MoneyTextField(
                value = state.tip,
                onValueChange = viewModel::setTip,
                label = "Tip amount",
                modifier = Modifier.fillMaxWidth(),
            )
            Text("How should the tip be split?", style = MaterialTheme.typography.titleSmall)
            RuleToggle(rule = state.tipRule, onRuleChange = viewModel::setTipRule)
            SplitPreview(
                title = "Tip per person",
                totals = state.totals,
                amount = { it.tipShare },
            )
        }
    }
}
