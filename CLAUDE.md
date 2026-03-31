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
- **`core/domain`** — Shared domain logic and models used across layers and features.
- **`core/network`** — Hilt module providing shared OkHttpClient + Moshi. Shared networking helpers.
- **`core/ui`** — Material3 theme only

**Key patterns:**
- Hilt DI throughout; ViewModels injected with `@HiltViewModel`
- StateFlow-based reactive state from ViewModels to Composables
- Use cases are simple suspending wrappers (single responsibility)
- Moshi + KSP for JSON deserialization (DTOs live in `data:feature` modules)
- DataStore (not SharedPreferences) for settings persistence

## Module Dependency Rules

- `domain:*` modules are pure Kotlin — never add Android or Hilt dependencies there
- `feature:*` modules depend on `domain:*` but never on `data:*` directly
- New shared UI components go in `core:ui`; new shared network setup goes in `core:network`
- API keys and local config belong in `local.properties` (not committed)

## Conventions
- Kotlin only, no Java
- Coroutines + Flow, never RxJava
- State exposed as StateFlow<UiState>
- Run `./gradlew detekt` before committing

## Git Workflow
When working on a task:
- Commit after each logical unit of work (a passing test, a completed
  function, a working module)
- Commit messages: imperative mood, max 72 chars, reference what changed
  and why e.g. "Add error state to GameViewModel for network failures"
- Never commit broken code — run `./gradlew :relevant:module:test` first
- Never commit to main — always work on a feature branch
- Do not push unless explicitly asked
- Do not create a PR unless explicitly asked

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