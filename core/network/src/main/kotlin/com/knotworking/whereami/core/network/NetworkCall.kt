package com.knotworking.whereami.core.network

import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.core.domain.Result
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

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
