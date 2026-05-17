package com.moo.beans

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/** Swift-callable Compose entry point. */
fun MainViewController(): UIViewController = ComposeUIViewController {
    BeansApp()
}
