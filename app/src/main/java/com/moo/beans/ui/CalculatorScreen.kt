package com.moo.beans.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moo.beans.R
import com.moo.beans.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TotalTextField(state.total, viewModel::setTotal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = viewModel::toggleLock) {
                    val icon = painterResource(
                        id = if (state.locked) R.drawable.round_lock_24
                        else R.drawable.round_lock_open_24
                    )
                    Icon(icon, contentDescription = "Lock")
                }
                Slider(
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .fillMaxWidth(.85f),
                    value = state.tipPercent.toFloat(),
                    onValueChange = viewModel::setTipPercent,
                    valueRange = state.tipMin.toFloat()..state.tipMax.toFloat(),
                    enabled = !state.locked
                )
                Text(
                    modifier = Modifier.padding(5.dp, 8.dp),
                    text = "${state.tipPercent}%",
                    fontSize = 20.sp
                )
            }
            Column(
                modifier = Modifier.padding(0.dp, 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.tip).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                SelectionContainer {
                    Text(text = state.tip, fontSize = 48.sp)
                }
            }
            Column(
                modifier = Modifier.padding(0.dp, 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.total).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                SelectionContainer {
                    Text(text = state.tipPlusTotal, fontSize = 48.sp)
                }
            }
        }
    }
}
