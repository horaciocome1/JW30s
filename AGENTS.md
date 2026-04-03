# AGENTS.md - JW30s

## Project Overview

Kotlin Multiplatform (KMP) + Compose Multiplatform app targeting Android and iOS.
A "30 Seconds" board game themed around Bible and Jehovah's Witnesses topics.
Package: `me.horacioco.jw30s`

## Tech Stack

- **Language:** Kotlin (shared code), Swift (iOS entry point)
- **UI:** Compose Multiplatform (Material 3)
- **DI:** Koin 4.0.4 (`koin-compose-viewmodel` for KMP ViewModel injection)
- **Database:** Room KMP 2.7.1 (with KSP code generation)
- **Persistence:** DataStore Preferences 1.1.7
- **Navigation:** Compose Navigation (JetBrains KMP fork) 2.9.2
- **Build system:** Gradle 8.14.3 (Kotlin DSL)
- **Kotlin:** 2.3.20
- **Compose Multiplatform:** 1.10.3
- **Android:** compileSdk 36, minSdk 29, targetSdk 36
- **JVM target:** 11
- **Dependency management:** Gradle Version Catalog (`gradle/libs.versions.toml`)

## Build Commands

```shell
./gradlew :composeApp:assembleDebug                      # Android debug APK
./gradlew :composeApp:assembleRelease                    # Android release APK
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64 # iOS framework
./gradlew clean                                          # Clean build
./gradlew build                                          # Full build (all targets)
```

## Test Commands

```shell
# Run ALL tests (all platforms)
./gradlew :composeApp:allTests

# Run common tests only (fastest, recommended during development)
./gradlew :composeApp:testDebugUnitTest # Android unit tests

# Run a SINGLE test class
./gradlew :composeApp:testDebugUnitTest --tests "me.horacioco.jw30s.ComposeAppCommonTest"

# Run a SINGLE test method
./gradlew :composeApp:testDebugUnitTest --tests "me.horacioco.jw30s.ComposeAppCommonTest.example"

# Run tests with verbose output
./gradlew :composeApp:allTests --info
```

Test files live in `composeApp/src/commonTest/kotlin/` (shared tests) and can also be
placed in `androidTest/` or `iosTest/` source sets for platform-specific tests.
Tests use `kotlin.test` (`@Test`, `assertEquals`, etc.) - not JUnit directly.

## Lint / Static Analysis

No dedicated linter (detekt, ktlint) is configured yet. The project uses
`kotlin.code.style=official` in `gradle.properties` for IDE formatting.

## Project Structure

```
JW30s/
  build.gradle.kts                              # Root build script
  settings.gradle.kts                           # Project name, module includes
  gradle/libs.versions.toml                     # Version catalog (all versions)
  composeApp/                                   # Main shared module
    build.gradle.kts                            # Module build config (all KMP deps)
    src/
      commonMain/
        kotlin/me/horacioco/jw30s/
          App.kt                                # Root composable, NavHost, Koin DI
          Feedback.kt                           # GameFeedback interface
          Platform.kt                           # expect platform declarations
          data/
            AppDatabase.kt                      # Room database + DATABASE_NAME const
            Card.kt                             # Room entity
            CardDao.kt                          # Room DAO
            CardRepository.kt                   # Card lifecycle (init, getNext, markShown)
            LanguagePersistence.kt              # Platform language save interface
            SettingsRepository.kt               # DataStore-backed settings
          di/
            AppModule.kt                        # Koin shared module (DAO, repos, VM)
            PlatformModule.kt                   # expect val platformModule: Module
          domain/
            CardResourceMapper.kt               # Card ID -> DrawableResource mapping
          navigation/
            Routes.kt                           # Type-safe routes (Home, Game)
          presentation/
            GameScreen.kt                       # Game UI (Scaffold, pager, timer, teams)
            GameSetupBottomSheet.kt             # Pre-game setup sheet
            GameViewModel.kt                    # Game state (per-round timer, teams)
            HomeScreen.kt                       # Home screen
            SettingsBottomSheet.kt              # Settings sheet (language, defaults)
        composeResources/
          drawable/                             # 82 PNGs (79 cards + 3 category backs)
          values/strings.xml                    # English strings
          values-pt/strings.xml                 # Portuguese strings
      commonTest/kotlin/                        # Shared tests (kotlin.test)
      androidMain/
        kotlin/me/horacioco/jw30s/
          MainActivity.kt                       # Activity with locale override
          JW30sApplication.kt                   # Application subclass (startKoin)
          Feedback.android.kt                   # AndroidGameFeedback (vibration + sound)
          Platform.android.kt                   # actual platform impl
          di/PlatformModule.android.kt          # Android Koin (Room, DataStore, Feedback, LanguagePersistence)
        AndroidManifest.xml                     # Manifest (JW30sApplication, VIBRATE permission)
        res/                                    # Android resources (icons, launcher)
      iosMain/
        kotlin/me/horacioco/jw30s/
          MainViewController.kt                 # iOS entry point (ComposeUIViewController + startKoin)
          Feedback.ios.kt                       # IOSGameFeedback (haptics + sound)
          Platform.ios.kt                       # actual platform impl
          di/PlatformModule.ios.kt              # iOS Koin (Room, DataStore, Feedback, LanguagePersistence)
  iosApp/                                       # Xcode project shell (Swift)
```

