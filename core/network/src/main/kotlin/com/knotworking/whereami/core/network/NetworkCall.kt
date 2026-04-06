package com.knotworking.whereami.core.network

import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.core.domain.Result
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

private const val HTTP_BAD_REQUEST = 400
private const val HTTP_UNAUTHORIZED = 401
private const val HTTP_NOT_FOUND = 404
private const val HTTP_REQUEST_TIMEOUT = 408
private const val HTTP_CONFLICT = 409
private const val HTTP_PAYLOAD_TOO_LARGE = 413
private const val HTTP_TOO_MANY_REQUESTS = 429
private const val HTTP_INTERNAL_SERVER_ERROR = 500
private const val HTTP_SERVICE_UNAVAILABLE = 503
private const val HTTP_SERVER_ERROR_MAX = 599

@Suppress("TooGenericExceptionCaught", "SwallowedException")
suspend fun <T> safeCall(call: suspend () -> T): Result<T, DataError.Network> {
    return try {
        Result.Success(call())
    } catch (e: IOException) {
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: HttpException) {
        Result.Error(e.toDataError())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Error(DataError.Network.UNKNOWN)
    }
}

private fun HttpException.toDataError(): DataError.Network = when (code()) {
    HTTP_BAD_REQUEST -> DataError.Network.BAD_REQUEST
    HTTP_UNAUTHORIZED -> DataError.Network.UNAUTHORIZED
    HTTP_NOT_FOUND -> DataError.Network.NOT_FOUND
    HTTP_REQUEST_TIMEOUT -> DataError.Network.REQUEST_TIMEOUT
    HTTP_CONFLICT -> DataError.Network.CONFLICT
    HTTP_PAYLOAD_TOO_LARGE -> DataError.Network.PAYLOAD_TOO_LARGE
    HTTP_TOO_MANY_REQUESTS -> DataError.Network.TOO_MANY_REQUESTS
    HTTP_SERVICE_UNAVAILABLE -> DataError.Network.SERVICE_UNAVAILABLE
    in HTTP_INTERNAL_SERVER_ERROR..HTTP_SERVER_ERROR_MAX -> DataError.Network.SERVER_ERROR
    else -> DataError.Network.UNKNOWN
}
