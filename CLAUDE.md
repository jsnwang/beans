# CLAUDE.md — Beans

Project instructions for Claude Code. Cross-cutting Kotlin / KMP / Compose principles live in the **global** `~/.claude/CLAUDE.md`; this file only covers Beans-specific decisions and what overrides the defaults.

## What Beans Is

KMP + Compose Multiplatform tip calculator + receipt splitter. Targets **Android** and **iOS** (no web, no desktop). Shipped from a single shared codebase: per-screen ViewModel + `XxxUiState` data class, state via `StateFlow`, collected with `collectAsStateWithLifecycle()`.

## Module Layout

```
beans/
├─ shared/             # KMP module — business logic + viewmodels + data layer
│  ├─ src/commonMain/  # data, viewmodels, DI declarations, utility
│  ├─ src/androidMain/ # platform Koin module (Android filesDir DataStore path)
│  └─ src/iosMain/     # platform Koin module (NSDocumentDirectory DataStore path)
├─ composeApp/         # Compose Multiplatform UI — exports the iOS framework
│  ├─ src/commonMain/  # screens, theme, root NavHost (BeansApp)
│  ├─ src/androidMain/ # CameraX + ML Kit + Accompanist actuals + Android theme actual
│  └─ src/iosMain/     # AVFoundation + Vision actuals + iOS theme actual + MainViewController
├─ androidApp/         # Android entry point: Application + MainActivity + Manifest + signing
├─ iosApp/             # Xcode project (generated from project.yml via xcodegen)
└─ gradle/libs.versions.toml   # version catalog — no hardcoded versions in build.gradle.kts
```

## Build Commands

### Android
```bash
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:assembleRelease
./gradlew :androidApp:bundleRelease   # AAB for Play
./gradlew :shared:allTests            # KMP test aggregator (jvm + iosX64 + iosSimulatorArm64)
./gradlew check                       # all verification: tests + lint
./gradlew :androidApp:lint
./gradlew clean
```

### iOS (macOS only — won't run on Windows)
```bash
brew install xcodegen cocoapods       # one-time
cd iosApp && xcodegen generate         # generates iosApp.xcodeproj from project.yml
cd iosApp && pod install               # creates iosApp.xcworkspace + builds ComposeApp.framework via Gradle
open iosApp/iosApp.xcworkspace        # build & run from Xcode
```

The `:composeApp` Cocoapods integration runs `./gradlew :composeApp:syncFramework` automatically during `pod install`.

## Architecture

```
                 ┌────────────────┐    ┌────────────────┐
                 │  androidApp    │    │     iosApp     │
                 │ MainActivity   │    │   iOSApp.swift │
                 │ BeansApplication│    │  ContentView   │
                 │ (initKoin)     │    │ (initKoinIos)  │
                 └────────┬───────┘    └────────┬───────┘
                          │   BeansApp()         │ MainViewController()
                          ▼                      ▼
                 ┌────────────────────────────────────────┐
                 │            composeApp                  │
                 │  BeansApp → NavHost → screens          │
                 │  BeansTheme (expect platformSystemColorScheme)│
                 │  CameraCaptureScreen (expect)          │
                 └────────────────┬───────────────────────┘
                                  │ koinViewModel<...>
                                  ▼
                 ┌────────────────────────────────────────┐
                 │              shared                    │
                 │  ViewModels, repositories, models      │
                 │  Koin sharedModule + expect platformModule│
                 │  DataStoreFactory (commonMain)         │
                 └────────────────────────────────────────┘
```

## Key Files

