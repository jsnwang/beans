import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        // Start Koin once at app launch.
        InitKoinIosKt.doInitKoinIos()
        // Initialize AdMob (Google Mobile Ads SDK).
        // If linking fails on this line, try `AdsIosKt.startAds()` instead — the Kotlin
        // file class name depends on which actual file the symbol lands in.
        AdsKt.startAds()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
