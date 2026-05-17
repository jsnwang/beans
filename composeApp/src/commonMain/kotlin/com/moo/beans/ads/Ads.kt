package com.moo.beans.ads

/**
 * AdMob unit IDs.
 *
 * These are Google's official test IDs — they always serve test ads.
 * Replace with production IDs from the AdMob console before release.
 *
 * https://developers.google.com/admob/android/test-ads
 * https://developers.google.com/admob/ios/test-ads
 */
object AdUnits {
    const val ANDROID_BANNER_TEST = "ca-app-pub-3940256099942544/6300978111"
    const val IOS_BANNER_TEST = "ca-app-pub-3940256099942544/2934735716"
}

/** Initialize AdMob. Call once on app launch, before any ad request. */
expect fun startAds()
