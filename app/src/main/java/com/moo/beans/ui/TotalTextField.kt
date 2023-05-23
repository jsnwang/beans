package com.moo.beans.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moo.beans.viewmodel.BeansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotalTextField(viewModel: BeansViewModel) {
    TextField(
        modifier = Modifier
            .padding(8.dp)
            .height(120.dp)
            .width(280.dp),
        textStyle = TextStyle(fontSize = 64.sp, textAlign = TextAlign.Center),
        value = viewModel.total.value,
        onValueChange = {
            if (it.all { char -> char.isDigit() || char == '.' })
                viewModel.total.value = it
        },
        label = null,
        shape = ShapeDefaults.ExtraLarge,
        colors = TextFieldDefaults.textFieldColors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}