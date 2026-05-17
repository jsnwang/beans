package com.moo.beans

import android.app.Application
import com.moo.beans.ads.startAds
import com.moo.beans.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class BeansApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@BeansApplication)
        }
        startAds()
    }
}
