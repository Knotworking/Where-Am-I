---
name: android-presentation-mvi
description: |
  MVI presentation layer for Android/KMP - State, Action, Event, ViewModel, Root/Screen composable split, UI models, UiText error mapping, and process death with SavedStateHandle. Use this skill whenever creating or reviewing a ViewModel, defining screen state, actions, or events, structuring composables, mapping errors to UI strings, or handling process death. Trigger on phrases like "add a ViewModel", "create a screen", "MVI", "state", "action", "event", "screen composable", "UiText", "SavedStateHandle", "ObserveAsEvents", or "UI model".
---

 
# Android / KMP Presentation Layer (MVI)
 
## Overview
 
Every screen has:
1. **State** — a single data class holding all UI state fields.
2. **Action** (Intent) — a sealed interface of all user-triggered actions.
3. **Event** — a sealed interface of one-time side effects (navigation, snackbar).
4. **ViewModel** — holds `StateFlow<State>`, processes `Action`, emits `Event` via `Channel`.
 
---
 
## State
 
```kotlin
data class LeaderboardUiState(
    val scores: List<HighScore> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null
)
```
 
Always update state with `.update { }` — never replace the entire flow:
```kotlin
_uiState.update { it.copy(isLoading = true) }
```
 
---
 
## Action (Intent)
 
```kotlin
sealed interface LeaderboardAction {
    data object OnClearClick : LeaderboardAction
    data object OnBackClick : LeaderboardAction
}
```
 
---
 
## Event (one-time side effects)
 
```kotlin
sealed interface LeaderboardEvent {
    data object NavigateBack : LeaderboardEvent
    data class ShowSnackbar(val message: UiText) : LeaderboardEvent
}
```
 
---
 
## ViewModel
 
```kotlin
class LeaderboardViewModel(
    private val highScoreRepository: HighScoreRepository
) : ViewModel() {
 
    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState = _uiState.asStateFlow()
 
    private val _events = Channel<LeaderboardEvent>()
    val events = _events.receiveAsFlow()
 
    fun onAction(action: LeaderboardAction) {
        when (action) {
            is LeaderboardAction.OnClearClick -> clearScores()
            is LeaderboardAction.OnBackClick -> {
                viewModelScope.launch {
                    _events.send(LeaderboardEvent.NavigateBack)
                }
            }
        }
    }
 
    private fun clearScores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            highScoreRepository.clearAll()
                .onSuccess {
                    _uiState.update { it.copy(scores = emptyList(), isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(LeaderboardEvent.ShowSnackbar(error.toUiText()))
                }
        }
    }
}
```
 
---
 
## Coroutine Dispatchers
 
**Do not inject** unless the class is unit-tested and dispatches to a non-main dispatcher. For ViewModel tests, use `Dispatchers.setMain(UnconfinedTestDispatcher())` in test setup.
 
For blocking code that doesn't support suspension, wrap it:
```kotlin
suspend fun compressImage(bytes: ByteArray): ByteArray = withContext(Dispatchers.IO) {
    // blocking compression logic
}
```
 
Only inject `CoroutineDispatcher` when:
1. The class dispatches to a non-main dispatcher (e.g., `IO`), AND
2. That class is directly unit-tested.
 
---
 
## Mapping Errors to UI Strings

`UiText` (`core:ui`) wraps strings that originate from — or could originate from — a string resource:

```kotlin
sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    class StringResource(val id: Int, val args: Array<Any> = emptyArray()) : UiText
}
```

**When to use `UiText`:** For any string that comes from a string resource, could be localized, or might be either a resource or a dynamic value depending on context (e.g., error messages that map to `R.string.*`).

**When to use plain `String`:** For values that are always dynamic and never come from resources — e.g., a formatted score, a formatted date, a distance string. These should be exposed as `String` directly in the state or UI model.

