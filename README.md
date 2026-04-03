# JW 30s

A Kotlin Multiplatform (KMP) + Compose Multiplatform app for Android and iOS. It's a "30 Seconds" board game themed around Bible and Jehovah's Witnesses topics.

## About the Game

**30 Seconds** is a fast-paced team party game. One player describes words on a card to their teammates within a time limit, without saying the actual words. Teams take turns, and the game rotates through all teams automatically.

This version focuses on Bible and Jehovah's Witnesses topics. Cards are organized into three categories:
- **Generic** (59 cards) - General Bible and JW knowledge
- **Miracles** (10 cards) - Biblical miracles
- **Teachings** (10 cards) - Biblical teachings

All 79 playable cards are in Portuguese. The app UI supports both Portuguese and English.

## Features

- **Team rotation** - Supports 2-8 teams with automatic turn rotation
- **Configurable round timer** - 15 to 120 seconds in 15-second increments (default: 30s)
- **Per-round timer** - Timer resets each round; not a session-wide countdown
- **Card management** - Room database tracks shown cards so they don't repeat until all have been used
- **Swipe to review** - After a round ends, swipe down through previously shown cards
- **Shuffle to advance** - New cards are only shown via the shuffle button (no swiping forward)
- **Round controls** - Timer must expire (or be skipped) before shuffling to the next card/team
- **Skip round** - Tap the timer during an active round to end it early
- **Haptic and audio feedback** - Device vibrates and plays a sound when time runs out
- **Game setup** - Bottom sheet on home screen to configure teams and round duration
- **Settings** - Persistent preferences for language, default teams, and default round duration
- **i18n** - Full Portuguese and English UI localization via Compose Multiplatform resources
- **Language switching** - Change language in settings; takes effect on Android via Activity recreation, on iOS via system locale
- **Exit confirmation** - Back button disabled during game; exit requires confirmation dialog
- **Blur background** - Instagram Stories-style blurred card background behind the active card

## Tech Stack

| Component | Technology | Version |
|---|---|---|
| Language | Kotlin / Swift (iOS entry) | 2.3.20 |
| UI | Compose Multiplatform (Material 3) | 1.10.3 |
| DI | Koin | 4.0.4 |
| Database | Room KMP | 2.7.1 |
| Persistence | DataStore Preferences | 1.1.7 |
| Navigation | Compose Navigation (JetBrains KMP) | 2.9.2 |
| Build | Gradle (Kotlin DSL) | 8.14.3 |
| Android | compileSdk 36, minSdk 29, targetSdk 36 | - |

## Project Structure

```
JW30s/
  composeApp/
    src/
      commonMain/
        kotlin/me/horacioco/jw30s/
          App.kt                          # Root composable, NavHost
          Feedback.kt                     # GameFeedback interface
          Platform.kt                     # expect platform declarations
          data/
            AppDatabase.kt                # Room database
            Card.kt                       # Room entity
            CardDao.kt                    # Room DAO
            CardRepository.kt             # Card business logic
            LanguagePersistence.kt        # Platform language save interface
            SettingsRepository.kt         # DataStore-backed settings
          di/
            AppModule.kt                  # Koin shared module
            PlatformModule.kt             # expect platform DI module
          domain/
            CardResourceMapper.kt         # Card ID -> DrawableResource mapping
          navigation/
            Routes.kt                     # Type-safe navigation routes
          presentation/
            GameScreen.kt                 # Game screen (pager, timer, teams)
            GameSetupBottomSheet.kt       # Pre-game setup (teams, duration)
            GameViewModel.kt              # Game state management
            HomeScreen.kt                 # Home screen
            SettingsBottomSheet.kt        # App settings
        composeResources/
          drawable/                        # 82 card PNGs (79 + 3 backs)
          values/strings.xml               # English strings
          values-pt/strings.xml            # Portuguese strings
      androidMain/
        kotlin/me/horacioco/jw30s/
          MainActivity.kt                 # Activity with locale override
          JW30sApplication.kt             # Application subclass (Koin init)
          Feedback.android.kt             # Android vibration + sound
          Platform.android.kt             # Android platform impl
          di/PlatformModule.android.kt    # Android Koin module
      iosMain/
        kotlin/me/horacioco/jw30s/
          MainViewController.kt           # iOS entry point (Koin init)
          Feedback.ios.kt                 # iOS haptics + sound
          Platform.ios.kt                 # iOS platform impl
          di/PlatformModule.ios.kt        # iOS Koin module
  iosApp/                                  # Xcode project shell
  gradle/libs.versions.toml               # Version catalog
```

## Architecture

Clean architecture with three layers, all in the shared `commonMain` source set:

- **Presentation** - Compose screens, bottom sheets, and `GameViewModel` with `StateFlow`-based state management
- **Domain** - `CardResourceMapper` mapping card IDs to drawable resources
- **Data** - `CardRepository` (Room database), `SettingsRepository` (DataStore), `LanguagePersistence` (platform interface)

Platform-specific code is minimal:
- **Android** (`androidMain`) - `MainActivity` (locale override), `JW30sApplication` (Koin bootstrap), `AndroidGameFeedback`, Koin platform module (Room, DataStore, SharedPreferences)
- **iOS** (`iosMain`) - `MainViewController` (Koin bootstrap), `IOSGameFeedback`, Koin platform module (Room, DataStore)

Dependency injection uses Koin 4.0.4 with `koin-compose-viewmodel` for KMP ViewModel injection. The `GameViewModel` receives `numberOfTeams` and `roundDurationSeconds` as constructor parameters via Koin's `parametersOf`.

## Build

```shell
# Android debug APK
./gradlew :composeApp:assembleDebug

# Android release APK
./gradlew :composeApp:assembleRelease

# iOS framework (simulator)
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Full build (all targets)
./gradlew build

# Clean
./gradlew clean
```

## Tests

```shell
# All tests (all platforms)
./gradlew :composeApp:allTests

# Android unit tests
./gradlew :composeApp:testDebugUnitTest

# Single test class
./gradlew :composeApp:testDebugUnitTest --tests "me.horacioco.jw30s.SomeTest"

# Verbose output
./gradlew :composeApp:allTests --info
```

Tests use `kotlin.test` (`@Test`, `assertEquals`, etc.) and live in `composeApp/src/commonTest/kotlin/`.

## Game Flow

1. **Home screen** - Tap "Start Game" to open the setup sheet, or the gear icon for settings
2. **Game setup** - Choose number of teams (2-8) and round duration (15-120s), then start
3. **Waiting state** - Shows "Team N - Get Ready!" with a "Start Round" button
4. **Active round** - Timer counts down, card is displayed, swiping and shuffling are locked
5. **Round over** - Timer hits 0 (or player skips), device vibrates and plays sound. Player can now swipe down to review previous cards
6. **Shuffle** - Tap the shuffle button to load the next card and rotate to the next team
7. **Repeat** from step 3 for the next team

## Known Build Warnings

These warnings are expected and benign:
- `expect/actual classes are in Beta` - Room's `AppDatabaseConstructor` uses expect/actual
- `Locale(String) is deprecated` - Android locale constructor in `MainActivity.attachBaseContext`
- Koin `SavedStateHandle` backing field warning on iOS native linking
- `Cannot infer bundle ID` info message on iOS framework linking
