package jp.careapp.core.utils

import android.text.TextUtils

class ConverterUtils {
    companion object {
        fun isContainEmoiji(content: String): Boolean {
            if (TextUtils.isEmpty(content)) {
                return false
            }
            for (i in content.length - 1 downTo 0) {
                val cp: Int = Character.codePointAt(content, i)
                when (cp) {
                    in 0x1F600..0x1F64F,
                    in 0x1F300..0x1F5FF,
                    in 0x1F680..0x1F6FF,
                    in 0x2600..0x26FF,
                    in 0x2700..0x27BF,
                    in 0xFE00..0xFE0F,
                    in 0x1F900..0x1F9FF,
                    in 55356..65039,
                    in 8400..8447 -> {
                        return true
                    }
                }
            }
            return false
        }

        fun Int?.defaultValue(): Int {
            return this ?: 0
        }

        fun String?.defaultValue(): String {
            return this ?: ""
        }
    }
}