```kotlin
// UiText — error message that maps to a string resource
data class LeaderboardUiState(
    val error: UiText? = null
)

// Plain String — always dynamic, never a resource
data class HighScoreUi(
    val formattedScore: String,
    val formattedDate: String
)
```

Define `DataError.toUiText()` extension functions in `core:presentation` (or feature `presentation`) that map error enums to `UiText.StringResource`.
 
---
 
## UI Model (Presentation Model)
 
When a domain model needs UI-specific formatting (dates, units, currency), create a dedicated UI model in the presentation layer:
 
```kotlin
data class HighScoreUi(
    val id: Long,
    val formattedScore: String,   // e.g. "4,800 pts"
    val formattedDate: String     // e.g. "Apr 5, 14:23"
)
 
fun HighScore.toHighScoreUi(): HighScoreUi = HighScoreUi(
    id = id,
    formattedScore = "$totalScore pts",
    formattedDate = DateFormat.format("MMM d, HH:mm", timestamp).toString()
)
```
 
UI models are always suffixed with `Ui` (e.g., `HighScoreUi`).
 
---
 
## Composable Structure

Both the Root and Screen composable live in the **same file** (e.g., `LeaderboardScreen.kt`).

### Root Composable (suffixed `Root`)

Receives the ViewModel (via `hiltViewModel()`) and any callbacks needed for navigation. Observes events. Passes state and `onAction` down.

### Screen Composable (suffixed `Screen`)

Receives only `uiState` and `onAction` (or typed callbacks). No ViewModel reference. Can be previewed independently.

```kotlin
// LeaderboardScreen.kt — Root + Screen in a single file

@Composable
fun LeaderboardScreenRoot(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is LeaderboardEvent.NavigateBack -> onBack()
            is LeaderboardEvent.ShowSnackbar -> { /* show snackbar */ }
        }
    }

    LeaderboardScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
fun LeaderboardScreen(
    uiState: LeaderboardUiState,
    onAction: (LeaderboardAction) -> Unit
) { ... }

@Preview
@Composable
private fun LeaderboardScreenPreview() {
    LeaderboardScreen(uiState = LeaderboardUiState(), onAction = {})
}
```
 
---
 
## Process Death
 
When a screen involves complex forms or critical user input, restore essential fields using `SavedStateHandle`:
 
```kotlin
class GameViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getRandomPhotoUseCase: GetRandomPhotoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        GameUiState(
            currentRound = savedStateHandle["currentRound"] ?: 1,
            totalScore = savedStateHandle["totalScore"] ?: 0
        )
    )
 
    fun nextRound() {
        val next = _uiState.value.currentRound + 1
        savedStateHandle["currentRound"] = next
        _uiState.update { it.copy(currentRound = next) }
    }
}
```
 
Only save what truly matters after process death — not the entire state.
 
---
 
## Naming Conventions
 
| Thing | Convention | Example |
|---|---|---|
| ViewModel | `<Screen>ViewModel` | `GameViewModel`, `LeaderboardViewModel` |
| State | `<Screen>UiState` | `GameUiState`, `LeaderboardUiState` |
| Action | `<Screen>Action` | `LeaderboardAction` |
| Event | `<Screen>Event` | `LeaderboardEvent` |
| Root composable | `<Screen>Root` | `GameScreenRoot`, `LeaderboardScreenRoot` |
| Screen composable | `<Screen>Screen` | `GameScreen`, `LeaderboardScreen` |
| UI model | `<Model>Ui` | `HighScoreUi` |
 
---
 
## Checklist: Adding a New Screen
 
- [ ] Define `State`, `Action`, `Event` in `feature:presentation`
- [ ] Implement `ViewModel` in `feature:presentation`
- [ ] Create `<Screen>Root` composable (holds ViewModel, observes events)
- [ ] Create `<Screen>Screen` composable (pure state + onAction, previewable)
- [ ] Map any domain errors to `UiText` via extension functions
- [ ] Add `SavedStateHandle` for any form fields that must survive process death
