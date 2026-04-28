# Where Am I?

A geography guessing game for Android: you're shown a geotagged photo and must drop a pin on the map where you think it was taken. Five rounds, Haversine scoring, leaderboard.

<img width="300" alt="Screenshot_20260428_144857" src="https://github.com/user-attachments/assets/b40b22d1-6c93-4658-bfc7-f0d1a328624f" />

<img width="300" alt="Screenshot_20260428_145013" src="https://github.com/user-attachments/assets/acc3a060-6267-4b77-8119-afccf6621d8e" />

---

## What it does

1. A geotagged photo is fetched from Flickr or the BenHikes API.
2. The player places a pin on a map to guess the location.
3. A score is calculated from the great-circle distance (Haversine formula) between the guess and the actual coordinates.
4. After 5 rounds the cumulative score is saved to the leaderboard.

---

## Architecture

Clean Architecture + MVI, strictly layered with unidirectional dependencies:

```mermaid
graph LR
   subgraph features["Feature Layer"]
      feature_game["feature:game"]
      feature_settings["feature:settings"]
   end

   subgraph domains["Domain Layer"]
      domain_game["domain:game"]
      domain_photo["domain:photo"]
   end

   subgraph data["Data Layer"]
      data_game["data:game"]
      data_photo["data:photo"]
   end

   subgraph core["Core"]
      core_ui["core:ui"]
      core_domain["core:domain"]
      core_network["core:network"]
   end

   feature_game --> domain_game
   feature_game --> domain_photo
   feature_game --> core_ui
   feature_game --> core_domain
   feature_settings --> domain_photo
   feature_settings --> core_ui

   domain_game --> domain_photo
   domain_photo --> core_domain

   data_game --> domain_game
   data_photo --> domain_photo
   data_photo --> core_network
   data_photo --> core_domain

   core_network --> core_domain
```

---

## Module map

| Module | Role |
|---|---|
| `:app` | Entry point, Hilt setup, Compose Navigation host |
| `:feature:game` | Game screen, leaderboard screen, ViewModels |
| `:feature:settings` | Photo source toggle (Flickr / BenHikes) |
| `:domain:game` | Game models, scoring use cases, high score repository interface |
| `:domain:photo` | Photo model, `GetRandomGeotaggedPhotoUseCase`, repository interface |
| `:data:photo` | `PhotoRepositoryImpl`, Flickr + BenHikes data sources, DataStore |
| `:data:game` | `HighScoreRepositoryImpl`, Room database + DAO |
| `:core:network` | Hilt module: shared OkHttpClient, Moshi |
| `:core:ui` | Material 3 theme only |
| `:core:domain` | Shared models and `Result<T, E>` wrapper |

---

## Tech stack

- **Jetpack Compose** — entire UI, MVI pattern, `StateFlow<UiState>`
- **Google Maps SDK** — interactive pin-drop for guessing
- **Hilt** — dependency injection throughout
- **Room** — local persistence for high scores
- **Moshi + KSP** — JSON deserialisation (DTOs in data modules)
- **DataStore** — settings persistence (selected photo source)
- **Coroutines + Flow** — async and reactive throughout
- **Flickr API** — primary geotagged photo source (100 photos per call)
- **BenHikes API** — secondary photo source (custom endpoint)
- **detekt** — static analysis, enforced in CI

---

## Build & setup

### Prerequisites

1. Copy the template and fill in your API keys:
   ```
   cp local.properties.template local.properties
   ```
   Then edit `local.properties`:
   ```
   FLICKR_API_KEY=<your Flickr API key>
   MAPS_API_KEY=<your Google Maps API key>
   BENHIKES_BASE_URL=<base URL for the BenHikes API>
   ```
   > `local.properties` is gitignored and never committed.

2. Install to a connected device or emulator:
   ```
   ./gradlew installDebug
   ```

---

## Test & lint

```bash
./gradlew test                   # all unit tests
./gradlew :domain:game:test      # single-module tests
./gradlew detekt                 # static analysis
./gradlew check                  # lint + tests combined
./gradlew connectedAndroidTest   # instrumented tests (requires device/emulator)
```
