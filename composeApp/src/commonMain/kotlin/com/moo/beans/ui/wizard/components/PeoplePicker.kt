package com.moo.beans.ui.wizard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.moo.beans.data.Person

/** Add/remove the people splitting a receipt, with one-tap suggestions from recent names. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PeoplePicker(
    people: List<Person>,
    savedNames: List<String>,
    onAddPerson: (String) -> Unit,
    onRemovePerson: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var newName by remember { mutableStateOf("") }
    fun submit() {
        onAddPerson(newName)
        newName = ""
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = ::submit, enabled = newName.isNotBlank()) { Text("Add") }
        }

        if (people.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                people.forEach { person ->
                    InputChip(
                        selected = true,
                        onClick = { onRemovePerson(person.id) },
                        label = { Text(person.name) },
                        trailingIcon = {
                            Icon(Icons.Default.Close, contentDescription = "Remove ${person.name}")
                        },
                    )
                }
            }
        }

        val suggestions = savedNames.filter { name ->
            people.none { it.name.equals(name, ignoreCase = true) }
        }
        if (suggestions.isNotEmpty()) {
            Text("Recent", style = MaterialTheme.typography.labelMedium)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                suggestions.forEach { name ->
                    AssistChip(
                        onClick = { onAddPerson(name) },
                        label = { Text(name) },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                    )
                }
            }
        }
    }
}
