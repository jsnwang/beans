package com.moo.beans.ads

import cocoapods.Google_Mobile_Ads_SDK.GADMobileAds
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun startAds() {
    GADMobileAds.sharedInstance().startWithCompletionHandler(null)
}
