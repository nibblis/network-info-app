package app.test.networkapp.data.models.exception

/** Exception for 5xx server errors. These can be retried as the error may be transient. */
class HttpServerException(
    code: Int,
    message: String
) : HttpException(code, message)
