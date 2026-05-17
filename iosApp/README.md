# iosApp

Xcode entry point for the Beans iOS build. Requires macOS with Xcode 15+, Cocoapods, and (one-time) [xcodegen](https://github.com/yonaskolb/XcodeGen).

## First-time setup (macOS only)

```bash
# Install tools (one time)
brew install xcodegen cocoapods

cd iosApp
xcodegen generate          # produces iosApp.xcodeproj from project.yml
pod install                # produces iosApp.xcworkspace pulling in ComposeApp.framework
open iosApp.xcworkspace    # always open the workspace, not the .xcodeproj
```

Build & run from Xcode. The `:composeApp` Kotlin module is built automatically as a Cocoapod via `pod_install` running `./gradlew :composeApp:syncFramework`.

## Notes

- `iOSApp.swift` calls `InitKoinIosKt.doInitKoinIos()` once at launch.
- `ContentView` embeds the Compose Multiplatform UI via `MainViewControllerKt.MainViewController()`.
- Camera/Vision OCR is implemented in `composeApp/src/iosMain/.../CameraCaptureScreen.ios.kt` (AVFoundation + Vision framework).
- The `project.yml` is the source of truth — do **not** commit `iosApp.xcodeproj` (it's regenerated from `project.yml`).
