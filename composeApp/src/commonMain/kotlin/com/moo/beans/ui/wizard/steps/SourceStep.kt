package com.moo.beans.ui.wizard.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.moo.beans.data.ReceiptSource
import com.moo.beans.data.WizardStep
import com.moo.beans.ui.wizard.WizardScaffold
import com.moo.beans.viewmodel.WizardViewModel

@Composable
fun SourceStep(
    viewModel: WizardViewModel,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onScan: () -> Unit,
    onManual: () -> Unit,
) {
    WizardScaffold(
        step = WizardStep.Source,
        onClose = onClose,
        onBack = onBack,
        continueLabel = null,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "How do you want to enter the items?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SourceOption(
                icon = Icons.Filled.CameraAlt,
                title = "Scan receipt",
                subtitle = "Snap a photo and we'll read the items for you.",
                onClick = {
                    viewModel.setSource(ReceiptSource.Scan)
                    onScan()
                },
            )
            SourceOption(
                icon = Icons.Filled.Edit,
                title = "Enter manually",
                subtitle = "Type each item and price yourself.",
                onClick = {
                    viewModel.setSource(ReceiptSource.Manual)
                    onManual()
                },
            )
        }
    }
}

@Composable
private fun SourceOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.size(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