## Code Style Guidelines

### Naming Conventions

| Element                  | Convention    | Example                               |
|--------------------------|---------------|---------------------------------------|
| Classes / Interfaces     | PascalCase    | `AndroidGameFeedback`, `CardRepository` |
| Composable functions     | PascalCase    | `App()`, `GameScreen()`, `HomeScreen()` |
| Regular functions        | camelCase     | `startRound()`, `shuffleNextCard()`   |
| Variables / Properties   | camelCase     | `showContent`, `timerSeconds`         |
| Constants                | SCREAMING_SNAKE | `DATABASE_NAME`, `DEFAULT_LANGUAGE`  |
| Packages                 | lowercase     | `me.horacioco.jw30s`               |
| Acronyms in names        | ALL CAPS      | `IOSPlatform` (not `IosPlatform`)     |

### File Naming

- One class/interface per file; filename matches the class name: `CardRepository.kt`
- Platform-specific implementations: `Feedback.android.kt`, `Feedback.ios.kt`
- Platform DI modules: `PlatformModule.android.kt`, `PlatformModule.ios.kt`
- Common `expect` declarations: `PlatformModule.kt` (expect val)
- Composable entry points named after the function: `App.kt`, `GameScreen.kt`

### Imports

- Use specific/named imports (not wildcards), except `androidx.compose.runtime.*` is acceptable
- Order: Kotlin stdlib -> AndroidX/Compose -> generated resources -> third-party -> project
- Generated resource imports use the pattern: `jw30s.composeapp.generated.resources.Res`
- Individual string resources: `jw30s.composeapp.generated.resources.app_title`

### Types and Declarations

- Use `interface` for platform abstractions: `GameFeedback`, `LanguagePersistence`
- Use `expect`/`actual` for DI modules: `expect val platformModule: Module`
- Explicit return types on public functions: `fun greet(): String`
- Composable functions may omit `Unit` return type (idiomatic)
- Use expression body for single-expression functions
- Use block body for multi-statement functions
- Data classes for UI state: `data class GameUiState(...)`
- Data classes for navigation routes: `data class Game(val numberOfTeams: Int, ...)`

### Compose Patterns

- Use Material 3 (`MaterialTheme` from `compose.material3`)
- State: `var x by remember { mutableStateOf(initialValue) }`
- Use `key()` wrapper when `remember` blocks depend on changing external values
- Modifier chaining: one modifier per line, indented
- Use trailing commas in parameter lists
- Annotate with `@Composable`; add `@Preview` for previewable composables
- String resources via `stringResource(Res.string.xxx)` or `stringResource(Res.string.xxx, arg)` for formatted strings
- Content descriptions use string resources for accessibility
- `@Composable` lambdas when `stringResource` is needed inside lambda parameters

### Error Handling

- Use Kotlin `Result` type or sealed classes for domain-level errors
- Use `try/catch` for platform-specific I/O operations
- Propagate errors through `StateFlow` to the UI layer

### Visibility

- Default is `public` (Kotlin convention - no explicit `public` keyword)
- Use `private` for implementation details (e.g., private composable helpers like `TeamIndicator`, `TimerDisplay`)
- Use `internal` for module-scoped APIs

### Gradle / Dependencies

- ALL dependency versions go in `gradle/libs.versions.toml` (version catalog)
- Reference dependencies as `libs.xxx` in build scripts - never hardcode versions
- Plugins are declared in root `build.gradle.kts` with `apply false`, then applied in modules
- KSP processors (Room) are added per-platform: `add("kspAndroid", ...)`, `add("kspIosArm64", ...)`

## Architecture

Clean architecture with three layers, all in shared `commonMain`:

- **Presentation:** Compose screens + `GameViewModel` with `StateFlow<GameUiState>`
  - `GameScreen` uses `Scaffold` + `TopAppBar`, `VerticalPager`, blur background
  - `HomeScreen` triggers `GameSetupBottomSheet` and `SettingsBottomSheet`
  - `GameViewModel` manages per-round timer, team rotation, card loading
