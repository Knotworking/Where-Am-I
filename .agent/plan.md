# Project Plan

Create a GeoGuessr-type game for Android called 'WhereAmI' using the Flickr API, Google Maps SDK, and following Clean Architecture with feature modules and modern Android libraries.

## Project Brief

WhereAmI Project Brief

Where AmI is a GeoGuessr-inspired game for Android that challenges players to pinpoint the location of geotagged photos. By leveraging the Flickr API and modern Android development practices, the app provides an engaging, high-performance, and visually vibrant experience.

## Features
- Random Photo Discovery: Fetches and displays geotagged images using the Flickr API.
- Interactive Guessing Map: Provides an integrated map interface for players to place a pin on their estimated location.
- Proximity-Based Scoring: Calculates and displays a score based on the geodesic distance between the player's guess and the photo's actual coordinates.
- Round-Based Game Flow: A complete game loop including photo fetching, guessing, results summary, and navigation to the next round.

## High-Level Technical Stack
- Kotlin: The primary programming language for robust and expressive code.
- Jetpack Compose (Material 3): A modern UI toolkit for building a vibrant, edge-to-edge interface following Material Design 3 guidelines.
- Coroutines & StateFlow: Utilized for asynchronous operations and reactive state management without callbacks or LiveData.
- Hilt: Dependency injection framework to support Clean Architecture and feature modularization.
- Retrofit & Moshi: For type-safe networking and efficient JSON parsing of the Flickr API responses.
- Google Maps SDK for Android: Provides the interactive map component for the guessing mechanism.
- Coil: A Compose-first image loading library for high-performance image fetching and caching.
- KSP (Kotlin Symbol Processing): Used for efficient code generation for Hilt, Moshi, and other annotation-based components.

## Architecture
- Clean Architecture with feature modules.
- Domain (UseCases), Data (Repository), UI (ViewModels, Compose).
- No LiveData, No callbacks.
- Edge-to-Edge display and adaptive icon.

## Implementation Steps
**Total Duration:** 46m

### Task_1_Setup_and_Data: Configure project dependencies (Hilt, Google Maps SDK), set up API keys, and implement the Data layer for Flickr API integration using Retrofit and Moshi.
- **Status:** COMPLETED
- **Updates:** Task 1 completed.
- **Acceptance Criteria:**
  - Hilt is correctly configured and building
  - Flickr API integration fetches random geotagged photos
  - API keys for Flickr and Google Maps are securely integrated
  - Data models and repositories are implemented
- **Duration:** 34m 42s

### Task_2_Domain_and_ViewModel: Implement the Domain layer (UseCases) for distance calculation and the ViewModel to manage the game state, including round transitions and scoring logic.
- **Status:** COMPLETED
- **Updates:** Task 2 completed.
- **Acceptance Criteria:**
  - UseCase accurately calculates geodesic distance between coordinates
  - ViewModel maintains game state (photos, guesses, scores, rounds)
  - Unit tests for core game logic pass
- **Duration:** 3m 7s

### Task_3_Compose_UI: Build the user interface using Jetpack Compose, integrating Coil for photo display and Google Maps for the interactive guessing pin.
- **Status:** COMPLETED
- **Updates:** Task 3 completed.
- **Acceptance Criteria:**
  - Flickr photo is displayed prominently with Coil
  - Google Maps allows the user to drop a pin and submit a guess
  - Results are displayed clearly after each guess
  - Navigation flow between game screen and results works correctly
- **Duration:** 4m 9s

### Task_4_Polish_and_Verify: Apply Material 3 vibrant theme, implement Edge-to-Edge display, create an adaptive app icon, and perform final verification of the app's stability and requirements.
- **Status:** COMPLETED
- **Updates:** Task 4 completed.
- Material 3 vibrant theme refined with custom color palette.
- Adaptive icon (Globe with Pin) created.
- Edge-to-Edge display polished.
- Final verification by critic_agent confirms app is stable, follows Clean Architecture, and implements all core features.
- App handles missing API keys gracefully with an error screen.
- Final build and tests successful.
- **Acceptance Criteria:**
  - Material 3 vibrant color scheme and theme applied
  - Full Edge-to-Edge display implemented
  - Adaptive app icon created matching the game theme
  - Project builds successfully and 'app does not crash'
  - All existing tests pass
  - Final app matches user requirements and UI design guidelines
- **Duration:** 4m 2s

