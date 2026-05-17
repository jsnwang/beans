package com.moo.beans.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.moo.beans.data.DATA_STORE_FILE_NAME
import com.moo.beans.data.createDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val platformModule: Module = module {
    single<DataStore<Preferences>> {
        createDataStore {
            val documentsDir = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
            val basePath = requireNotNull(documentsDir?.path) {
                "Could not resolve iOS documents directory"
            }
            "$basePath/$DATA_STORE_FILE_NAME"
        }
    }
}
