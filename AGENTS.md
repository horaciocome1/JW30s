# AGENTS.md - JW30s

## Project Overview

Kotlin Multiplatform (KMP) + Compose Multiplatform app targeting Android and iOS.
A "30 Seconds" board game themed around Bible and Jehovah's Witnesses topics.
Package: `me.horaciocome.jw30s`

## Tech Stack

- **Language:** Kotlin (shared code), Swift (iOS entry point)
- **UI:** Compose Multiplatform (Material 3)
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
./gradlew :composeApp:testDebugUnitTest --tests "me.horaciocome.jw30s.ComposeAppCommonTest"

# Run a SINGLE test method
./gradlew :composeApp:testDebugUnitTest --tests "me.horaciocome.jw30s.ComposeAppCommonTest.example"

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
    src/commonMain/kotlin/me/horaciocome/jw30s/ # Shared code
    src/commonTest/kotlin/me/horaciocome/jw30s/ # Shared tests
    src/androidMain/                            # Android-specific code
    src/iosMain/                                # iOS-specific code
  iosApp/                                       # iOS native app shell (Swift/Xcode)
  cards/                                        # Card images
```

## Code Style Guidelines

### Naming Conventions

| Element                  | Convention    | Example                          |
|--------------------------|---------------|----------------------------------|
| Classes / Interfaces     | PascalCase    | `AndroidPlatform`, `Greeting`    |
| Composable functions     | PascalCase    | `App()`, `GameScreen()`          |
| Regular functions        | camelCase     | `greet()`, `getPlatform()`       |
| Variables / Properties   | camelCase     | `showContent`, `platform`        |
| Constants                | SCREAMING_SNAKE | `MAX_TIMER_SECONDS`            |
| Packages                 | lowercase     | `me.horaciocome.jw30s`          |
| Acronyms in names        | ALL CAPS      | `IOSPlatform` (not `IosPlatform`)|

### File Naming

- One class/interface per file; filename matches the class name: `Greeting.kt`
- Platform-specific `actual` declarations: `Platform.android.kt`, `Platform.ios.kt`
- Common `expect` declarations: `Platform.kt`
- Composable entry points named after the function: `App.kt`

### Imports

- Use specific/named imports (not wildcards), except `androidx.compose.runtime.*` is acceptable
- Order: Kotlin stdlib -> AndroidX/Compose -> third-party -> generated resources
- Separate logically different groups with a blank line

### Types and Declarations

- Use `interface` for platform abstractions with `expect`/`actual` pattern
- Explicit return types on public functions: `fun greet(): String`
- Composable functions may omit `Unit` return type (idiomatic)
- Use expression body for single-expression functions: `actual fun getPlatform(): Platform = AndroidPlatform()`
- Use block body for multi-statement functions

### Compose Patterns

- Use Material 3 (`MaterialTheme` from `compose.material3`)
- State: `var x by remember { mutableStateOf(initialValue) }`
- Modifier chaining: one modifier per line, indented
- Use trailing commas in parameter lists
- Annotate with `@Composable`; add `@Preview` for previewable composables

### Error Handling

- Use Kotlin `Result` type or sealed classes for domain-level errors
- Use `try/catch` for platform-specific I/O operations
- Propagate errors through `StateFlow` to the UI layer

### Visibility

- Default is `public` (Kotlin convention - no explicit `public` keyword)
- Use `private` for implementation details
- Use `internal` for module-scoped APIs

### Gradle / Dependencies

- ALL dependency versions go in `gradle/libs.versions.toml` (version catalog)
- Reference dependencies as `libs.xxx` in build scripts - never hardcode versions
- Plugins are declared in root `build.gradle.kts` with `apply false`, then applied in modules

## Architecture (Planned)

Clean architecture with three layers:
- **Presentation:** Composables + ViewModels (Compose state management)
- **Domain:** Use cases and business logic
- **Data:** Repositories + Room database for card management

## KMP Expect/Actual Pattern

Use `expect`/`actual` for platform-specific code. Place shared code in `commonMain`;
only use `androidMain`/`iosMain` when platform APIs are required.

```kotlin
// commonMain - Platform.kt
expect fun getPlatform(): Platform

// androidMain - Platform.android.kt
actual fun getPlatform(): Platform = AndroidPlatform()

// iosMain - Platform.ios.kt
actual fun getPlatform(): Platform = IOSPlatform()
```
