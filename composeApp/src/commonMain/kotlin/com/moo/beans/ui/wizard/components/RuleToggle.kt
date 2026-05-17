package com.moo.beans.ui.wizard.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moo.beans.data.SplitRule

/** Segmented toggle for choosing how a shared amount is split. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleToggle(
    rule: SplitRule,
    onRuleChange: (SplitRule) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        SegmentedButton(
            selected = rule == SplitRule.Even,
            onClick = { onRuleChange(SplitRule.Even) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
        ) {
            Text("Split evenly")
        }
        SegmentedButton(
            selected = rule == SplitRule.Proportional,
            onClick = { onRuleChange(SplitRule.Proportional) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
        ) {
            Text("By item cost")
        }
    }
}
