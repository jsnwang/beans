package com.moo.beans

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moo.beans.ui.TotalTextField
import com.moo.beans.viewmodel.BeansViewModel

@Composable
fun BeansApp(viewModel: BeansViewModel) {
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
            TotalTextField(viewModel.total.value) {
                    viewModel.setTotal(it)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = { viewModel.lockorUnlock() }) {
                    val icon =
                        painterResource(
                            id = (if (viewModel.isLocked()) {
                                R.drawable.round_lock_24
                            } else {
                                R.drawable.round_lock_open_24
                            })
                        )
                    Icon(icon, contentDescription = "Lock")
                }
                Slider(
                    modifier = Modifier
                        .padding(0.dp, 8.dp)
                        .fillMaxWidth(.85f),
                    value = viewModel.getTipPercentage(),
                    onValueChange = { viewModel.setTipPercentage(it) },
                    valueRange = (10f)..(20f),
                    steps = 9,
                    enabled = !viewModel.isLocked()
                )
                Text(
                    modifier = Modifier.padding(5.dp, 8.dp),
                    text = "${viewModel.getTipPercentage().toInt()}%",
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
                SelectionContainer() {
                    Text(text = viewModel.getTip(), fontSize = 48.sp)
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
                SelectionContainer() {
                    Text(text = viewModel.getTipPlusTotal(), fontSize = 48.sp)
                }
            }
//            TotalTextField(viewModel.people.value) {
//                viewModel.setPeople(it)
//            }
//
//            Text(text = viewModel.calcSplit(), fontSize = 48.sp)
        }
    }
}