### `shared/src/commonMain`
- `data/ThemeMode.kt` — sealed interface (`System`, `Seeded(argb: Long)`). **No** Compose `Color` here — UI module converts.
- `data/ThemeRepository.kt` — `mode: Flow<ThemeMode>`, `setMode(...)`. App-wide cross-cutting state.
- `data/SplitterRepository.kt` — wraps DataStore for `SAVED_NAMES`.
- `data/SplitterModels.kt` — `LineItem`, `Person`, `PersonTotal`. IDs via `kotlin.uuid.Uuid` (experimental, opt-in).
- `data/ReceiptParser.kt` — pure: `List<String>` → `List<LineItem>`.
- `data/PreferencesKeys.kt` — DataStore keys.
- `data/DataStoreFactory.kt` — `createDataStore(producePath: () -> String)` using `PreferenceDataStoreFactory.createWithPath` (okio path).
- `data/SplitRule.kt` — `enum { Even, Proportional }` — how tip/tax divides between people.
- `data/ReceiptSource.kt` — `enum { Scan, Manual }` — how the receipt's items are entered.
- `data/WizardStep.kt` — ordered enum of the add-receipt wizard steps; drives the progress bar.
- `viewmodel/*` — `CalculatorViewModel`, `SettingsViewModel`, `WizardViewModel` + `XxxUiState` siblings. Use multiplatform `ViewModel` + `viewModelScope`. `WizardViewModel` backs the whole add-receipt flow; receipts are **not persisted** yet (in-memory only).
- `util/Money.kt` — `Double.toFixed2()`, `Double.toMoney()` — pure-Kotlin replacements for `String.format("%.2f", ...)` and `BigDecimal.HALF_UP`.
- `di/SharedModule.kt` — Koin module: repos as `single`, ViewModels as `factory` (KMP Koin doesn't have the `viewModel` DSL — that's Android-only; `koinViewModel<T>()` from `koin-compose-viewmodel` resolves the factory).
- `di/PlatformModule.kt` — `expect val platformModule: Module`.
- `di/InitKoin.kt` — `initKoin { ... }` entry point used by both apps.

### `shared/src/androidMain`
- `di/PlatformModule.android.kt` — single `DataStore<Preferences>` using `androidContext().filesDir`.

### `shared/src/iosMain`
- `di/PlatformModule.ios.kt` — single `DataStore<Preferences>` using `NSFileManager` `NSDocumentDirectory`.
- `di/InitKoinIos.kt` — `initKoinIos()` Swift-callable wrapper.

### `composeApp/src/commonMain`
- `BeansApp.kt` — root: pulls `ThemeRepository` via `koinInject()`, observes mode, wraps `BeansNav` in `BeansTheme`. NavHost wires the four tab destinations + the `wizardGraph` nested graph + the hidden `WIZARD_CAMERA_ROUTE`.
- `ui/Screen.kt` — sealed tab routes (`Calculator`, `People`, `Receipts`, `Settings`) + companion route constants for the wizard graph/steps and `WIZARD_CAMERA_ROUTE`.
- `ui/CalculatorScreen.kt` — bill input, tip slider, lock toggle (Material `Icons.Filled.Lock` / `LockOpen` — no `R.drawable`).
- `ui/SettingsScreen.kt` — min/max tip stepper pair, accent color dropdown including "System".
- `ui/PeopleScreen.kt`, `ui/ReceiptsScreen.kt` — placeholder empty-state screens (real data pending receipt persistence).
- `ui/components/EmptyState.kt` — centered icon + title + message, used by the placeholder screens.
- `ui/nav/BeansBottomBar.kt` — custom 5-slot bottom bar: four tabs flanking a centered circular `+` FAB. Replaces Material `NavigationBar`.
- `ui/wizard/` — the add-receipt wizard. `WizardGraph.kt` (nested nav graph, shared `WizardViewModel` scoped to the graph entry), `WizardScaffold.kt` (title + progress + Back/Continue chrome), `steps/` (People → Source → Items → Tip → Tax → Assign → Totals), `components/` (`PeoplePicker`, `AssignableItemRow`, `TotalsCard`, `MoneyTextField`, `RuleToggle`, `SplitPreview`).
- `ui/CameraCaptureScreen.kt` — `expect Composable` taking `onItemsParsed: (List<LineItem>) -> Unit` + `onDone`. Decoupled from any ViewModel.
- `ui/TotalTextField.kt` — numeric input.
- `ui/theme/Theme.kt` — `BeansTheme(mode, useDarkTheme)`. `Seeded` always uses material-kolor; `System` calls `expect platformSystemColorScheme(useDarkTheme)`.
- `ui/theme/ThemeOption.kt` — sealed `System | Named(enum)` for the Settings dropdown.

