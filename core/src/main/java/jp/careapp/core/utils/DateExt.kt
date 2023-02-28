package jp.careapp.core.utils

import android.content.Context
import jp.careapp.core.R
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.* // ktlint-disable no-wildcard-imports
import java.util.concurrent.TimeUnit

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

fun Context.getDurationBreakdown(time: Long): String {
    val zero: Long = 0
    var millis = time
    if (millis <= 0) {
        return "now"
    }
//    require(millis >= 0) { "Duration must be greater than zero!" }
    val days = TimeUnit.MILLISECONDS.toDays(millis)
    millis -= TimeUnit.DAYS.toMillis(days)
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    millis -= TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    millis -= TimeUnit.MINUTES.toMillis(minutes)
    val sb = StringBuilder(64)

    if (days > 30) {
        val month = days / 30
        sb.append(month)
        sb.append(this.resources.getString(R.string.within_month))
        return sb.toString()
    }

    if (days > 7) {
        val week = days / 7
        sb.append(week)
        sb.append(this.resources.getString(R.string.within_week))
        return sb.toString()
    }
    if (days != zero) {
        sb.append(days)
        sb.append(this.resources.getString(R.string.within_days))

        return sb.toString()

    }
    if (hours != zero) {
        sb.append(hours)
        sb.append(this.resources.getString(R.string.within_hour))

        return sb.toString()

    }
    if (minutes != zero) {
        sb.append(minutes)
        sb.append(this.resources.getString(R.string.within_minutes))

        return sb.toString()
    } else {
        return "Now"
    }
}


fun Long.ago(): LocalDate = LocalDate.now().minusDays(this)

