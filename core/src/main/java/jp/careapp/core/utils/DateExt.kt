package jp.careapp.core.utils

import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.* // ktlint-disable no-wildcard-imports

fun String.toDate(format: String): Date? {
    val dateFormatter = SimpleDateFormat(format, Locale.US)
    return try {
        dateFormatter.parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun String.toJPDate(format: String): Date? {
    val dateFormatter = SimpleDateFormat(format, Locale.US)
    dateFormatter.timeZone = TimeZone.getTimeZone("Japan")
    return try {
        dateFormatter.parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun getCurrentDateTime(): Date? {
    try {
        return Calendar.getInstance().time
    } catch (exception: Exception) {
    }
    return null
}

fun Date.toString(format: String): String {
    val dateFormatter = SimpleDateFormat(format, Locale.US)
    return dateFormatter.format(this)
}

fun String.getCurrentDayName(): String {
    val cal = Calendar.getInstance()
    val date = toDate("yyyy/MM/dd")
    if (date != null) {
        cal.time = date
    }
    return when (cal[Calendar.DAY_OF_WEEK]) {
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        else -> "Sun"
    }
}
