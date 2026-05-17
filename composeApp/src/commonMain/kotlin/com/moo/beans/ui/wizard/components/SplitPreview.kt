package com.moo.beans.ui.wizard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moo.beans.data.PersonTotal
import com.moo.beans.util.toMoney

/** Live per-person preview of how a shared amount currently divides. */
@Composable
fun SplitPreview(
    title: String,
    totals: List<PersonTotal>,
    amount: (PersonTotal) -> Double,
    modifier: Modifier = Modifier,
) {
    if (totals.isEmpty()) return
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            totals.forEach { total ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(total.person.name, style = MaterialTheme.typography.bodyMedium)
                    Text(amount(total).toMoney(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
