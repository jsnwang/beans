package com.moo.beans.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moo.beans.data.LineItem
import com.moo.beans.data.Person
import com.moo.beans.viewmodel.SplitterViewModel
import java.util.Locale

@Composable
fun SplitterScreen(
    viewModel: SplitterViewModel,
    onOpenCamera: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var addItemOpen by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<LineItem?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                ActionBar(
                    onCamera = onOpenCamera,
                    onAdd = { addItemOpen = true },
                    onReset = viewModel::resetReceipt,
                )
            }
            item { SectionHeader("People") }
            item { PeopleSection(state.people, state.savedNames, viewModel) }
            item { SectionHeader("Items (${state.items.size})") }
            if (state.items.isEmpty()) {
                item { EmptyMessage("No items yet. Tap the camera or + button.") }
            } else {
                items(state.items, key = { it.id }) { item ->
                    LineItemRow(
                        item = item,
                        people = state.people,
                        onToggle = { personId -> viewModel.toggleAssignment(item.id, personId) },
                        onAssignAll = { viewModel.assignItemToAll(item.id) },
                        onClear = { viewModel.clearAssignments(item.id) },
                        onEdit = { editItem = item },
                        onDelete = { viewModel.removeItem(item.id) },
                    )
                }
            }
            item { SectionHeader("Tax & Tip") }
            item { TaxTipRow(state.tax, state.tip, viewModel::setTax, viewModel::setTip) }
            item { SectionHeader("Totals") }
            item {
                TotalsSection(
                    state = state,
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (addItemOpen) {
        ItemDialog(
            title = "Add item",
            initial = null,
            confirmLabel = "Add",
            onDismiss = { addItemOpen = false },
            onConfirm = { desc, price ->
                viewModel.addItem(desc, price)
                addItemOpen = false
            },
        )
    }
    editItem?.let { current ->
        ItemDialog(
            title = "Edit item",
            initial = current,
            confirmLabel = "Save",
            onDismiss = { editItem = null },
            onConfirm = { desc, price ->
                viewModel.updateItem(current.id, desc, price)
                editItem = null
            },
        )
    }
}

@Composable
private fun ActionBar(onCamera: () -> Unit, onAdd: () -> Unit, onReset: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(onClick = onCamera) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Scan receipt")
        }
        Row {
            IconButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
            IconButton(onClick = onReset) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset receipt")
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
    )
    HorizontalDivider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeopleSection(
    people: List<Person>,
    savedNames: List<String>,
    viewModel: SplitterViewModel,
) {
    var newName by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                viewModel.addPerson(newName)
                newName = ""
            }) { Text("Add") }
        }
        if (people.isNotEmpty()) {
            FlowRowChips {
                people.forEach { person ->
                    InputChip(
                        selected = false,
                        onClick = { viewModel.removePerson(person.id) },
                        label = { Text(person.name) },
                        trailingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = "Remove ${person.name}")
                        },
                    )
                }
            }
        }
        val suggestions = savedNames.filter { name ->
            people.none { it.name.equals(name, ignoreCase = true) }
        }
        if (suggestions.isNotEmpty()) {
            Text("Recent:", style = MaterialTheme.typography.labelMedium)
            FlowRowChips {
                suggestions.forEach { name ->
                    AssistChip(
                        onClick = { viewModel.addPerson(name) },
                        label = { Text(name) },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LineItemRow(
    item: LineItem,
    people: List<Person>,
    onToggle: (String) -> Unit,
    onAssignAll: () -> Unit,
    onClear: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.description, fontWeight = FontWeight.Medium)
                    Text(formatMoney(item.price), style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            if (people.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                FlowRowChips {
                    people.forEach { person ->
                        FilterChip(
                            selected = person.id in item.assignedTo,
                            onClick = { onToggle(person.id) },
                            label = { Text(person.name) },
                        )
                    }
                    if (people.size > 1) {
                        ElevatedAssistChip(
                            onClick = onAssignAll,
                            label = { Text("Split all") },
                        )
                    }
                    if (item.assignedTo.isNotEmpty()) {
                        ElevatedAssistChip(
                            onClick = onClear,
                            label = { Text("Clear") },
                        )
                    }
                }
                if (item.assignedTo.isEmpty()) {
                    Text(
                        "Unassigned",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            } else {
                Text(
                    "Add a person above to assign this item.",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun TaxTipRow(
    tax: String,
    tip: String,
    onTaxChange: (String) -> Unit,
    onTipChange: (String) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = tax,
            onValueChange = onTaxChange,
            label = { Text("Tax") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
            value = tip,
            onValueChange = onTipChange,
            label = { Text("Tip") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TotalsSection(state: com.moo.beans.viewmodel.SplitterUiState) {
    if (state.people.isEmpty()) {
        EmptyMessage("Add people to see per-person totals.")
        return
    }
    Column {
        state.totals.forEach { total ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(total.person.name, fontWeight = FontWeight.Medium)
                Text(formatMoney(total.total), fontWeight = FontWeight.Medium)
            }
            Text(
                "items ${formatMoney(total.subtotal)} · tax ${formatMoney(total.taxShare)} · tip ${formatMoney(total.tipShare)}",
                style = MaterialTheme.typography.labelSmall,
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Receipt total", fontWeight = FontWeight.Bold)
            Text(formatMoney(state.grandTotal), fontWeight = FontWeight.Bold)
        }
        if (state.unassignedItems.isNotEmpty()) {
            Text(
                "${state.unassignedItems.size} unassigned item(s) — ${formatMoney(state.unassignedItems.sumOf { it.price })}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun EmptyMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ItemDialog(
    title: String,
    initial: LineItem?,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (description: String, price: Double) -> Unit,
) {
    var desc by remember { mutableStateOf(initial?.description ?: "") }
    var priceText by remember {
        mutableStateOf(initial?.let { String.format(Locale.US, "%.2f", it.price) } ?: "")
    }
    val priceValue = priceText.toDoubleOrNull()
    val canConfirm = desc.isNotBlank() && priceValue != null && priceValue > 0.0
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    singleLine = true,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                            priceText = input
                        }
                    },
                    label = { Text("Price") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(desc, priceValue ?: 0.0) }, enabled = canConfirm) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FlowRowChips(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        content()
    }
}

private fun formatMoney(value: Double): String = "$" + String.format(Locale.US, "%.2f", value)
