package com.moo.beans.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUIViewController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.Google_Mobile_Ads_SDK.GADBannerView
import cocoapods.Google_Mobile_Ads_SDK.GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth
import cocoapods.Google_Mobile_Ads_SDK.GADRequest
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun AdBanner(modifier: Modifier) {
    val rootViewController = LocalUIViewController.current
    val screenWidth = UIScreen.mainScreen.bounds.useContents { size.width }
    val adaptiveSize = GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(screenWidth)
    val adHeightDp = adaptiveSize.useContents { size.height }.dp
    UIKitView(
        modifier = modifier.fillMaxWidth().height(adHeightDp),
        factory = {
            GADBannerView(adSize = adaptiveSize).apply {
                adUnitID = AdUnits.IOS_BANNER_TEST
                this.rootViewController = rootViewController
                loadRequest(GADRequest())
            }
        },
    )
}
