package app.test.networkapp.data.models.exception

/** Exception for 4xx client errors. These should not be retried as the request is likely invalid. */
class HttpClientException(
    code: Int,
    message: String
) : HttpException(code, message)
