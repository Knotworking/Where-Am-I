package com.knotworking.whereami.core.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

private enum class TestError : Error {
    SOME_ERROR
}

class ResultTest {

    @Test
    fun `onSuccess executes action for Success`() {
        var called = false
        val result: Result<String, TestError> = Result.Success("data")

        result.onSuccess { called = true }

        assertThat(called).isTrue()
    }

    @Test
    fun `onSuccess does not execute action for Error`() {
        var called = false
        val result: Result<String, TestError> = Result.Error(TestError.SOME_ERROR)

        result.onSuccess { called = true }

        assertThat(called).isFalse()
    }

    @Test
    fun `onSuccess returns original result for chaining`() {
        val result: Result<String, TestError> = Result.Success("data")

        val returned = result.onSuccess { }

        assertThat(returned).isEqualTo(result)
    }

    @Test
    fun `onFailure executes action for Error`() {
        var captured: TestError? = null
        val result: Result<String, TestError> = Result.Error(TestError.SOME_ERROR)

        result.onFailure { captured = it }

        assertThat(captured).isEqualTo(TestError.SOME_ERROR)
    }

    @Test
    fun `onFailure does not execute action for Success`() {
        var called = false
        val result: Result<String, TestError> = Result.Success("data")

        result.onFailure { called = true }

        assertThat(called).isFalse()
    }

    @Test
    fun `map transforms Success data`() {
        val result: Result<Int, TestError> = Result.Success(5)

        val mapped = result.map { it * 2 }

        assertThat(mapped).isEqualTo(Result.Success(10))
    }

    @Test
    fun `map passes Error through unchanged`() {
        val result: Result<Int, TestError> = Result.Error(TestError.SOME_ERROR)

        val mapped = result.map { it * 2 }

        assertThat(mapped).isEqualTo(Result.Error(TestError.SOME_ERROR))
    }

    @Test
    fun `asEmptyResult converts Success to Success Unit`() {
        val result: Result<String, TestError> = Result.Success("data")

        val empty = result.asEmptyResult()

        assertThat(empty).isInstanceOf<Result.Success<Unit>>()
        assertThat((empty as Result.Success).data).isEqualTo(Unit)
    }

    @Test
    fun `asEmptyResult passes Error through unchanged`() {
        val result: Result<String, TestError> = Result.Error(TestError.SOME_ERROR)

        val empty = result.asEmptyResult()

        assertThat(empty).isEqualTo(Result.Error(TestError.SOME_ERROR))
    }
}
