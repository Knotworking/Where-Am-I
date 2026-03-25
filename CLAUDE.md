# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What This App Does

WhereAmI is a geolocation guessing game built in Jetpack Compose. It shows geotagged photos from Flickr or a custom BenHikes API and challenges users to guess the location on a map. After 5 rounds, a final score is calculated based on distance accuracy (Haversine formula).

## Build & Test Commands

```bash
./gradlew build                  # Full build
./gradlew installDebug           # Build and install debug APK
./gradlew test                   # Unit tests (all modules)
./gradlew :domain:game:test      # Unit tests for a specific module
./gradlew connectedAndroidTest   # Instrumented tests (requires device/emulator)
./gradlew check                  # Lint + tests
./gradlew clean
```

To run tests for a single module: `./gradlew :<layer>:<module>:test` (e.g., `./gradlew :domain:photo:test`).

## Architecture

Clean Architecture + MVVM, strictly layered with unidirectional dependencies (outer → inner only):

```
app → feature:* → domain:* ← data:* ← core:network
                               ↑
                           core:ui (shared Compose deps/theme)
```

**Layers:**
- **`app`** — Entry point, Hilt setup (`@HiltAndroidApp`), Compose Navigation (`WhereAmINavHost`)
- **`feature/game`, `feature/settings`** — MVVM: ViewModel + `UiState` data class + Composable screens
- **`domain/game`, `domain/photo`** — Pure Kotlin (JVM only, no Android deps). Models, repository interfaces, use cases. Fully unit-testable.
- **`data/photo`** — `PhotoRepositoryImpl` coordinates `FlickrDataSource` and `BenHikesDataSource`. DataStore for persisting the selected `PhotoSource`.
- **`core/network`** — Hilt module providing shared OkHttpClient + Moshi
- **`core/ui`** — Material3 theme only

**Key patterns:**
- Hilt DI throughout; ViewModels injected with `@HiltViewModel`
- StateFlow-based reactive state from ViewModels to Composables
- Use cases are simple suspending wrappers (single responsibility)
- Moshi + KSP for JSON deserialization (DTOs live in `data:photo`)
- DataStore (not SharedPreferences) for settings persistence

## Key Libraries

| Purpose | Library |
|---|---|
| UI | Jetpack Compose + Material3 |
| Navigation | Navigation Compose |
| DI | Hilt 2.59.2 |
| Networking | Retrofit + OkHttp 4.10 |
| JSON | Moshi 1.15.2 (KSP codegen) |
| Image loading | Coil Compose |
| Maps | Maps Compose + Google Play Services |
| Settings | DataStore Preferences |
| Testing | JUnit4, Mockk, Coroutines Test |
| Code gen | KSP 2.3.5 |

## Module Dependency Rules

- `domain:*` modules are pure Kotlin — never add Android or Hilt dependencies there
- `feature:*` modules depend on `domain:*` but never on `data:*` directly
- New shared UI components go in `core:ui`; new shared network setup goes in `core:network`
- API keys and local config belong in `local.properties` (not committed)

## Conventions
- Kotlin only, no Java
- Coroutines + Flow, never RxJava
- State exposed as StateFlow<UiState>
- Write tests in the same module under src/test
- Run `./gradlew detekt` before committing

## Testing Conventions
- Use Mockk for mocking (never Mockito)
- ViewModel tests use `TestCoroutineDispatcher` / `UnconfinedTestDispatcher`
- Use `turbine` for Flow/StateFlow assertions if available, otherwise `toList()`
- Domain layer tests should have zero Android dependencies — pure JUnit4
- Aim for: one test class per use case, one per ViewModel

## Before Writing Code
1. Check existing modules — don't create new ones without confirming
2. Run `./gradlew :relevant:module:test` to confirm baseline passes
3. If the task touches the map or scoring logic, read domain:game carefully first

## Dependencies
- All dependencies are managed via `libs.versions.toml` (version catalog)
- Never hardcode version strings in build.gradle.kts files
- To add a dependency: add to libs.versions.toml first, then reference via `libs.*` alias

## What NOT to do
- Don't add dependencies without checking existing ones first
- Don't modify :core:ui components without flagging it
- Don't use LiveData

## Local Config
`local.properties` contains (never commit these):
- `FLICKR_API_KEY` — accessed via BuildConfig.FLICKR_API_KEY
- `BENHIKES_BASE_URL` — base URL for the custom API