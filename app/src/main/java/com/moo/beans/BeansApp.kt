package com.moo.beans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moo.beans.viewmodel.BeansViewModel
import com.moo.beans.viewmodel.BeansViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeansApp() {
    val viewModel: BeansViewModel = viewModel(factory = BeansViewModelFactory())
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), horizontalAlignment = Alignment.Start
        ) {
            TextField(
                modifier = Modifier.padding(8.dp),
                value = viewModel.total.value,
                onValueChange = { viewModel.total.value = it },
                label = { Text(text = "Total") },
                shape = ShapeDefaults.ExtraLarge,
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Slider(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(.90f),
                    value = viewModel.percent.value,
                    onValueChange = { viewModel.percent.value = it },
                    valueRange = (.10f)..(.20f),
                    steps = 9,

                    )
                Text(text = viewModel.percent.value.toString())
            }
            Text(text = viewModel.getTip())
            Text(text = viewModel.getTipPlusTotal())

        }
    }
}