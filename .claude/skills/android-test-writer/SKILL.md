---
name: android-test-writer
description: This skill should be used when the user asks to "write tests", "add tests", "create unit tests", "test this code", "write a test for", or mentions testing a ViewModel, use case, repository, or any Kotlin class in this Android project. Use this skill whenever the user wants to add, write, or generate test code.
version: 1.0.0
---

# Android Test Writer

Write unit tests for this Android project following established conventions.

## Mocking

Use **Mockk** exclusively — never Mockito.

- `mockk()` for interface/class mocks
- `coEvery { ... }` / `coVerify { ... }` for suspending functions
- `every { ... }` / `verify { ... }` for non-suspending functions

## Test Placement

Write tests in `src/test/` within the same module as the code under test. Package mirrors the source package.

## Domain Layer Tests

Zero Android dependencies — pure JUnit4 + Mockk only. Use `runBlocking` for suspending invocations.

```kotlin
class MyUseCaseTest {

    private val myRepository: MyRepository = mockk()
    private val myUseCase = MyUseCase(myRepository)

    @Test
    fun `invoke returns photo from repository`() = runBlocking {
        val expected = Photo("1", "title", 0.0, 0.0, "url")
        coEvery { myRepository.getRandomGeotaggedPhoto() } returns expected

        val result = myUseCase()

        assertEquals(expected, result)
    }
}
```

## ViewModel Tests

Use `UnconfinedTestDispatcher` and set/reset `Dispatchers.Main` in `@Before`/`@After`.

```kotlin
@ExperimentalCoroutinesApi
class MyViewModelTest {

    private val myUseCase: MyUseCase = mockk()
    private lateinit var viewModel: MyViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { myUseCase() } returns someDefaultValue
        viewModel = MyViewModel(myUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        // assert on state
    }
}
```

## Flow / StateFlow Assertions

- For `StateFlow`, read `.value` directly after triggering state changes — no turbine needed.
- Use `turbine` (`app.cash.turbine`) for collecting emissions from a regular `Flow` if the dependency is present in the module.
- Otherwise, collect with `toList()` inside a coroutine scope.

## Layer Rules

| Layer | Android deps | Hilt | Notes |
|-------|-------------|------|-------|
| `domain:*` | Never | Never | Pure JUnit4 |
| `feature:*` | Test libs only | No `@HiltAndroidTest` | JUnit4 + coroutine test |
| `data:*` | Minimal | No | JUnit4 + Mockk |

## Coverage Goals

- **Use cases**: test `invoke()` with mocked repository — happy path, null returns, exceptions.
- **ViewModels**: test initial state, each public function's state transition, and error states.
- **Pure functions** (e.g., `CalculateDistanceUseCase`): concrete assertions with delta tolerance.
- Aim for one test class per use case and one per ViewModel.

## Workflow

1. Read the target class to understand dependencies and public API.
2. Read any existing tests in the module as a style reference.
3. Identify all state transitions or return values to cover.
4. Write the test class following the templates above.
5. Run `./gradlew :<layer>:<module>:test` to confirm tests pass.
6. Run `./gradlew detekt` before committing.