package app.test.networkapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
private const val UI_DATE_FORMAT = "dd.MM.yyyy"

fun String.toDate(): Date? {
    return try {
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        dateFormat.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Date.toFormattedString(): String {
    val formatter = SimpleDateFormat(UI_DATE_FORMAT, Locale.getDefault())
    return formatter.format(this)
}