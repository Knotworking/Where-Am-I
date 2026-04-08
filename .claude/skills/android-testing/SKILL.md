---
name: android-testing
description: |
  Testing patterns for Android/KMP - ViewModel unit tests with JUnit5, Turbine, AssertK,
  UnconfinedTestDispatcher, fake repositories, SavedStateHandle, and Compose UI tests. 
  Use this skill whenever writing or reviewing tests for ViewModels, repositories, use cases, 
  or Compose screens. Trigger on phrases like "write a test", "unit test the ViewModel", 
  "test a repository", "Turbine", "fake repository", "UnconfinedTestDispatcher", "runTest", 
  "ComposeTestRule", or "JUnit5".
---

# Android / KMP Testing

## Stack

| Concern                  | Library                                                |
|--------------------------|--------------------------------------------------------|
| Test framework           | JUnit5                                                 |
| Assertions               | AssertK                                                |
| Flow / StateFlow testing | Turbine                                                |
| Coroutine testing        | `kotlinx-coroutines-test` + `UnconfinedTestDispatcher` |
| UI testing               | `ComposeTestRule`                                      |

 
---

## ViewModel Unit Tests

### Setup

```kotlin
class LeaderboardViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
```

### Testing State with Turbine

```kotlin
@Test
fun `loading scores emits scores in state`() = runTest {
        val repo = FakeHighScoreRepository()
        val viewModel = LeaderboardViewModel(repo)

        viewModel.uiState.test {
            viewModel.onAction(LeaderboardAction.OnClearClick)
            assertThat(awaitItem().isLoading).isTrue()
            assertThat(awaitItem().scores).isEmpty()
        }
    }
```

### Testing Events (one-time side effects)

```kotlin
@Test
fun `clicking back sends NavigateBack event`() = runTest {
        val viewModel = LeaderboardViewModel(FakeHighScoreRepository())

        viewModel.events.test {
            viewModel.onAction(LeaderboardAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(LeaderboardEvent.NavigateBack)
        }
    }
```

 
---

## Fake Repositories

Prefer **fakes** (not mocks) for repository dependencies. A fake is a simple in-memory
implementation:

```kotlin
class FakeHighScoreRepository : HighScoreRepository {
    private val scores = mutableListOf<HighScore>()
    var shouldReturnError = false

    override fun getTopScores(): Flow<List<HighScore>> = flowOf(scores.toList())

    override suspend fun save(totalScore: Int): EmptyResult<DataError.Local> {
        scores.add(HighScore(id = scores.size.toLong(), totalScore = totalScore, timestamp = System.currentTimeMillis()))
        return Result.Success(Unit)
    }

    override suspend fun clearAll(): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        scores.clear()
        return Result.Success(Unit)
    }
}
```

 
---

## SavedStateHandle in Tests

Instantiate it directly — no mocking needed:

```kotlin
val savedStateHandle = SavedStateHandle(mapOf("currentRound" to 3))
val viewModel = GameViewModel(savedStateHandle, FakePhotoRepository())
```

 
---

## When to Inject Dispatchers

Only inject `CoroutineDispatcher` into a class when:

1. It dispatches to a non-main dispatcher (e.g., `IO`), AND
2. That class is directly unit-tested.

ViewModels that only use `viewModelScope` do not need injected dispatchers. Use
`Dispatchers.setMain()` in tests instead.

If a non-ViewModel class uses `withContext(Dispatchers.IO)` and is unit-tested, inject the
dispatcher:

```kotlin
class ImageCompressor(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    suspend fun compress(bytes: ByteArray): ByteArray = withContext(ioDispatcher) { ... }
}

// In test:
val compressor = ImageCompressor(ioDispatcher = UnconfinedTestDispatcher())
```

 
---

## Integration and E2E Tests

Write integration tests where database/network interactions are non-trivial. Write E2E tests for
complex user flows using `ComposeTestRule`:

```kotlin
@get:Rule
val composeTestRule = createComposeRule()

@Test
fun leaderboard_displaysScores_afterLoad() {
    composeTestRule.setContent {
        LeaderboardScreen(
            uiState = LeaderboardUiState(
                isLoading = false,
                scores = listOf(HighScore(id = 1, totalScore = 5000, timestamp = 1712345678000))
            ),
            onBack = {},
            onClear = {}
        )
    }
    composeTestRule.onNodeWithText("5000").assertIsDisplayed()
}
```

---

## Robot Pattern (Complex UI / E2E Tests)

For complex end-to-end or multi-step UI tests, use the **Robot Pattern** to separate test intent
from Compose interactions. A robot encapsulates all `composeTestRule` interactions for a screen,
keeping tests readable and DRY.

### Structure

Every robot function returns `this` so calls can be chained like a builder:

```kotlin
// Robot class — owns all UI interactions for the screen
class LeaderboardRobot(private val composeTestRule: ComposeContentTestRule) {

    fun setContent(
        uiState: LeaderboardUiState,
        onBack: () -> Unit = {},
        onClear: () -> Unit = {}
    ) = apply {
        composeTestRule.setContent {
            LeaderboardScreen(uiState = uiState, onBack = onBack, onClear = onClear)
        }
    }

    fun assertScoreVisible(score: Int) = apply {
        composeTestRule.onNodeWithText(score.toString()).assertIsDisplayed()
    }

    fun clickClear() = apply {
        composeTestRule.onNodeWithTag("clear_button").performClick()
    }

    fun assertEmptyState() = apply {
        composeTestRule.onNodeWithTag("empty_state").assertIsDisplayed()
    }
}
```

### Usage in Tests

```kotlin
class LeaderboardScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val robot by lazy { LeaderboardRobot(composeTestRule) }

    @Test
    fun displaysScores_afterLoad() {
        robot
            .setContent(LeaderboardUiState(scores = listOf(HighScore(id = 1, totalScore = 5000, timestamp = 0))))
            .assertScoreVisible(5000)
    }

    @Test
    fun showsEmptyState_whenNoScores() {
        robot
            .setContent(LeaderboardUiState(scores = emptyList()))
            .assertEmptyState()
    }

    @Test
    fun clickingClear_triggersCallback() {
        var cleared = false
        robot
            .setContent(
                uiState = LeaderboardUiState(scores = listOf(HighScore(id = 1, totalScore = 5000, timestamp = 0))),
                onClear = { cleared = true }
            )
            .assertScoreVisible(5000)
            .clickClear()
    }
}
```

**When to use:** Apply the robot pattern when a screen has 3+ UI test cases, when multiple tests
share the same setup/assertion sequences, or when testing complex multi-step user flows (e.g., play
a round → submit guess → assert score displayed).
 
---

## What to Test

- Unit-test every ViewModel and any non-trivial domain/data logic.
- Unit-test every UseCase.
- Unit-test any logic that is likely to change.
- Use fakes over mocks where possible — fakes are simpler and catch more real bugs.
- Write integration tests where DB/network interactions are non-trivial.
- Write E2E Compose tests for critical user flows.
- Use the robot pattern for complex UI/E2E tests with multiple test cases or shared interaction
  sequences.
