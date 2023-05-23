package com.moo.beans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moo.beans.ui.TotalTextField
import com.moo.beans.viewmodel.BeansViewModel
import com.moo.beans.viewmodel.BeansViewModelFactory

@Composable
fun BeansApp() {
    val viewModel: BeansViewModel = viewModel(factory = BeansViewModelFactory())
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TotalTextField(viewModel = viewModel)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Slider(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(.85f),
                    value = viewModel.getTipPercentage(),
                    onValueChange = { viewModel.percent.value = it },
                    valueRange = (10f)..(20f),
                    steps = 9,

                    )
                Text(
                    text = "${viewModel.getTipPercentage().toInt()}%",
                    fontSize = 20.sp
                )
            }
            Text(text = viewModel.getTip(), fontSize = 48.sp)
            Text(text = viewModel.getTipPlusTotal(), fontSize = 48.sp)
        }
    }
}