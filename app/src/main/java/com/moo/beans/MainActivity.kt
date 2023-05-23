package com.moo.beans

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.preferences.preferencesDataStore
import com.moo.beans.ui.theme.BeansTheme

val Context.dataStore by preferencesDataStore(
    name = "preferences"
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BeansTheme {
                BeansApp()
            }
        }
    }
}
