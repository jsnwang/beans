package com.moo.beans

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moo.beans.ui.CalculatorScreen
import com.moo.beans.ui.CameraCaptureScreen
import com.moo.beans.ui.Screen
import com.moo.beans.ui.SettingsScreen
import com.moo.beans.ui.SplitterScreen
import com.moo.beans.viewmodel.CalculatorViewModel
import com.moo.beans.viewmodel.SettingsViewModel
import com.moo.beans.viewmodel.SplitterViewModel

@Composable
fun BeansApp(factory: ViewModelProvider.Factory) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Screen.SPLITTER_CAMERA_ROUTE

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    listOf(Screen.Calculator, Screen.Splitter, Screen.Settings).forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calculator.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Calculator.route) {
                CalculatorScreen(viewModel<CalculatorViewModel>(factory = factory))
            }
            composable(Screen.Splitter.route) {
                SplitterScreen(
                    viewModel = viewModel<SplitterViewModel>(factory = factory),
                    onOpenCamera = { navController.navigate(Screen.SPLITTER_CAMERA_ROUTE) },
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel<SettingsViewModel>(factory = factory))
            }
            composable(Screen.SPLITTER_CAMERA_ROUTE) { entry ->
                val splitterEntry = remember(entry) {
                    navController.getBackStackEntry(Screen.Splitter.route)
                }
                val splitterVm: SplitterViewModel = viewModel(
                    viewModelStoreOwner = splitterEntry,
                    factory = factory,
                )
                CameraCaptureScreen(
                    splitterViewModel = splitterVm,
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}
