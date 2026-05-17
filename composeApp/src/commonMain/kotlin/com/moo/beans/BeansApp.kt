package com.moo.beans

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moo.beans.ads.AdBanner
import com.moo.beans.data.DarkModePreference
import com.moo.beans.data.ThemeRepository
import com.moo.beans.ui.CalculatorScreen
import com.moo.beans.ui.CameraCaptureScreen
import com.moo.beans.ui.PeopleScreen
import com.moo.beans.ui.ReceiptsScreen
import com.moo.beans.ui.Screen
import com.moo.beans.ui.SettingsScreen
import com.moo.beans.ui.nav.BeansBottomBar
import com.moo.beans.ui.theme.BeansTheme
import com.moo.beans.ui.wizard.wizardGraph
import com.moo.beans.viewmodel.CalculatorViewModel
import com.moo.beans.viewmodel.SettingsViewModel
import com.moo.beans.viewmodel.WizardViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private val MAIN_TABS = listOf(
    Screen.Calculator,
    Screen.People,
    Screen.Receipts,
    Screen.Settings,
)

@Composable
fun BeansApp() {
    val themeRepository: ThemeRepository = koinInject()
    val mode by themeRepository.mode.collectAsStateWithLifecycle(
        initialValue = ThemeRepository.DEFAULT_MODE
    )
    val darkPreference by themeRepository.darkPreference.collectAsStateWithLifecycle(
        initialValue = DarkModePreference.DEFAULT
    )
    BeansTheme(mode = mode, darkPreference = darkPreference) {
        BeansNav()
    }
}

@Composable
private fun BeansNav() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val inWizard = currentRoute?.startsWith("wizard") == true

    Scaffold(
        bottomBar = {
            if (!inWizard) {
                Column {
                    AdBanner()
                    BeansBottomBar(
                        tabs = MAIN_TABS,
                        currentRoute = currentRoute,
                        onTabSelected = { screen ->
                            if (currentRoute != screen.route) navController.navigateTab(screen)
                        },
                        onFabClick = {
                            navController.navigate(Screen.WIZARD_ROUTE) { launchSingleTop = true }
                        },
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calculator.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            composable(Screen.Calculator.route) {
                CalculatorScreen(koinViewModel<CalculatorViewModel>())
            }
            composable(Screen.People.route) {
                PeopleScreen()
            }
            composable(Screen.Receipts.route) {
                ReceiptsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(koinViewModel<SettingsViewModel>())
            }
            wizardGraph(navController)
            composable(Screen.WIZARD_CAMERA_ROUTE) { entry ->
                val wizardEntry = remember(entry) {
                    navController.getBackStackEntry(Screen.WIZARD_ROUTE)
                }
                val wizardViewModel = koinViewModel<WizardViewModel>(
                    viewModelStoreOwner = wizardEntry,
                )
                CameraCaptureScreen(
                    onItemsParsed = { items ->
                        wizardViewModel.ingestParsedItems(items)
                        navController.navigate(Screen.WIZARD_ITEMS_ROUTE) {
                            popUpTo(Screen.WIZARD_CAMERA_ROUTE) { inclusive = true }
                        }
                    },
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun NavController.navigateTab(screen: Screen) {
    navigate(screen.route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
