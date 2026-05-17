package com.moo.beans.ui.wizard.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.moo.beans.ui.wizard.components.PeoplePicker
import com.moo.beans.viewmodel.WizardViewModel

@Composable
fun PeopleStep(
    viewModel: WizardViewModel,
    onClose: () -> Unit,
    onContinue: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WizardScaffold(
        step = WizardStep.People,
        onClose = onClose,
        continueEnabled = state.people.isNotEmpty(),
        onContinue = onContinue,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(
                "Add everyone splitting this receipt.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            PeoplePicker(
                people = state.people,
                savedNames = state.savedNames,
                onAddPerson = viewModel::addPerson,
                onRemovePerson = viewModel::removePerson,
            )
        }
    }
}
