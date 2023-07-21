package jp.careapp.core.utils

import androidx.annotation.StringRes
import jp.careapp.core.R

object Constants {
    const val PREF_FILE_NAME = "Picking.Preferences"
    const val DEFAULT_TIMEOUT = 30
    const val DURATION_TIME_CLICKABLE = 500

    // common dialog
    const val TYPE_DEFAULT = -1
    // splash
    const val DURATION_TIME_SPLASH = 300
    const val DURATION_ANIM_CHANGE_SCREEN = 300L
    const val TYPE_SETTING_TITLE = 1
    const val TYPE_SETTING_CONTENT = 0

    object NetworkRequestCode {
        const val REQUEST_CODE_200 = 200 // normal
        const val REQUEST_CODE_400 = 400 // parameter error
        const val REQUEST_CODE_401 = 401 // unauthorized error
        const val REQUEST_CODE_403 = 403
        const val REQUEST_CODE_404 = 404 // No data error
        const val REQUEST_CODE_500 = 500 // system error
    }

    enum class Settings {
        SUPPORT(
            R.string.support,
            TYPE_SETTING_TITLE
        ),

        //        SUPPORT_INFO(R.string.support_information) ,
        HELP(R.string.help),
        GUID_USE(R.string.guid_to_use_app),
        PRIVACY_POLICY(R.string.privacy_policy),
        APP_INFO(
            R.string.app_info,
            TYPE_SETTING_TITLE
        ),
        VERSION(R.string.version),
        TERM_SERVICE(R.string.term_of_service);

        @StringRes
        var stringId: Int
        var typeSetting: Int

        constructor(stringId: Int) {
            this.stringId = stringId
            typeSetting = TYPE_SETTING_CONTENT
        }

        constructor(stringId: Int, type: Int) {
            this.stringId = stringId

            typeSetting = type
        }
    }

    enum class Gender(val index: Int, val label: String) {
        MALE(1, "男性"),
        FEMALE(2, "女性"),
        OTHER(3, "その他")
    }

    enum class MemberStatus(val index: Int) {
        NOMARL(0), // registered
        UNREGISTED(1),
        BAD(2),
        WITHDRAWAL(3),
        STAFF(4),
    }

    enum class OptionSearch {
        CATEGORY,
        GENDER,
        CONSULTANT_RANK,
        REVIEW_AVERAGE,
    }
}
