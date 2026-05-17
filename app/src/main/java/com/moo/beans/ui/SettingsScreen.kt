package com.moo.beans.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moo.beans.ui.theme.ThemeOption
import com.moo.beans.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            SectionHeader("Tip slider range")
            StepperRow(
                label = "Min",
                value = state.tipMin,
                canDecrement = state.tipMin > SettingsViewModel.ABSOLUTE_MIN,
                canIncrement = state.tipMin < state.tipMax - 1,
                onDecrement = viewModel::decrementMin,
                onIncrement = viewModel::incrementMin,
            )
            StepperRow(
                label = "Max",
                value = state.tipMax,
                canDecrement = state.tipMax > state.tipMin + 1,
                canIncrement = state.tipMax < SettingsViewModel.ABSOLUTE_MAX,
                onDecrement = viewModel::decrementMax,
                onIncrement = viewModel::incrementMax,
            )

            Spacer(Modifier.height(24.dp))

            SectionHeader("Theme")
            ThemeDropdown(
                selected = ThemeOption.forMode(state.themeMode),
                onSelect = { viewModel.setThemeMode(it.mode) },
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun StepperRow(
    label: String,
    value: Int,
    canDecrement: Boolean,
    canIncrement: Boolean,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, fontSize = 16.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledIconButton(onClick = onDecrement, enabled = canDecrement) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease $label")
            }
            Text(
                text = "$value%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(48.dp),
            )
            FilledIconButton(onClick = onIncrement, enabled = canIncrement) {
                Icon(Icons.Default.Add, contentDescription = "Increase $label")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeDropdown(
    selected: ThemeOption,
    onSelect: (ThemeOption) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selected.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Accent color") },
            leadingIcon = { ThemeOptionIcon(selected) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            ThemeOption.all.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName) },
                    leadingIcon = { ThemeOptionIcon(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionIcon(option: ThemeOption) {
    when (option) {
        is ThemeOption.System -> Icon(
            imageVector = Icons.Default.Smartphone,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        is ThemeOption.Named -> Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(option.seedColor)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
        )
    }
}
