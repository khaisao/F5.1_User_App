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
    val sb = StringBuilder(64)
    var millis = time
    if (millis <= 0) {
        return ""
    }
    val days = TimeUnit.MILLISECONDS.toDays(millis)
    millis -= TimeUnit.DAYS.toMillis(days)
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    millis -= TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    millis -= TimeUnit.MINUTES.toMillis(minutes)

    if (days > 30) {
        sb.append(1)
        sb.append(this.resources.getString(R.string.month_or_more))
        return sb.toString()
    }

    if (days in 8..30) {
        sb.append(1)
        sb.append(this.resources.getString(R.string.within_month))
        return sb.toString()
    }
    if (days in 4..7) {
        sb.append(1)
        sb.append(this.resources.getString(R.string.within_week))
        return sb.toString()
    }
    if (days in 2..3) {
        sb.append(3)
        sb.append(this.resources.getString(R.string.within_days))
        return sb.toString()
    }
    if (hours in 2..24) {
        sb.append(24)
        sb.append(this.resources.getString(R.string.within_hour))
        return sb.toString()

    }
    if (minutes in 31..59) {
        sb.append(1)
        sb.append(this.resources.getString(R.string.within_hour))
        return sb.toString()
    }

    if(minutes <= 5){
        sb.append(5)
        sb.append(this.resources.getString(R.string.within_minutes))
        return sb.toString()
    }

    if(minutes <= 30){
        sb.append(30)
        sb.append(this.resources.getString(R.string.within_minutes))
        return sb.toString()
    }
    return ""

}


fun Long.ago(): LocalDate = LocalDate.now().minusDays(this)