- **Domain:** `CardResourceMapper` maps card IDs to `DrawableResource`
- **Data:**
  - `CardRepository` + `CardDao` + Room `AppDatabase` for card lifecycle
  - `SettingsRepository` + DataStore Preferences for persistent settings
  - `LanguagePersistence` interface for platform-specific language write-through

### Dependency Injection (Koin)

- `AppModule` - shared: CardDao, CardRepository, SettingsRepository, GameViewModel (parametersOf)
- `PlatformModule` (expect/actual) - platform-specific: AppDatabase, GameFeedback, DataStore, LanguagePersistence
- Android: `startKoin` in `JW30sApplication.onCreate()` (not Activity - survives config changes)
- iOS: `startKoin` in `MainViewController` configure block

### Navigation

Type-safe navigation with `kotlinx.serialization`:
- `Home` - singleton object route
- `Game(numberOfTeams: Int, roundDurationSeconds: Int)` - data class route with parameters

### i18n

- String resources in `composeResources/values/strings.xml` (EN) and `composeResources/values-pt/strings.xml` (PT)
- All UI strings use `stringResource(Res.string.xxx)` - no hardcoded strings
- Format args use `%1$s` pattern: `stringResource(Res.string.team_get_ready, teamNumber.toString())`
- Language switching on Android: `MainActivity.attachBaseContext()` reads SharedPreferences, overrides locale, calls `recreate()`
- Language switching on iOS: follows system locale (no-op `LanguagePersistence`)

### Platform Abstractions

| Abstraction | Common | Android | iOS |
|---|---|---|---|
| `GameFeedback` | Interface | `AndroidGameFeedback` (Vibrator + MediaPlayer) | `IOSGameFeedback` (UIImpactFeedbackGenerator) |
| `LanguagePersistence` | Interface | SharedPreferences write-through | No-op (system locale) |
| `PlatformModule` | expect val | Room + DataStore + Feedback + LanguagePersistence | Room + DataStore + Feedback + LanguagePersistence |

## Game State Machine

```
isWaitingToStart  -->  startRound()  -->  isRoundActive  -->  timer=0 or skipRound()  -->  isRoundOver
      ^                                                                                        |
      |                                                                                        v
      +----------------------------  shuffleNextCard() (rotates team)  <------------------------+
```

- **isWaitingToStart** - Shows "Start Round" button, swiping disabled
- **isRoundActive** - Timer counting down, swiping and shuffling disabled, timer clickable to skip
- **isRoundOver** - Timer at 0, swipe down to review, shuffle button enabled, shows next team

## Known Build Warnings (Benign)

- `expect/actual classes are in Beta` - Room's `AppDatabaseConstructor`
- `Locale(String) is deprecated` - Android locale constructor in `MainActivity`
- Koin `SavedStateHandle` backing field info on iOS native linking
- `Cannot infer bundle ID` info message on iOS framework linking
- Swift LSP errors in `iosApp/` are pre-existing Xcode toolchain issues

## KMP Patterns Used

### expect/actual for DI modules
```kotlin
// commonMain - PlatformModule.kt
expect val platformModule: Module

// androidMain - PlatformModule.android.kt
actual val platformModule: Module = module { ... }

// iosMain - PlatformModule.ios.kt
actual val platformModule: Module = module { ... }
```

### Interface for platform abstractions (preferred over expect/actual for classes)
```kotlin
// commonMain - Feedback.kt
interface GameFeedback {
    fun vibrate()
    fun playTimerEndSound()
}

// androidMain - Feedback.android.kt
class AndroidGameFeedback(private val context: Context) : GameFeedback { ... }

// iosMain - Feedback.ios.kt
class IOSGameFeedback : GameFeedback { ... }
```

### Compose Multiplatform string resources
```kotlin
import jw30s.composeapp.generated.resources.Res
import jw30s.composeapp.generated.resources.team_get_ready
import org.jetbrains.compose.resources.stringResource

Text(text = stringResource(Res.string.team_get_ready, teamNumber.toString()))
```

## Important Notes

- `String.format()` is NOT available in Kotlin/Native - use `padStart()` or string templates
- Card images are in `composeResources/drawable/`, loaded via `painterResource(DrawableResource)`
- 79 playable cards + 3 back images = 82 PNGs total (generic: 59+1, miracle: 10+1, teaching: 10+1)
- Timer display uses `padStart(2, '0')` instead of `String.format("%02d", ...)` for KMP compatibility
- Koin 4.0.4 uses `viewModel { }` DSL (not `viewModelOf()`) with `parametersOf` for constructor args
- DataStore KMP uses `PreferenceDataStoreFactory.createWithPath()` with `okio.Path`
- `@OptIn(ExperimentalForeignApi::class)` required on iOS platform module for NSFileManager usage
