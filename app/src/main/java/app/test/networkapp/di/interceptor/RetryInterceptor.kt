package app.test.networkapp.di.interceptor

import android.util.Log
import app.test.networkapp.data.models.exception.HttpServerException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * An Interceptor that demonstrates a finite retry mechanism for transient server errors (5xx)
 * and network connectivity issues. This is crucial for building a resilient client.
 * This implementation is safe for idempotent requests (GET, PUT, DELETE).
 */
class RetryInterceptor(private val maxRetries: Int = 3, private val delayMillis: Long = 1000) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null

        for (tryCount in 1..maxRetries) {
            try {
                Log.d("RetryInterceptor", "Attempt #$tryCount for ${request.url}")
                val response = chain.proceed(request)

                // If the response is not a server error, we're done with retries.
                // The ErrorResponseInterceptor will handle client errors (4xx) if any.
                if (response.code < 500) {
                    return response
                }
                // It's a 5xx, so we close the response and let the loop retry.
                response.close()
                lastException = HttpServerException(response.code, "Attempt #$tryCount failed with server error")

            } catch (e: IOException) {
                lastException = e
                Log.w("RetryInterceptor", "Attempt #$tryCount failed with IOException", e)
            }

            // If we're not on the last try, wait before retrying.
            if (tryCount < maxRetries) {
                Thread.sleep(delayMillis)
            }
        }
        // If we've exhausted all retries, throw the last-known exception.
        throw lastException ?: IOException("Unknown error after $maxRetries retries")
    }
}
