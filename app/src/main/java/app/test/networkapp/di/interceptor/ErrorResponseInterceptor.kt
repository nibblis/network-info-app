package app.test.networkapp.di.interceptor

import app.test.networkapp.data.models.exception.HttpClientException
import app.test.networkapp.data.models.exception.HttpException
import app.test.networkapp.data.models.exception.HttpServerException
import okhttp3.Interceptor
import okhttp3.Response

/**
 * An Interceptor that converts non-successful HTTP responses into typed exceptions.
 * This allows the repository/viewModel layer to catch specific error types (client vs. server)
 * and react accordingly.
 */
class ErrorResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) {
            val exception = when (response.code) {
                in 400..499 -> HttpClientException(response.code, response.message)
                in 500..599 -> HttpServerException(response.code, response.message)
                else -> HttpException(response.code, response.message)
            }
            response.close() // We must close the body before throwing the exception.
            throw exception
        }
        return response
    }
}