### `composeApp/src/commonMain` (cont.)
- `ads/AdBanner.kt` — `expect Composable AdBanner(modifier)`. Slotted in `BeansApp`'s bottomBar above `BeansBottomBar`, persistent across all tab screens, hidden on every `wizard*` route (the whole bottom bar is suppressed there).
- `ads/Ads.kt` — `expect fun startAds()` + `AdUnits` constants (currently Google's TEST IDs).

### `composeApp/src/androidMain`
- `ui/CameraCaptureScreen.android.kt` — Accompanist permissions + CameraX `PreviewView` (via `AndroidView`) + ML Kit text recognition.
- `ui/theme/Theme.android.kt` — returns `dynamic{Light,Dark}ColorScheme(LocalContext.current)` on API 31+; null on lower.
- `ads/AdBanner.android.kt` — wraps `com.google.android.gms.ads.AdView` in `AndroidView`. Banner size = `AdSize.BANNER` (320×50).
- `ads/Ads.android.kt` — `MobileAds.initialize(getKoin().get<Context>())`. Called from `BeansApplication.onCreate` after Koin setup.

### `composeApp/src/iosMain`
- `MainViewController.kt` — `fun MainViewController(): UIViewController = ComposeUIViewController { BeansApp() }`. Swift calls this.
- `ui/CameraCaptureScreen.ios.kt` — AVFoundation (`AVCaptureSession` + `AVCapturePhotoOutput` + `AVCaptureVideoPreviewLayer`) embedded in `UIKitView`; Vision framework's `VNRecognizeTextRequest` for OCR. `PhotoCaptureDelegate : NSObject(), AVCapturePhotoCaptureDelegateProtocol`.
- `ui/theme/Theme.ios.kt` — returns `null` (no system Material You on iOS — falls back to default seed).
- `ads/AdBanner.ios.kt` — wraps `GADBannerView` (Google-Mobile-Ads-SDK Cocoapod) in `UIKitView`; pulls `rootViewController` from `LocalUIViewController`.
- `ads/Ads.ios.kt` — `GADMobileAds.sharedInstance().startWithCompletionHandler(null)`. Called from `iOSApp.swift` after Koin init.

### `androidApp/src/main`
- `BeansApplication.kt` — `initKoin { androidLogger(); androidContext(this) }`.
- `MainActivity.kt` — `setContent { BeansApp() }` only. No DI plumbing — Koin handles it.

### `iosApp/`
- `iosApp/iOSApp.swift` — `@main` entry. Calls `InitKoinIosKt.doInitKoinIos()` once, wraps `ContentView()`.
- `iosApp/ContentView.swift` — `UIViewControllerRepresentable` wrapping `MainViewControllerKt.MainViewController()`.
- `project.yml` — xcodegen source of truth. **Do not commit `iosApp.xcodeproj`** — it's regenerated.
- `Podfile` — pulls in `ComposeApp` framework via `:composeApp/`.

## Tech Stack

| Concern | Pick | Notes |
|---|---|---|
| Kotlin | 2.1.0 (K2) | Compose compiler is now a separate plugin (`org.jetbrains.kotlin.plugin.compose`) |
| AGP | 8.9.1 | Required by CameraX 1.6.x — bumped from 8.7.3 |
| Gradle | 8.11.1 | Minimum for AGP 8.9.x — bumped from 8.10.2 |
| Android compileSdk | 36 | Required by CameraX 1.6.x — bumped from 35. `targetSdk` stays at 35. |
| Compose Multiplatform | 1.7.3 | |
| Navigation | `org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10` | KMP variant — alpha but stable enough for current scope |
| Lifecycle/ViewModel | `org.jetbrains.androidx.lifecycle:*:2.8.4` | KMP variant; `collectAsStateWithLifecycle` works in commonMain |
| DataStore | `androidx.datastore:datastore-preferences-core:1.1.1` | KMP-capable; `createWithPath` takes okio.Path |
| DI | Koin 4.0.0 | `koin-compose-viewmodel` for `koinViewModel<T>()` in Compose |
| material-kolor | 2.0.0 | KMP |
| OCR (Android) | ML Kit text-recognition 16.0.1 | androidMain only |
| OCR (iOS) | Vision framework `VNRecognizeTextRequest` | iosMain only |
| Camera (Android) | CameraX 1.6.1 | androidMain only |
| Camera (iOS) | AVFoundation | iosMain only |
| Permissions (Android) | Accompanist 0.36.0 | androidMain only |
| Permissions (iOS) | `AVCaptureDevice.requestAccessForMediaType` | iosMain only |
| UUID | `kotlin.uuid.Uuid` (experimental) | commonMain — opt-in `@OptIn(ExperimentalUuidApi::class)` |

## Project-Specific Tech Decisions

- **DI**: Koin (not Hilt — Hilt is Android-only and breaks KMP; not kotlin-inject-anvil — Koin is simpler for this scope).
- **No hand-rolled ViewModelFactory** — `koinViewModel<T>()` from `koin-compose-viewmodel` resolves the factory binding declared in `sharedModule`. Old `BeansViewModelFactory` is removed.
- **DataStore stored as okio.Path** in commonMain; per-platform path resolution (Android `filesDir`, iOS `NSDocumentDirectory`) lives in the platform Koin module — not in expect/actual.
- **Theme `System` mode** — Android does Material You via dynamic{Light,Dark}ColorScheme; iOS has no equivalent so it falls back to the default seed via material-kolor. Encoded as a single expect Composable returning `ColorScheme?`.
- **Currency formatting** — `Double.toFixed2()` / `toMoney()` in `shared/.../util/Money.kt`. **Do not** reach for `BigDecimal` / `String.format` / `java.util.Locale` — JVM-only, won't compile on iOS.
- **IDs** — `kotlin.uuid.Uuid.random().toString()`. **Do not** use `java.util.UUID`.

## DataStore Keys (all in `data/PreferencesKeys.kt`)

| Key | Type | Default | Purpose |
|-----|------|---------|---------|
| `TIP_PERCENT` | Int | 15 | Current tip percentage |
| `LOCK` | Boolean | false | Lock tip slider |
| `SAVED_NAMES` | String | `""` | Newline-delimited recurring names |
| `ACCENT_COLOR` | Long (ARGB) | `0xFF6650A4` | Material3 seed color. Long (not Int) so unsigned ARGB doesn't sign-extend negative. |
| `USE_SYSTEM_THEME` | Boolean | false | When true, ignore `ACCENT_COLOR` and use platform System theme |
| `TIP_MIN` | Int | 10 | Minimum tip % on slider |
| `TIP_MAX` | Int | 20 | Maximum tip % on slider |

## Screens (bottom nav)

Five-slot bottom bar: four tabs around a centered `+` FAB.

| Slot | Status | Purpose |
|--------|--------|---------|
| Calculator | shipped | Tip calc, lock toggle, tip % slider |
| People | placeholder | Empty state — who-owes-whom aggregation pending receipt persistence |
| `+` FAB | shipped | Launches the add-receipt wizard (`wizardGraph`) |
| Receipts | placeholder | Empty state — saved-receipts list pending receipt persistence |
| Settings | shipped | Accent seed picker, configurable min/max tip range |

The add-receipt wizard (`+`) walks: pick People → choose Source (scan/manual) → enter Items & prices → Tip (amount + Even/Proportional rule) → Tax (amount + rule) → Assign items to people → Totals. The old all-in-one `SplitterScreen` is gone. Receipts are **not persisted** — "Done" on the Totals step resets the wizard and lands on the Receipts tab; saving is deferred.

## KMP Gotchas Worth Knowing

- **Swift sees `init*` Kotlin functions with a `do` prefix.** `fun initKoinIos()` is `InitKoinIosKt.doInitKoinIos()` in Swift — `init` collides with Swift initializers, so K/N renames it. Same applies to anything else named `initX`.
- **`composeApp` is what iOS links, not `shared`.** `composeApp/build.gradle.kts` declares `framework { export(project(":shared")) }` so Swift sees both modules' types through one framework. Don't add a `binaries.framework` block to `:shared` — that produces a second framework Cocoapods won't link.
- **No `composeResources` strings yet.** `CalculatorScreen` inlines "TIP" / "TOTAL" rather than using `Res.string.*`. If localization is added, switch to `compose.components.resources` properly (artifact is already in `commonMain.dependencies`).
- **`menuAnchor()` deprecation.** `ExposedDropdownMenuBox` in Material3 1.3+ deprecates the no-arg `menuAnchor()` in favor of `menuAnchor(MenuAnchorType.PrimaryNotEditable)`. Compiles with a warning; update if quietness matters.
- **DataStore + okio.** `createDataStore { producePath() }` takes a `String` path (resolved to `okio.Path` internally). Don't try to pass `java.io.File` — that's JVM-only.

## Building from Windows

The user's primary dev machine is Windows. Android builds run there. **iOS builds require macOS** (Kotlin/Native iOS targets only compile on macOS). For iOS:
- Use a Mac (or Codemagic / GitHub Actions macOS runner).
- The Kotlin/iOS code compiles fine when invoked from a macOS host — it's just the `linkRelease*Framework*Ios*` tasks that need macOS.
- Don't expect `./gradlew :composeApp:linkDebugFrameworkIosArm64` to work on Windows.

## Monetization

### Ads (shipped — test IDs)
AdMob banner pinned above the bottom nav bar, persistent across Calculator/Splitter/Settings, hidden on the camera screen along with the nav bar. Implementation in `composeApp/.../ads/` via expect/actual: Google Mobile Ads SDK on Android, `Google-Mobile-Ads-SDK` Cocoapod on iOS.

**Currently using Google's TEST IDs** — they always serve test ads. Swap before release:
- Android app ID: `androidApp/src/main/AndroidManifest.xml` — `com.google.android.gms.ads.APPLICATION_ID` meta-data
- Android banner unit ID: `composeApp/.../ads/Ads.kt` — `AdUnits.ANDROID_BANNER_TEST`
- iOS app ID: `iosApp/iosApp/Info.plist` — `GADApplicationIdentifier`
- iOS banner unit ID: `composeApp/.../ads/Ads.kt` — `AdUnits.IOS_BANNER_TEST`

### Pre-release compliance work (not done yet)
- **UMP consent flow** — required for EEA/UK/CH users before serving personalized ads. Wire `UserMessagingPlatform` (Android) and `UMP` (iOS) before `startAds()`.
- **App Tracking Transparency** — iOS 14.5+ prompt via `ATTrackingManager` for personalized ads. Without it, iOS serves only non-personalized ads.
- **SKAdNetworkItems** — `Info.plist` has only Google's primary identifier; if AdMob mediation is enabled, paste the full ~75-entry list from [Google's docs](https://developers.google.com/admob/ios/quick-start#update_your_infoplist).
- **Play Data Safety form** + **Apple App Privacy Nutrition Label** — declare AdMob's data collection in both stores.
- **`app-ads.txt`** on the marketing domain.

### Pro IAP (not implemented)
One-time non-consumable purchase that gates the ad and unlocks the full accent palette.
- Play Billing Library on Android, StoreKit 2 on iOS.
- Pricing: $1.99.
- `HAS_PRO` boolean key in DataStore caches entitlement. Verify against Play Billing / StoreKit on app start — don't trust the cached flag alone.
- When `HAS_PRO == true`, return early from `AdBanner` (or skip rendering it in `BeansApp`).
