package com.moo.beans.ui.wizard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moo.beans.data.LineItem
import com.moo.beans.data.Person
import com.moo.beans.util.toMoney

/** A receipt line item with per-person assignment chips. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AssignableItemRow(
    item: LineItem,
    people: List<Person>,
    onToggle: (personId: String) -> Unit,
    onAssignAll: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = item.description.ifBlank { "Untitled item" },
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Text(item.price.toMoney(), fontWeight = FontWeight.Medium)
            }
            if (people.isEmpty()) {
                Text(
                    "Add people first to assign this item.",
                    style = MaterialTheme.typography.labelSmall,
                )
                return@Card
            }
            Spacer(Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                people.forEach { person ->
                    FilterChip(
                        selected = person.id in item.assignedTo,
                        onClick = { onToggle(person.id) },
                        label = { Text(person.name) },
                    )
                }
                if (people.size > 1) {
                    ElevatedAssistChip(onClick = onAssignAll, label = { Text("Split all") })
                }
                if (item.assignedTo.isNotEmpty()) {
                    ElevatedAssistChip(onClick = onClear, label = { Text("Clear") })
                }
            }
            if (item.assignedTo.isEmpty()) {
                Text(
                    "Unassigned",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
