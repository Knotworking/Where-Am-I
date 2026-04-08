---
name: android-error-handling
description: |
  Generic Result wrapper, error types, and extension helpers for Android/KMP - Result<T, E>, DataError, EmptyResult, map, onSuccess, onFailure. Use this skill whenever defining error types, creating a Result wrapper, handling success/failure flows, mapping errors, or working with typed errors anywhere in the app (not just data layer — also validation, auth, domain logic). Trigger on phrases like "Result wrapper", "error handling", "DataError", "onSuccess", "onFailure", "EmptyResult", "map result", "error type", "validation error", or "typed errors".
---

# Android / KMP Error Handling

## Result Wrapper (`core:domain`)

A generic, typed Result that works across all layers — data, domain, presentation, validation, anywhere a function can succeed or fail with a typed error.

```kotlin
interface Error

sealed interface Result<out D, out E : Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : com.example.Error>(val error: E) : Result<Nothing, E>
}

typealias EmptyResult<E> = Result<Unit, E>
```

---

## Extension Helpers (`core:domain`)

These live alongside the `Result` definition:

```kotlin
inline fun <T, E : Error, R> Result<T, E>.map(
    map: (T) -> R
): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(this.data))
    }
}

inline fun <T, E : Error> Result<T, E>.onSuccess(
    action: (T) -> Unit
): Result<T, E> {
    return when (this) {
        is Result.Error -> this
        is Result.Success -> {
            action(this.data)
            this
        }
    }
}

inline fun <T, E : Error> Result<T, E>.onFailure(
    action: (E) -> Unit
): Result<T, E> {
    return when (this) {
        is Result.Error -> {
            action(error)
            this
        }
        is Result.Success -> this
    }
}

fun <T, E : Error> Result<T, E>.asEmptyResult(): EmptyResult<E> {
    return map { }
}
```

All helpers return `Result` so they can be chained:
```kotlin
repository.saveHighScore(totalScore)
    .onSuccess { /* update UI */ }
    .onFailure { /* show error */ }
    .asEmptyResult()
```

---

## Shared Error Types (`core:domain`)

### DataError

```kotlin
sealed interface DataError : Error {
    enum class Network : DataError {
        BAD_REQUEST,
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERVICE_UNAVAILABLE,
        SERIALIZATION,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        UNKNOWN
    }
}
```

### Feature-Specific Errors

Features define their own error types by implementing `Error`:

```kotlin
enum class PasswordValidationError : Error {
    TOO_SHORT,
    NO_UPPERCASE,
    NO_DIGIT
}

// Used with Result — always a single error, not a list:
fun validatePassword(pw: String): EmptyResult<PasswordValidationError>
```

Multiple validation errors are not supported — always return a single error type per Result.

---

## Exception Handling Philosophy

Never throw exceptions for expected failures — always return `Result.Error`. Catch exceptions at the layer that is responsible for the exception:

| Exception origin | Catch in | Example |
|---|---|---|
| HTTP / network | Data layer | `UnresolvedAddressException` → `DataError.Network.NO_INTERNET` |
| Database / disk | Data layer | `SQLiteFullException` → `DataError.Local.DISK_FULL` |
| Business logic | Domain layer | Invalid input → `Result.Error(ValidationError.TOO_SHORT)` |
| Presentation | Presentation layer | Catch and map to `Result.Error` at that layer |

The layer that owns the exception catches it and converts it to a typed `Result.Error`. Upper layers never see raw exceptions for expected failures.

---

## Mapping Errors to UiText

Every error type that is displayed to the user should have a `.toUiText()` extension function. Place it in:

- **Feature's `ui` module** — if the error is feature-specific (e.g., `GameError.toUiText()`)
- **`core:ui`** — if the error is shared across features (e.g., `DataError.toUiText()`)

If an error is purely internal and never shown to the user (e.g., a retry signal, an internal state marker), it does not need a `.toUiText()` mapping.

```kotlin
// core:ui
fun DataError.toUiText(): UiText {
    return when (this) {
        DataError.Network.NO_INTERNET -> UiText.StringResource(R.string.error_no_internet)
        DataError.Network.SERVER_ERROR -> UiText.StringResource(R.string.error_server)
        DataError.Network.UNAUTHORIZED -> UiText.StringResource(R.string.error_unauthorized)
        DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
        // ... map all user-facing cases
        else -> UiText.StringResource(R.string.error_unknown)
    }
}
```

---

## Safe Call Helpers (`core:network`)

`safeCall` wraps a Retrofit suspension call and maps `IOException` / `HttpException` to `Result<T, DataError.Network>`. Lives in `core/network/NetworkCall.kt`:

```kotlin
suspend fun <T> safeCall(call: suspend () -> T): Result<T, DataError.Network> {
    return try {
        Result.Success(call())
    } catch (e: IOException) {
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: HttpException) {
        Result.Error(e.toDataError())
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Result.Error(DataError.Network.UNKNOWN)
    }
}

private fun HttpException.toDataError(): DataError.Network = when (code()) {
    400 -> DataError.Network.BAD_REQUEST
    401 -> DataError.Network.UNAUTHORIZED
    404 -> DataError.Network.NOT_FOUND
    408 -> DataError.Network.REQUEST_TIMEOUT
    409 -> DataError.Network.CONFLICT
    413 -> DataError.Network.PAYLOAD_TOO_LARGE
    429 -> DataError.Network.TOO_MANY_REQUESTS
    503 -> DataError.Network.SERVICE_UNAVAILABLE
    in 500..599 -> DataError.Network.SERVER_ERROR
    else -> DataError.Network.UNKNOWN
}
```

Usage in a repository is clean and uniform:
```kotlin
return when (val result = safeCall { dataSource.fetchPhoto() }) {
    is Result.Success -> result.data?.let { Result.Success(it) }
        ?: Result.Error(PhotoError.NO_PHOTO_FOUND)
    is Result.Error -> result
}
```

---

## When to Use What

| Scenario | Error type | Example return |
|---|---|---|
| Network call | `DataError.Network` | `Result<List<BenHikesPhotoDto>, DataError.Network>` |
| Local DB access | `DataError.Local` | `Result<HighScore, DataError.Local>` |
| Repository (multi-source) | `DataError` (supertype) | `Result<List<Photo>, DataError>` |
| Domain validation | Custom `Error` enum | `EmptyResult<PasswordValidationError>` |
| Auth logic | Custom `Error` enum | `Result<User, AuthError>` |

The `Result` wrapper is not limited to the data layer — use it anywhere a function has typed success and failure outcomes.
