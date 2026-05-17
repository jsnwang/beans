package com.moo.beans.ui

import androidx.compose.runtime.Composable
import com.moo.beans.data.LineItem

@Composable
expect fun CameraCaptureScreen(
    onItemsParsed: (List<LineItem>) -> Unit,
    onDone: () -> Unit,
)
