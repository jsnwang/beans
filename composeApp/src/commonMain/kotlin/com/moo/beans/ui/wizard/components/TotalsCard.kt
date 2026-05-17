package com.moo.beans.ui.wizard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moo.beans.data.LineItem
import com.moo.beans.data.PersonTotal
import com.moo.beans.util.toMoney

/** Per-person breakdown plus the receipt grand total. */
@Composable
fun TotalsCard(
    totals: List<PersonTotal>,
    grandTotal: Double,
    unassignedItems: List<LineItem>,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            totals.forEach { total ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(total.person.name, fontWeight = FontWeight.Medium)
                    Text(total.total.toMoney(), fontWeight = FontWeight.Medium)
                }
                Text(
                    "items ${total.subtotal.toMoney()} · " +
                        "tax ${total.taxShare.toMoney()} · tip ${total.tipShare.toMoney()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Receipt total", fontWeight = FontWeight.Bold)
                Text(grandTotal.toMoney(), fontWeight = FontWeight.Bold)
            }
            if (unassignedItems.isNotEmpty()) {
                Text(
                    "${unassignedItems.size} unassigned item(s) — " +
                        unassignedItems.sumOf { it.price }.toMoney(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
