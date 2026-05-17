package com.moo.beans.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Calculator : Screen("calculator", "Calculator", Icons.Filled.Calculate)
    object Splitter   : Screen("splitter",   "Splitter",   Icons.AutoMirrored.Filled.CallSplit)
    object Settings   : Screen("settings",   "Settings",   Icons.Filled.Settings)

    companion object {
        const val SPLITTER_CAMERA_ROUTE = "splitter_camera"
    }
}
