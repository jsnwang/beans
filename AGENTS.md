# AGENTS.md — Beans

Project instructions for AI coding agents (Cursor, Copilot, Aider, Codex, Claude Code, etc.). Vendor-neutral mirror of `CLAUDE.md` — if both files exist they should stay in sync.

This file covers Beans-specific decisions only. Cross-cutting Kotlin / KMP / Compose engineering principles are assumed; if your agent doesn't have them loaded elsewhere, treat the **Engineering Principles** section at the bottom as canonical.

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
- `viewmodel/*` — `CalculatorViewModel`, `SettingsViewModel`, `SplitterViewModel` + `XxxUiState` siblings. Use multiplatform `ViewModel` + `viewModelScope`.
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
- `BeansApp.kt` — root: pulls `ThemeRepository` via `koinInject()`, observes mode, wraps `BeansNav` in `BeansTheme`. NavHost wires the four destinations including hidden `SPLITTER_CAMERA_ROUTE`.
- `ui/Screen.kt` — sealed nav routes (`Calculator`, `Splitter`, `Settings`) + companion `SPLITTER_CAMERA_ROUTE`.
- `ui/CalculatorScreen.kt` — bill input, tip slider, lock toggle (Material `Icons.Filled.Lock` / `LockOpen` — no `R.drawable`).
- `ui/SettingsScreen.kt` — min/max tip stepper pair, accent color dropdown including "System".
- `ui/SplitterScreen.kt` — receipt OCR + manual entry + assignment + per-person totals.
- `ui/CameraCaptureScreen.kt` — `expect Composable` taking `SplitterViewModel + onDone`.
- `ui/TotalTextField.kt` — numeric input.
- `ui/theme/Theme.kt` — `BeansTheme(mode, useDarkTheme)`. `Seeded` always uses material-kolor; `System` calls `expect platformSystemColorScheme(useDarkTheme)`.
- `ui/theme/ThemeOption.kt` — sealed `System | Named(enum)` for the Settings dropdown.
- `ads/AdBanner.kt` — `expect Composable AdBanner(modifier)`. Slotted in `BeansApp`'s bottomBar above the `NavigationBar`, persistent across all main screens, hidden on the camera screen along with the nav bar.
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
| AGP | 8.7.3 | |
| Gradle | 8.10.2 | |
| Compose Multiplatform | 1.7.3 | |
| Navigation | `org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10` | KMP variant — alpha but stable enough for current scope |
| Lifecycle/ViewModel | `org.jetbrains.androidx.lifecycle:*:2.8.4` | KMP variant; `collectAsStateWithLifecycle` works in commonMain |
| DataStore | `androidx.datastore:datastore-preferences-core:1.1.1` | KMP-capable; `createWithPath` takes okio.Path |
| DI | Koin 4.0.0 | `koin-compose-viewmodel` for `koinViewModel<T>()` in Compose |
| material-kolor | 2.0.0 | KMP |
| OCR (Android) | ML Kit text-recognition 16.0.1 | androidMain only |
| OCR (iOS) | Vision framework `VNRecognizeTextRequest` | iosMain only |
| Camera (Android) | CameraX 1.3.4 | androidMain only |
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

| Screen | Status | Purpose |
|--------|--------|---------|
| Calculator | shipped | Tip calc, lock toggle, tip % slider |
| Splitter | shipped | Camera → OCR → assign line items → per-person totals; manual entry too |
| Settings | shipped | Accent seed picker, configurable min/max tip range |

## KMP Gotchas Worth Knowing

- **Swift sees `init*` Kotlin functions with a `do` prefix.** `fun initKoinIos()` is `InitKoinIosKt.doInitKoinIos()` in Swift — `init` collides with Swift initializers, so K/N renames it. Same applies to anything else named `initX`.
- **`composeApp` is what iOS links, not `shared`.** `composeApp/build.gradle.kts` declares `framework { export(project(":shared")) }` so Swift sees both modules' types through one framework. Don't add a `binaries.framework` block to `:shared` — that produces a second framework Cocoapods won't link.
- **No `composeResources` strings yet.** `CalculatorScreen` inlines "TIP" / "TOTAL" rather than using `Res.string.*`. If localization is added, switch to `compose.components.resources` properly (artifact is already in `commonMain.dependencies`).
- **`menuAnchor()` deprecation.** `ExposedDropdownMenuBox` in Material3 1.3+ deprecates the no-arg `menuAnchor()` in favor of `menuAnchor(MenuAnchorType.PrimaryNotEditable)`. Compiles with a warning; update if quietness matters.
- **DataStore + okio.** `createDataStore { producePath() }` takes a `String` path (resolved to `okio.Path` internally). Don't try to pass `java.io.File` — that's JVM-only.

