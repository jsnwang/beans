package com.moo.beans.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
actual fun AdBanner(modifier: Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            val metrics = context.resources.displayMetrics
            val widthDp = (metrics.widthPixels / metrics.density).toInt()
            val adaptiveSize =
                AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
            AdView(context).apply {
                setAdSize(adaptiveSize)
                adUnitId = AdUnits.ANDROID_BANNER_TEST
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
