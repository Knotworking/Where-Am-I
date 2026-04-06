package com.knotworking.whereami.core.network

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.core.domain.Result
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.HttpException
import retrofit2.Response

class SafeCallTest {

    @Test
    fun `returns Success when call succeeds`() = runTest {
        val result = safeCall { "hello" }

        assertThat(result).isInstanceOf<Result.Success<String>>()
        assertThat((result as Result.Success).data).isEqualTo("hello")
    }

    @Test
    fun `returns NO_INTERNET on IOException`() = runTest {
        val result = safeCall<String> { throw java.io.IOException("no network") }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.NO_INTERNET))
    }

    @Test
    fun `maps HTTP 400 to BAD_REQUEST`() = runTest {
        val result = safeCall<String> { throw httpException(400) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.BAD_REQUEST))
    }

    @Test
    fun `maps HTTP 401 to UNAUTHORIZED`() = runTest {
        val result = safeCall<String> { throw httpException(401) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.UNAUTHORIZED))
    }

    @Test
    fun `maps HTTP 404 to NOT_FOUND`() = runTest {
        val result = safeCall<String> { throw httpException(404) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.NOT_FOUND))
    }

    @Test
    fun `maps HTTP 408 to REQUEST_TIMEOUT`() = runTest {
        val result = safeCall<String> { throw httpException(408) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.REQUEST_TIMEOUT))
    }

    @Test
    fun `maps HTTP 409 to CONFLICT`() = runTest {
        val result = safeCall<String> { throw httpException(409) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.CONFLICT))
    }

    @Test
    fun `maps HTTP 413 to PAYLOAD_TOO_LARGE`() = runTest {
        val result = safeCall<String> { throw httpException(413) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.PAYLOAD_TOO_LARGE))
    }

    @Test
    fun `maps HTTP 429 to TOO_MANY_REQUESTS`() = runTest {
        val result = safeCall<String> { throw httpException(429) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.TOO_MANY_REQUESTS))
    }

    @Test
    fun `maps HTTP 503 to SERVICE_UNAVAILABLE`() = runTest {
        val result = safeCall<String> { throw httpException(503) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.SERVICE_UNAVAILABLE))
    }

    @Test
    fun `maps HTTP 500 to SERVER_ERROR`() = runTest {
        val result = safeCall<String> { throw httpException(500) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.SERVER_ERROR))
    }

    @Test
    fun `maps HTTP 502 to SERVER_ERROR`() = runTest {
        val result = safeCall<String> { throw httpException(502) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.SERVER_ERROR))
    }

    @Test
    fun `maps unmapped HTTP code to UNKNOWN`() = runTest {
        val result = safeCall<String> { throw httpException(418) }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.UNKNOWN))
    }

    @Test
    fun `rethrows CancellationException`() = runTest {
        assertThrows<CancellationException> {
            safeCall<String> { throw CancellationException("cancelled") }
        }
    }

    @Test
    fun `maps generic exception to UNKNOWN`() = runTest {
        val result = safeCall<String> { throw RuntimeException("unexpected") }

        assertThat(result).isEqualTo(Result.Error(DataError.Network.UNKNOWN))
    }

    private fun httpException(code: Int): HttpException {
        val response = Response.error<String>(code, "".toResponseBody(null))
        return HttpException(response)
    }
}
