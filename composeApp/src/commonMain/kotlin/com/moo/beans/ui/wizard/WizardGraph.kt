package com.moo.beans.ui.wizard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.moo.beans.ui.Screen
import com.moo.beans.ui.wizard.steps.AssignStep
import com.moo.beans.ui.wizard.steps.ItemsStep
import com.moo.beans.ui.wizard.steps.PeopleStep
import com.moo.beans.ui.wizard.steps.SourceStep
import com.moo.beans.ui.wizard.steps.TaxStep
import com.moo.beans.ui.wizard.steps.TipStep
import com.moo.beans.ui.wizard.steps.TotalsStep
import com.moo.beans.viewmodel.WizardViewModel
import org.koin.compose.viewmodel.koinViewModel

/** Resolves the shared [WizardViewModel], scoped to the wizard graph back stack entry. */
@Composable
private fun NavBackStackEntry.wizardViewModel(navController: NavController): WizardViewModel {
    val parent = remember(this) { navController.getBackStackEntry(Screen.WIZARD_ROUTE) }
    return koinViewModel(viewModelStoreOwner = parent)
}

private fun NavController.exitWizard() {
    popBackStack(Screen.WIZARD_ROUTE, inclusive = true)
}

/** Nested navigation graph for the add-receipt wizard. */
fun NavGraphBuilder.wizardGraph(navController: NavController) {
    navigation(
        startDestination = Screen.WIZARD_PEOPLE_ROUTE,
        route = Screen.WIZARD_ROUTE,
    ) {
        composable(Screen.WIZARD_PEOPLE_ROUTE) { entry ->
            PeopleStep(
                viewModel = entry.wizardViewModel(navController),
                onClose = { navController.exitWizard() },
                onContinue = { navController.navigate(Screen.WIZARD_SOURCE_ROUTE) },
            )
        }
        composable(Screen.WIZARD_SOURCE_ROUTE) { entry ->
            SourceStep(
                viewModel = entry.wizardViewModel(navController),
                onClose = { navController.exitWizard() },
                onBack = { navController.popBackStack() },
                onScan = { navController.navigate(Screen.WIZARD_CAMERA_ROUTE) },
                onManual = { navController.navigate(Screen.WIZARD_ITEMS_ROUTE) },
            )
        }
        composable(Screen.WIZARD_ITEMS_ROUTE) { entry ->
            ItemsStep(
                viewModel = entry.wizardViewModel(navController),
                onClose = { navController.exitWizard() },
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.WIZARD_TIP_ROUTE) },
            )
        }
        composable(Screen.WIZARD_TIP_ROUTE) { entry ->
            TipStep(
                viewModel = entry.wizardViewModel(navController),
                onClose = { navController.exitWizard() },
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.WIZARD_TAX_ROUTE) },
            )
        }
        composable(Screen.WIZARD_TAX_ROUTE) { entry ->
            TaxStep(
                viewModel = entry.wizardViewModel(navController),
                onClose = { navController.exitWizard() },
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.WIZARD_ASSIGN_ROUTE) },
            )
        }
        composable(Screen.WIZARD_ASSIGN_ROUTE) { entry ->
            AssignStep(
                viewModel = entry.wizardViewModel(navController),
                onClose = { navController.exitWizard() },
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.WIZARD_TOTALS_ROUTE) },
            )
        }
        composable(Screen.WIZARD_TOTALS_ROUTE) { entry ->
            val viewModel = entry.wizardViewModel(navController)
            TotalsStep(
                viewModel = viewModel,
                onClose = { navController.exitWizard() },
                onBack = { navController.popBackStack() },
                onDone = {
                    viewModel.finalizeAndReset()
                    navController.navigate(Screen.Receipts.route) {
                        popUpTo(Screen.WIZARD_ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
