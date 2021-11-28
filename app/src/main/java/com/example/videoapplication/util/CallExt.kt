package com.example.videoapplication.util


import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import com.example.videoapplication.util.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Suspend extension that allows suspend [Call] inside coroutine.
 *
 * @return sealed class [Result] object that can be
 *         casted to [Result.Ok] (success) or [Result.Error] (HTTP error) and [Result.Exception] (other errors)
 */
public suspend fun <T : Any> Call<T>.awaitResult(): Result<T> {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>) {
                continuation.resume(
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body == null) {
                            Result.Exception(EmptyBodyException())
                        } else {
                            Result.Ok(body, response.raw())
                        }
                    } else {
                        Result.Error(HttpException(response), response.raw())
                    }
                )
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                // Don't bother with resuming the continuation if it is already cancelled.
                if (continuation.isCancelled) return
                val ex = when (t) {
                    is ConnectException -> NetworkUnavailableException()
                    is UnknownHostException -> NetworkUnavailableException()
                    is SocketTimeoutException -> TimeOutException()
                    is ClassCastException -> ParseDataException()
                    else -> t
                }
                continuation.resume(Result.Exception(ex))
            }
        })

        registerOnCompletion(continuation)
    }
}

public suspend fun <T : Any> Call<T>.awaitWithException(): T {
    return suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        continuation.resumeWithException(EmptyBodyException())
                    } else {
                        continuation.resume(body)
                    }
                } else {
                    continuation.resumeWithException(HttpException(response))
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                // Don't bother with resuming the continuation if it is already cancelled.
                if (continuation.isCancelled) return
                val ex = when (t) {
                    is ConnectException -> NetworkUnavailableException()
                    is UnknownHostException -> NetworkUnavailableException()
                    is SocketTimeoutException -> TimeOutException()
                    is ClassCastException -> ParseDataException()
                    else -> t
                }
                continuation.resumeWithException(ex)
            }
        })

        registerOnCompletion(continuation)
    }
}

private fun Call<*>.registerOnCompletion(continuation: CancellableContinuation<*>) {
    continuation.invokeOnCancellation {
        if (continuation.isCancelled)
            try {
                cancel()
            } catch (ex: Throwable) {
                //Ignore cancel exception
            }
    }
}
