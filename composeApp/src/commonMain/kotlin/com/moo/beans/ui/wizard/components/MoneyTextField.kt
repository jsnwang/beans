package com.moo.beans.ui.wizard.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

private val MoneyRegex = Regex("""^\d*\.?\d{0,2}$""")

/** Numeric input that only accepts up-to-two-decimal money strings. */
@Composable
fun MoneyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            if (input.isEmpty() || (input.length <= 8 && input.matches(MoneyRegex))) {
                onValueChange(input)
            }
        },
        label = { Text(label) },
        prefix = { Text("$") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = imeAction,
        ),
        modifier = modifier,
    )
}
