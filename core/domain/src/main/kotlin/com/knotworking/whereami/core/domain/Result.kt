package com.knotworking.whereami.core.domain

sealed interface Result<out T, out E : Error> {
    data class Success<out T>(val data: T) : Result<T, Nothing>
    data class Error<out E : com.knotworking.whereami.core.domain.Error>(val error: E) : Result<Nothing, E>
}

inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T, E : Error, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
}
