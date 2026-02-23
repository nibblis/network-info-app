package app.test.networkapp.data.models.exception

import java.io.IOException

/** Base exception for any non-2xx HTTP response. */
open class HttpException(
    val code: Int,
    override val message: String
) : IOException("HTTP Error $code: $message")
