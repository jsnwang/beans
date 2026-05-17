package com.moo.beans.di

import com.moo.beans.data.SplitterRepository
import com.moo.beans.data.ThemeRepository
import com.moo.beans.viewmodel.CalculatorViewModel
import com.moo.beans.viewmodel.SettingsViewModel
import com.moo.beans.viewmodel.WizardViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val sharedModule: Module = module {
    single { ThemeRepository(get()) }
    single { SplitterRepository(get()) }
    factory { CalculatorViewModel(get()) }
    factory { SettingsViewModel(get(), get()) }
    factory { WizardViewModel(get()) }
}
