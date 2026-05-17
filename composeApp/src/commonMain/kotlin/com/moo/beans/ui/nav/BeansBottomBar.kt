package com.moo.beans.ui.nav

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.moo.beans.ui.Screen

private val BarHeight = 72.dp
private val FabSize = 56.dp

/**
 * Five-slot bottom navigation: four tabs flanking a centered circular FAB.
 * The FAB sits inside the bar (not docked above it).
 */
@Composable
fun BeansBottomBar(
    tabs: List<Screen>,
    currentRoute: String?,
    onTabSelected: (Screen) -> Unit,
    onFabClick: () -> Unit,
) {
    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(BarHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.take(2).forEach { screen ->
                BarItem(screen, currentRoute == screen.route) { onTabSelected(screen) }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                FabButton(onFabClick)
            }
            tabs.drop(2).forEach { screen ->
                BarItem(screen, currentRoute == screen.route) { onTabSelected(screen) }
            }
        }
    }
}

@Composable
private fun RowScope.BarItem(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "barItemColor",
    )
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .selectable(
                selected = selected,
                interactionSource = interaction,
                indication = ripple(bounded = false, radius = 44.dp),
                role = Role.Tab,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(screen.icon, contentDescription = screen.label, tint = color)
        Spacer(Modifier.height(4.dp))
        Text(screen.label, color = color, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun FabButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 6.dp,
        shadowElevation = 6.dp,
        modifier = Modifier.size(FabSize),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Add, contentDescription = "Add receipt")
        }
    }
}
