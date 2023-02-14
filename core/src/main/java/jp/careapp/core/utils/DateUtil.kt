package jp.careapp.core.utils

import android.text.TextUtils
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateUtil {
    companion object {
        private const val DEFAULT_TIMEZONE = "Asia/Tokyo"
        var DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss"
        var DATE_FORMAT_2 = "yyyy/MM/dd"
        var DATE_FORMAT_3 = "yyyy-MM-dd"
        var DATE_FORMAT_4 = "次回：MM月dd日 HH時～"
        var DATE_FORMAT_5 = "HH:mm"
        var DATE_FORMAT_6 = "昨日 HH:mm"
        var DATE_FORMAT_7 = "yyyy-dd-MM HH:mm:ss"
        var DATE_FORMAT_8 = "MM月dd日"
        var DATE_FORMAT_9 = "yyyy年MM月dd日"
        var DATE_FORMAT_10 = "yyyy/MM/dd HH:mm"
        val ONE_DAY = 60 * 60 * 24 * 1000L

        fun getSimpleDateFormat(format: String?): SimpleDateFormat {
            var sdf = SimpleDateFormat(format)
            return sdf
        }

        fun getSimpleDateFormat(format: String?, locale: Locale): SimpleDateFormat {
            var sdf = SimpleDateFormat(format, locale)
            sdf.timeZone = TimeZone.getTimeZone(DEFAULT_TIMEZONE)
            return sdf
        }

        fun getDateTimeDisplayByFormat(
            format: String?,
            calendar: Calendar
        ): String {
            try {
                val dateFormat: DateFormat = getSimpleDateFormat(format)
                return dateFormat.format(calendar.timeInMillis)
            } catch (e: Exception) {
            }
            return null.toString()
        }

        fun convertStringToCalendar(time: String?, format: String?): Calendar? {
            val cal = Calendar.getInstance()
            val sdf = getSimpleDateFormat(format)
            try {
                cal.time = sdf.parse(time)
            } catch (e: ParseException) {
                Timber.d(DateUtil::class.java.simpleName, "convertStringToCalendar: " + e.message)
            }
            return cal
        }

        fun convertStringToDateString(dateStr: String?, format: String, format2: String): String {
            val str: String
            str = try {
                val srcDf: DateFormat = getSimpleDateFormat(format)
                val date = srcDf.parse(dateStr)
                val destDf: DateFormat = getSimpleDateFormat(format2)
                return destDf.format(date)
            } catch (e: java.lang.Exception) {
                dateStr ?: ""
            }
            return str
        }

        fun getCurrentDate(): String {
            val str: String
            str = try {
                val sdf = getSimpleDateFormat(DATE_FORMAT_1)
                return sdf.format(Date())
            } catch (e: java.lang.Exception) {
                ""
            }
            return str
        }

        fun formatDateToString(format: String, date: Date): String {
            val sdf = getSimpleDateFormat(format)
            return sdf.format(date)
        }

        fun getTimeHistoryChat(dateMessageString: String): String {
            try {
                val calendarToday = Calendar.getInstance()
                val calendarYesterday = Calendar.getInstance()
                calendarYesterday.add(Calendar.DAY_OF_YEAR, -1)
                val dateMessage = convertStringToCalendar(dateMessageString, DATE_FORMAT_1)
                dateMessage?.let {
                    if (calendarToday.get(Calendar.DAY_OF_MONTH) == it.get(Calendar.DAY_OF_MONTH) &&
                        calendarToday.get(Calendar.MONTH) == it.get(Calendar.MONTH) &&
                        calendarToday.get(Calendar.YEAR) == it.get(Calendar.YEAR)
                    ) {
                        return formatDateToString(DATE_FORMAT_5, it.time)
                    } else if (calendarYesterday.get(Calendar.DAY_OF_MONTH) == it.get(Calendar.DAY_OF_MONTH) &&
                        calendarYesterday.get(Calendar.MONTH) == it.get(Calendar.MONTH) &&
                        calendarYesterday.get(Calendar.YEAR) == it.get(Calendar.YEAR)
                    ) {
                        return formatDateToString(DATE_FORMAT_6, it.time)
                    } else {
                        return formatDateToString(DATE_FORMAT_2, it.time)
                    }
                }
                return ""
            } catch (e: java.lang.Exception) {
                return ""
            }
        }

        fun getTimeMessageChat(dateMessageString: String): String {
            if (TextUtils.isEmpty(dateMessageString)) {
                return ""
            }
            try {
                val calendarToday = Calendar.getInstance()
                val calendarYesterday = Calendar.getInstance()
                calendarYesterday.add(Calendar.DAY_OF_YEAR, -1)
                val dateMessage = convertStringToCalendar(dateMessageString, DATE_FORMAT_3)
                dateMessage?.let {
                    if (calendarToday.get(Calendar.DAY_OF_MONTH) == it.get(Calendar.DAY_OF_MONTH) &&
                        calendarToday.get(Calendar.MONTH) == it.get(Calendar.MONTH) &&
                        calendarToday.get(Calendar.YEAR) == it.get(Calendar.YEAR)
                    ) {
                        return "今日"
                    } else if (calendarYesterday.get(Calendar.DAY_OF_MONTH) == it.get(Calendar.DAY_OF_MONTH) &&
                        calendarToday.get(Calendar.MONTH) == it.get(Calendar.MONTH) &&
                        calendarToday.get(Calendar.YEAR) == it.get(Calendar.YEAR)
                    ) {
                        return "昨日"
                    } else if (calendarToday.get(Calendar.YEAR) == it.get(Calendar.YEAR)) {
                        return formatDateToString(DATE_FORMAT_8, it.time)
                    } else {
                        return formatDateToString(DATE_FORMAT_9, it.time)
                    }
                }
                return ""
            } catch (e: java.lang.Exception) {
                return ""
            }
        }

        fun getTimeRemaining(dateStr: String): String {
            val calendar = convertStringToCalendar(dateStr, DATE_FORMAT_1)
            calendar?.let {
                val timeDiff = it.timeInMillis - Calendar.getInstance().timeInMillis
                var totalMinute = TimeUnit.MILLISECONDS.toMinutes(timeDiff)
                if (totalMinute > 0) {
                    val hour = (totalMinute / 60).toInt()
                    val minute = (totalMinute % 60).toInt()
                    return if (hour > 0) {
                        "残り${hour}時間${minute}分"
                    } else {
                        "残り${minute}分"
                    }
                }
            }
            return ""
        }

        fun getAge(dob: Calendar): Int {
            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                age--
            }
            return age
        }
    }
}
