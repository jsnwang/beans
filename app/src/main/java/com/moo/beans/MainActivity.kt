package com.moo.beans

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.moo.beans.ui.theme.BeansTheme
import com.moo.beans.viewmodel.BeansViewModel
import com.moo.beans.viewmodel.BeansViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: BeansViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = requireNotNull(this).application as BeansApplication
        val viewModelFactory = BeansViewModelFactory(application.dataStore)
        viewModel = ViewModelProvider(this, viewModelFactory)[BeansViewModel::class.java]

        setContent {
            BeansTheme {
                BeansApp(viewModel)
            }
        }
    }
}