## Building from Windows

The primary dev machine is Windows. Android builds run there. **iOS builds require macOS** (Kotlin/Native iOS targets only compile on macOS). For iOS:
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
- **SKAdNetworkItems** — `Info.plist` has only Google's primary identifier; if AdMob mediation is enabled, paste the full ~75-entry list from Google's docs.
- **Play Data Safety form** + **Apple App Privacy Nutrition Label** — declare AdMob's data collection in both stores.
- **`app-ads.txt`** on the marketing domain.

### Pro IAP (not implemented)
One-time non-consumable purchase that gates the ad and unlocks the full accent palette.
- Play Billing Library on Android, StoreKit 2 on iOS.
- Pricing: $1.99.
- `HAS_PRO` boolean key in DataStore caches entitlement. Verify against Play Billing / StoreKit on app start — don't trust the cached flag alone.
- When `HAS_PRO == true`, return early from `AdBanner` (or skip rendering it in `BeansApp`).

---

## Engineering Principles (assumed defaults)

If your agent has its own loaded conventions, defer to those. Otherwise treat the following as binding for this repo.

### SOLID
- **SRP** — one ViewModel per screen, one composable per visual concern.
- **OCP** — extend by composition. Use `sealed class`/`sealed interface` for closed sets so `when` is exhaustive at compile time.
- **LSP / ISP** — narrow interfaces. Prefer `(T) -> Unit` callbacks over multi-method listeners.
- **DIP** — depend on abstractions; constructor-inject everything; never reach for singletons or `Application`-scoped backdoors.

### State & Concurrency
- **Unidirectional data flow** — state down (`StateFlow` → composable), events up (callbacks → ViewModel).
- **Immutability** — `UiState` is `data class` of `val`s; mutate via `_uiState.update { it.copy(...) }`.
- **Single source of truth** — repository/DataStore is canonical for persisted state; UI reads derived state from VM, never directly.
- **Structured concurrency** — `viewModelScope` only. No `GlobalScope`. No `runBlocking` outside tests.
- **Lifecycle-aware collection** — `collectAsStateWithLifecycle()` works in `commonMain`; use it everywhere.
- **Never expose `MutableStateFlow`** — always `StateFlow` (read-only) at the boundary.

### Compose
- **Hoist state** — leaf composables take `value` + `onValueChange`. VM-aware composables are screen-level only.
- **No side effects in composition** — use `LaunchedEffect`, `DisposableEffect`, `rememberCoroutineScope`.
- **Stable parameters** — `@Stable`/`@Immutable` data classes and immutable collections so recomposition can skip.
- **Preview-friendly** — write screens so `@Preview` can call them with a hand-built `UiState`.

### Anti-patterns to avoid
- Business logic inside composables.
- `object`-held mutable state, service locators, `Application` backdoors.
- Bare `catch (e: Throwable)` that swallows.
- Reading prefs directly from composables — go through a ViewModel.
- MockK or any JVM-only library in `commonTest`.
- `java.util.*` types (`Date`, `File`, `UUID`, `BigDecimal`) in `commonMain` — won't compile for iOS.
- `Context` in `commonMain` — Android-only.
- Hardcoded versions in `build.gradle.kts` — must come from `gradle/libs.versions.toml`.

### Code Quality
- Composition over inheritance. Avoid `open class` unless modeling true is-a.
- DRY after the third repetition. Premature abstraction costs more than duplication.
- Pure functions for math/transformations; isolate side effects at the edges.
- `enum class`/`sealed class` over magic strings.
- Accessibility: every interactive element gets a `contentDescription` or semantic label; respect dynamic font sizes.

### Default to terse comments
- Don't explain WHAT the code does — well-named identifiers do that.
- Only comment WHY when non-obvious: hidden constraints, workarounds, surprising behavior.
- Don't reference the current task or PR in comments — those belong in the PR description.
