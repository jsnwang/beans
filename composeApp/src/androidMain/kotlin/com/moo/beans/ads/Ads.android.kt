package com.moo.beans.ads

import com.google.android.gms.ads.MobileAds
import org.koin.mp.KoinPlatform.getKoin
import android.content.Context

actual fun startAds() {
    val context: Context = getKoin().get()
    MobileAds.initialize(context) { /* status callback — no-op */ }
}
