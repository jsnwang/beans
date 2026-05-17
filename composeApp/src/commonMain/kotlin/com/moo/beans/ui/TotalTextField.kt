package com.moo.beans.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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

@Composable
fun TotalTextField(value: String, onValueChange: (s: String) -> Unit) {
    TextField(
        modifier = Modifier
            .padding(8.dp)
            .height(140.dp)
            .width(300.dp),
        textStyle = TextStyle(fontSize = 64.sp, textAlign = TextAlign.Center),
        value = value,
        onValueChange = { newValue ->
            val regex = "^([0-9]+\\.?[0-9]{0,2}|[0-9]*)$".toRegex()
            if (newValue.matches(regex)) {
                onValueChange(newValue)
            }
        },
        label = null,
        shape = ShapeDefaults.ExtraLarge,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}
