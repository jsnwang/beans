package com.moo.beans.ui.wizard.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moo.beans.data.LineItem
import com.moo.beans.data.WizardStep
import com.moo.beans.ui.wizard.WizardScaffold
import com.moo.beans.ui.wizard.components.MoneyTextField
import com.moo.beans.util.toFixed2
import com.moo.beans.viewmodel.WizardViewModel

@Composable
fun ItemsStep(
    viewModel: WizardViewModel,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.ensureEditableRows() }

    WizardScaffold(
        step = WizardStep.Items,
        onClose = onClose,
        onBack = onBack,
        continueEnabled = state.realItems.isNotEmpty(),
        onContinue = {
            viewModel.commitItems()
            onContinue()
        },
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
                    "Add each item and its price — a new row appears as you fill the last one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(state.items, key = { it.id }) { item ->
                ItemEditRow(
                    item = item,
                    onDescriptionChange = { viewModel.setItemDescription(item.id, it) },
                    onPriceChange = { viewModel.setItemPrice(item.id, it) },
                    onRemove = { viewModel.removeItem(item.id) },
                )
            }
        }
    }
}

@Composable
private fun ItemEditRow(
    item: LineItem,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (Double) -> Unit,
    onRemove: () -> Unit,
) {
    var priceText by remember(item.id) {
        mutableStateOf(if (item.price > 0.0) item.price.toFixed2() else "")
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = item.description,
            onValueChange = onDescriptionChange,
            label = { Text("Item") },
            singleLine = true,
            modifier = Modifier.weight(1.6f),
        )
        MoneyTextField(
            value = priceText,
            onValueChange = {
                priceText = it
                onPriceChange(it.toDoubleOrNull() ?: 0.0)
            },
            label = "Price",
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Delete item")
        }
    }
}
