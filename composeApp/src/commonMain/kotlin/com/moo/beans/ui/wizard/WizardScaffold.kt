package com.moo.beans.ui.wizard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moo.beans.data.WizardStep

private fun stepTitle(step: WizardStep): String = when (step) {
    WizardStep.People -> "Who's involved?"
    WizardStep.Source -> "Add the receipt"
    WizardStep.Items -> "Items & prices"
    WizardStep.Tip -> "Tip"
    WizardStep.Tax -> "Tax"
    WizardStep.Assign -> "Who had what?"
    WizardStep.Totals -> "The split"
}

/** Shared chrome for every wizard step: title, progress, and Back/Continue actions. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardScaffold(
    step: WizardStep,
    onClose: () -> Unit,
    onBack: (() -> Unit)? = null,
    continueLabel: String? = "Continue",
    continueEnabled: Boolean = true,
    onContinue: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stepTitle(step)) },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel")
                        }
                    },
                )
                LinearProgressIndicator(
                    progress = { (step.ordinal + 1).toFloat() / WizardStep.entries.size },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .animateContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (onBack != null) {
                        OutlinedButton(onClick = onBack) { Text("Back") }
                    }
                    Spacer(Modifier.weight(1f))
                    if (continueLabel != null) {
                        Button(onClick = onContinue, enabled = continueEnabled) {
                            Text(continueLabel)
                        }
                    }
                }
            }
        },
        content = content,
    )
}
