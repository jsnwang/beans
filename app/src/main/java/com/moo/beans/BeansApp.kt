package com.moo.beans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moo.beans.ui.TotalTextField
import com.moo.beans.viewmodel.BeansViewModel

@Composable
fun BeansApp(viewModel: BeansViewModel) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TotalTextField(viewModel = viewModel)
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
            Text(text = stringResource(id = R.string.tip), fontSize = 30.sp)
            Text(text = viewModel.getTip(), fontSize = 48.sp)
            Text(text = stringResource(id = R.string.total), fontSize = 30.sp)
            Text(text = viewModel.getTipPlusTotal(), fontSize = 48.sp)
        }
    }
}