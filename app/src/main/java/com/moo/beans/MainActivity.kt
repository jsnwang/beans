package com.moo.beans

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moo.beans.data.SplitterRepository
import com.moo.beans.data.ThemeRepository
import com.moo.beans.ui.theme.BeansTheme
import com.moo.beans.viewmodel.beansViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStore = (application as BeansApplication).dataStore
        val themeRepository = ThemeRepository(dataStore)
        val splitterRepository = SplitterRepository(dataStore)
        val factory = beansViewModelFactory(dataStore, themeRepository, splitterRepository)
        setContent {
            val mode by themeRepository.mode.collectAsStateWithLifecycle(
                initialValue = ThemeRepository.DEFAULT_MODE
            )
            BeansTheme(mode = mode) {
                BeansApp(factory)
            }
        }
    }
}
