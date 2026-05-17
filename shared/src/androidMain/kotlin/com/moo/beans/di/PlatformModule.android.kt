package com.moo.beans.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.moo.beans.data.DATA_STORE_FILE_NAME
import com.moo.beans.data.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<DataStore<Preferences>> {
        createDataStore {
            androidContext().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
        }
    }
}
