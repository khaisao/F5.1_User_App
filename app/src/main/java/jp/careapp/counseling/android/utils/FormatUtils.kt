package jp.careapp.counseling.android.utils

fun Int.formatDecimalSeparator(): String {
    return toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}