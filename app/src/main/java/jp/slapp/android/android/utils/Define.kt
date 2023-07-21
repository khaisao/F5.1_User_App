package jp.slapp.android.android.utils

import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import jp.slapp.android.BuildConfig
import jp.slapp.android.android.data.network.CategoryResponse
import jp.slapp.android.android.data.network.FreeTemplateResponse
import jp.slapp.android.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_1
import jp.slapp.android.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_2
import jp.slapp.android.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_3
import jp.slapp.android.android.utils.Define.Companion.IN_APP_PURCHASE_KEY_4

class Define {
    companion object {
        const val USER_BAD = 3
        const val USER_WITH_DRAWAL = 4
        const val NAME_MAX_LENGTH = 10

        // Insufficient points
        const val INSU_POINT = 0
        const val BUY_POINT_FIRST = 1
        const val BUY_POINT_UNDER_500 = 2
        const val BUY_POINT_CHAT_MESSAGE = 3

        // key of in app purchase
        const val IN_APP_PURCHASE_KEY_1: String = "sl_tier_3600"
        const val IN_APP_PURCHASE_KEY_2: String = "sl_tier_5800"
        const val IN_APP_PURCHASE_KEY_3: String = "sl_tier_10800"
        const val IN_APP_PURCHASE_KEY_4: String = "sl_tier_15800"

        // screen name
        const val SEND_MESSAGE_SCREEN = "SEND_MESSAGE_SCREEN"

        // webView
        const val TITLE_WEB_VIEW = "TITLE_WEB_VIEW"
        const val URL_WEB_VIEW = "URL_WEB_VIEW"
        const val URL_TROUBLE_LOGIN =
            "https://careapp.tayori.com/q/careapp-helpcenter/detail/228005"
        const val URL_TERMS = "https://web.careapp.jp/webview/terms.html"
        const val URL_PRIVACY = "https://web.careapp.jp/webview/privacy.html"
        const val URL_OPEN_FROM_HOME =
            "https://careapp.tayori.com/q/careapp-helpcenter/detail/234970"
        const val URL_ABOUT_CHARGES =
            "https://careapp.tayori.com/q/careapp-helpcenter/detail/225211"
        const val URL_HOW_TO_USE = "https://careapp.tayori.com/q/careapp-helpcenter/category/34544"
        const val URL_LEGAL = "http://web.careapp.jp/webview/legal.html"
        const val URL_TWITTER = "https://twitter.com/SPARKLIVE_JP"
        const val URL_SETTLEMENT = "https://web.careapp.jp/webview/fund_settlement.html"
        const val URL_FREE_POINT =
            "${BuildConfig.WEB_DOMAIN}/webview/member/freepoint/free-point.html"
        const val URL_PAY_TYPE_CREDIT =
            "${BuildConfig.WEB_DOMAIN}/payment/buy-point?kind=credit"
        const val URL_PAY_TYPE_BANK =
            "${BuildConfig.WEB_DOMAIN}/payment/buy-point?kind=bank"
        const val URL_STRIPE_BUY_POINT =
            "${BuildConfig.WEB_DOMAIN}/webview/member/purchase/stripe/buy-point-list.html"
        const val URL_BUY_POINT =
            "${BuildConfig.WEB_DOMAIN}/payment/methods?"
        const val URL_CREDIT_PURCHASE_CONFIRM =
            "${BuildConfig.WEB_DOMAIN}/webview/member/purchase/stripe/purchase-oneclick-confirm.html"
        const val URL_CONFIRM_POINT =
            "${BuildConfig.WEB_PURCHASE_DOMAIN}/flax/_app/AndroidSettlement.do"
        const val URL_BUY_POINT_CREDIT_CARD_CONFIRM_SEGMENT =
            "webview/member/purchase/stripe/purchase-confirm.html"
        const val URL_BUY_POINT_CREDIT_CARD_ONECLICK_CONFIRM_SEGMENT =
            "webview/member/purchase/stripe/purchase-oneclick-confirm.html"
        const val URL_LIST_POINT_CREDIT =
            "${BuildConfig.WEB_DOMAIN}/webview/member/purchase/stripe/buy-point-list.html"
        const val URL_LIVE_STREAM_POINT_PURCHASE = "${BuildConfig.WEB_DOMAIN}/popup/payment/credit"

        const val APP_CODE = "SparkLiveAndroid"
        const val PREFIX_CARE_APP = "sparklive://"
        const val CALL_BACK_BUY_POINT_GOOGLE_ = "sparklive://buypoint"
        const val CALL_BACK_BUY_POINT_CREDIT_CLOSE = "sparklive://close"
        const val CALL_BACK_BUY_POINT_PAYPAY = "careapp://mymenu"
        const val TYPE_ERROR = "ERROR"
        const val TYPE_MAINTENANCE = "MAINTENANCE"
        const val RESPONSE = 1

        const val MAX_LENGTH_CONTENT_NEW_QUESTION = 128
        const val LIMIT_CHAR_CONTENT = 30
        const val INVALID_VALUE_OF_CATEGORY = -1

        //Height search result
        const val DURATION_ALPHA = 800L
        const val TIME_SHOW_NOTIFICATION = 5000L
        const val COUNT_DOWN_INTERVAL = 5000L

        const val BLOG_ID = "id"
        const val TYPE_CONTACT = "type_contact"

        const val PRIVACY_POLICY_PATH = "support/privacy"
        const val TERM_PATH = "support/terms"

        // App mode
        const val NORMAL_MODE = 2
        const val REVIEW_MODE = 97

        // Live stream
        const val KEY_ALIAS = "member_key"

        const val FLAX_STATUS_PRIVATE = 0

        const val FLAX_STATUS_PREMIUM_PRIVATE = 1
    }

    class SearchCondition {
        companion object {
            const val DEFAULT = 0

            // Genres
            const val GENRES_6 = 6
            const val GENRES_7 = 7
            const val GENRES_9 = 9
            const val GENRES_10 = 10
            const val GENRES_11 = 11
            const val GENRES_12 = 12
            const val GENRES_32 = 32
            val GENRES_IDS = listOf(
                GENRES_6, GENRES_7, GENRES_9, GENRES_10,
                GENRES_11, GENRES_12, GENRES_32
            )

            // Gender
            const val MALE = 1
            const val FEMALE = 2

            // Ranking
            const val DIAMOND = 6
            const val BRONZE = 5
            const val PLATINUM = 4
            const val GOLD = 3
            const val SILVER = 2
            const val BASIC = 1
            val RANKING_IDS = listOf(
                DIAMOND, PLATINUM, GOLD, SILVER, BRONZE, BASIC
            )

            // Review
            const val HAVE_REVIEW = 1
            const val THREE_OR_MORE = 1
            const val FOUR_OR_MORE = 2

            // Status
            const val ONLY_DURING_RECEPTION = 1
        }
    }

    class Intent {
        companion object {
            const val BODY: String = "body"
            const val URL: String = "url"
            const val PERFORMER_CODE: String = "performer_code"
            const val DATA_FROM_NOTIFICATION: String = "DATA_FROM_NOTIFICATION"
            const val TYPE: String = "deeplink_kind"
            const val NOTICE = "notice"
            const val RANKING = "ranking"
            const val BUY_POINT = "buypoint"
            const val FREE_POINT = "freepoint"
            const val MESSAGE = "message"
            const val ONLINE = "online"
            const val COUNSELOR = "counselor"
            const val MY_MENU = "mymenu"
            const val LABO = "labo"
            const val PAYMENT_METHOD = "payment_method"
            const val CREDIT = "payment_01"
            const val BANK = "payment_04"
            const val BLOG = "blog"
            const val CONTACT = "contact"
            const val SETTING_NOTIFICATION = "setting_notification"
            const val NUMBER_UNREAD_MESSAGE = "NUMBER_UNREAD_MESSAGE"
            const val OPEN_DIRECT_FROM_NOTIFICATION: String = "OPEN_DIRECT_FROM_NOTIFICATION"
            const val ACTION_RECEIVE_NOTIFICATION: String = "ACTION_RECEIVE_NOTIFICATION"
            const val QUESTION_ID: String = "question_id"
            const val CODE: String = "code"
            const val OPEN_DIRECT: String = "OPEN_DIRECT"
            const val SENDER_CODE: String = "sender_code"
            const val SUPPORTER_CODE: String = "0"
            const val CODE_OPEN_CHAT_SUPPORTER = ""
            const val ONE_LINK = "sparklive.onelink.me"
            const val DEEP_LINK_VALUE = "deep_link_value"
            const val DEEP_LINK_URL_SCHEME = "af_dp"
            const val TOP = "top"
            const val MESSAGE_HISTORY = "message_history"
            const val RANKING_DAY = "ranking_day"
            const val RANKING_WEEK = "ranking_week"
            const val RANKING_MONTH = "ranking_month"
            const val RANKING_RECOMMEND = "ranking_recommend"
            const val WITHDRAW = "withdraw"
            const val PAYMENT_1 = "payment_1"
            const val PAYMENT_2 = "payment_2"
            const val FAQ = "faq"
        }
    }
}

class MODE_USER {
    companion object {
        // mode user 1. mode view 2. mode all
        const val MODE_ALL = 2
        const val MEMBER_APP_REVIEW_MODE = 97
    }
}

class ORDER {
    companion object {
        const val ASC = "asc"
        const val DESC = "desc"
    }
}

class SORT {
    companion object {
        const val CREATE_AT = "created_at"
        const val ANSWER_ACOUNT = "answer_count"
        const val LAST_ANSWERED_AT = "last_answered_at"
        const val DEFAULT = "こだわらない"
    }
}


class BUNDLE_KEY {
    companion object {
        const val NEW_QUESTION_SUCCESS: String = "NEW_QUESTION_SUCCESS"
        const val TITLE_TROUBLE_SHEET: String = "TITLE_TROUBLE_SHEET"
        const val CHOOSE_TYPE_TROUBLE: String = "CHOOSE_TYPE_TROUBLE"
        const val TYPE_BUY_POINT: String = "TYPE_BUY_POINT"
        const val ITEM_SELECT: String = "ITEM_SELECT"
        const val DESIRED_RESPONSE: String = "DESIRED_RESPONSE"
        const val PROFILE_SCREEN: String = "PROFILE_SCREEN"
        const val FIRST_CHAT: String = "FIRST_CHAT"
        const val LIST_USER_PROFILE: String = "LIST_USER_PROFILE"
        const val POSITION_SELECT: String = "POSITION_SELECT"
        const val TYPE_SEARCH: String = "TYPE_SEARCH"
        const val TITLE: String = "TITLE"
        const val HAVE_POINT: String = "HAVE_POINT"
        const val USER_PROFILE: String = "USER_PROFILE"
        const val USER_PROFILE_BOTTOM_SHEET: String = "USER_PROFILE_BOTTOM_SHEET"
        const val FIRST_BUY_POINT = "FIRST_BUY_POINT"
        const val NAME = "NAME"
        const val MAX_LENGTH_NAME = 10
        const val TYPE_RANKING = "TYPE_RANKING"
        const val TYPE_ONLINE_LIST_SCREEN = "TYPE_ONLINE_LIST_SCREEN"
        const val TYPE_MESSAGE_SCREEN = "TYPE_MESSAGE_LIST_SCREEN"
        const val GENRES_SELECTED = "GENRES_SELECTED"
        const val POSITION_USER: String = "POSITION_USER"
        const val LAB: String = "LAB"
        const val LAB_ID: String = "LAB_ID"
        const val IS_SHOW_FREE_MESS: String = "IS_SHOW_FREE_MESS"
        const val CALL_RESTRICTION: String = "CALL_RESTRICTION"
        const val DATA_RESPONSE_REGISTER: String = "DATA_RESPONSE_REGISTER"
        const val EMAIL_REGISTER: String = "EMAIL_REGISTER"

        // param
        const val PARAM_CATEGORY = "category"
        const val PARAM_START_AT_FROM = "start_at_from"
        const val PARAM_START_AT_TO = "start_at_to"
        const val PARAM_SORT = "sort"
        const val PARAM_ODER = "order"
        const val PARAM_PAY_OPEN = "pay_open"
        const val PARAM_SORT_2 = "sort2"
        const val PARAM_ODER_2 = "order2"
        const val PARAM_SORT_3 = "sort3"
        const val PARAM_ODER_3 = "order3"
        const val PARAM_GENRE: String = "PARAM_GENRE"
        const val PARAM_REGISTRATION: String = "PARAM_REGISTRATION"
        const val PARAM_PRESENCE_STATUS: String = "presence_status"
        const val PARAM_NAME: String = "name"
        const val PARAM_REVIEW_TOTAL_NUMBER: String = "review_total_number"
        const val PARAM_SEX: String = "sex"
        const val PARAM_GENRES: String = "genres"
        const val PARAM_STAGE: String = "stage"
        const val PARAM_REVIEW_AVERAGE: String = "review_average"
        const val PRESENCE_STATUS = "presence_status"
        const val LAST_AUTHENTICATION_DATE = "last_authentication_date"
        const val LAST_LOGIN_DATE = "last_login_date"
        const val REVIEW_TOTAL_NUMBER = "review_total_number"
        const val REVIEW_TOTAL_SCORE = "review_total_score"
        const val DESC = "desc"
        const val CHAT_STATUS = "chat_status"
        const val LIMIT = "limit"
        const val LIMIT_20 = 20
        const val LIMIT_30 = 30
        const val PAGE = "page"
        const val POINT = "point"
        const val INTERVAL = "interval"
        const val STATUS = "status"
        const val STATUS_ALL = "all"
        const val ANSWER_ID = "answer_id"
        const val IS_ENABLE = "is_enable"

        //online_user_list
        const val TYPE_ALL_PERFORMER = 0
        const val TYPE_ALL_PERFORMER_FOLLOW_HOME = 1
        const val TYPE_ALL_PERFORMER_FOLLOW_FAVORITE = 2
        const val TYPE_HISTORY = 3

        //message
        const val TYPE_ALL_MESSAGE = 0
        // ranking
        const val TYPE_DAILY = 0
        const val TYPE_WEEKLY = 1
        const val TYPE_MONTHLY = 2
        const val TYPE_RECOMMEND = 3
        const val EMAIL = "EMAIL"
        const val CODE_SCREEN = "CODE_SCREEN"
        const val OWNER_CODE = "ownerCode"
        const val MEMBER_CODE = "memberCode"
        const val APP_CODE = "appCode"
        const val RECEIPT = "receipt"
        const val SIGNATURE = "signature"
        const val SCREEN_NAME: String = "SCREEN_NAME"
        const val SEND_DATE = "send_date"
        const val PERFORMER_CODE = "performer_code"
        const val PERFORMER_NAME = "performer_name"
        const val PERFORMER_IMAGE = "performer_image"
        const val PERFORMER = "performer"
        const val IS_HIDDEN_OWNER_MAIL = "is_hidden_owner_mail"
        const val MULTIPLE_SELECTION = "multiple_selection"
        const val DATA = "DATA"
        const val DATA_PERFORMER = "DATA_PERFORMER"
        const val OS_ANDROID = "Android"
        const val FOCUS_EDITTEXT_EMAIL = "FOCUS_EDITTEXT_EMAIL"
        const val FOCUS_EDITTEXT_VERIFY = "FOCUS_EDITTEXT_VERIFY"

        // message
        const val MESSAGE_REVIEW = "@@COUNSELOR_REVIEW@@"
        const val SCREEN_TYPE = "SCREEN_TYPE"
        const val NEXT_TO_SCREEN = "NEXT_TO_SCREEN"
        const val CHAT_MESSAGE = "CHAT_MESSAGE"
        const val POSITION_TYPE_TROUBLE = "position"

        // REVIEW
        const val PERFORMER_REVIEW_STATUS = "performer_review_status"
        const val PERFORMER_REVIEW_POINT = "performer_review_point"
        const val PERFORMER_REVIEW_REVIEW = "performer_review_review"
        const val PERFORMER_REVIEW_ID = "performer_review_id"
        const val POINT_REVIEW = "POINT_REVIEW"
        const val THRESHOLD_SHOW_REVIEW_APP = 3
        const val PERFORMER_MSG_SHOW_REVIEW_APP = 10

        //search
        const val CRITERIA = "CRITERIA"

        // Banner
        const val BANNER_TYPE = "BANNER_TYPE"
        const val IS_OPEN_FROM_CALLING = "IS_OPEN_FROM_CALLING"

        const val IS_SHOW_TOOLBAR = "IS_SHOW_TOOLBAR"
        const val LIST_TEMPLATE = "LIST_TEMPLATE"
        const val BACK_TO_CHAT_BOX = "BACK_TO_CHAT_BOX"

        // Buy Point
        const val POINT_URL = "POINT_URL"

        // Live Stream
        const val FLAX_LOGIN_AUTH_RESPONSE = "FLAX_LOGIN_AUTH_RESPONSE"
        const val VIEW_STATUS = "VIEW_STATUS"
        const val CAMERA_SETTING = "CAMERA_SETTING"
        const val MIC_SETTING = "MIC_SETTING"
        const val ROOT_SCREEN = "ROOT_SCREEN"
        const val SCREEN_DETAIL = 0
        const val SCREEN_MESSAGE = 1
    }
}

class TYPE_DIALOG {
    companion object {
        const val DEFAULT = -1
        const val UN_AUTHEN = 1
        const val NO_INTERNET = 2
        const val BAD_REQUEST = 3
        const val CONNECTION_LOST = 4
        const val NOT_FOUND = 5
        const val GENERIC = 6
    }
}

enum class BUY_POINT(
    val id: String,
    val pointCount: Int,
    val pointBonus: Int,
    val money: String
) {
    FIST_BUY_COIN_1(
        IN_APP_PURCHASE_KEY_1,
        1800,
        0,
        " ¥3,600 ",
    ),
    FIST_BUY_COIN_2(
        IN_APP_PURCHASE_KEY_2,
        2900,
        0,
        " ¥5,800 ",
    ),
    FIST_BUY_COIN_3(
        IN_APP_PURCHASE_KEY_3,
        5900,
        0,
        " ¥10,800 ",
    ),
    FIST_BUY_COIN_4(
        IN_APP_PURCHASE_KEY_4,
        8900,
        0,
        "¥15,800",
    )
}

enum class BUY_POINT_RM(
    val id: String,
    val pointCount: Int,
    val pointBonus: Int,
    val money: String,
) {
    FIST_BUY_COIN_1(
        IN_APP_PURCHASE_KEY_1,
        1800,
        0,
        "¥3,600",
    ),
    FIST_BUY_COIN_2(
        IN_APP_PURCHASE_KEY_2,
        2900,
        0,
        "¥5,800",
    ),
    FIST_BUY_COIN_3(
        IN_APP_PURCHASE_KEY_3,
        5900,
        0,
        "¥10,800",
    ),
    FIST_BUY_COIN_4(
        IN_APP_PURCHASE_KEY_4,
        8900,
        0,
        "¥15,800",
    )
}

class LoadMoreState {
    companion object {
        const val SHOW_LOAD_MORE = 0
        const val HIDDEN_LOAD_MORE = 1
        const val DISABLE_LOAD_MORE = 2
        const val ENABLE_LOAD_MORE = 3
    }
}

object HistoryMessage {
    const val PAID_MESS = 1
    const val NON_PAID_MESS = 2
}

fun dummyCategoryData(): List<CategoryResponse> {
    return listOf(
        CategoryResponse(1, "恋愛", "恋愛で悩んでます。", false),
        CategoryResponse(5, "不倫・浮気", "不倫・浮気で悩んでます。", false),
        CategoryResponse(6, "復縁", "復縁で悩んでます。", true),
        CategoryResponse(7, "夫婦関係", "夫婦関係で悩んでます。", true),
        CategoryResponse(8, "家庭・夫婦", "家庭・夫婦で悩んでます。", false),
        CategoryResponse(9, "不倫", "不倫で悩んでます。", true),
        CategoryResponse(10, "浮気", "浮気で悩んでます。", true),
        CategoryResponse(11, "離婚", "離婚で悩んでます。", true),
        CategoryResponse(12, "片思い", "片思いで悩んでます。", true),
        CategoryResponse(13, "仕事・キャリア", "仕事・キャリアで悩んでます。", false),
        CategoryResponse(20, "心・対人関係", "心・対人関係で悩んでます。", false),
        CategoryResponse(25, "体・性", "体・性で悩んでます。", false),
        CategoryResponse(28, "ダイエット・美容", "ダイエット・美容で悩んでます。", false),
        CategoryResponse(32, "その他", "悩んでます。\\n(その他)", true)
    )
}

fun dummyFreeTemplateData(): List<FreeTemplateResponse> {
    return listOf(
        FreeTemplateResponse(1, "【無料】よろしくお願いします。"),
        FreeTemplateResponse(2, "【無料】前回の相談の続きからお願いします。"),
        FreeTemplateResponse(3, "【無料】ありがとうございます。"),
        FreeTemplateResponse(4, "【無料】通話鑑定お願いします。"),
        FreeTemplateResponse(5, "【無料】鑑定お願いします。"),
    )
}

enum class Position {
    TOP,
    DOWN
}

enum class StateView {
    SHOWING_TO_UP,  //dang anim vao vi tri o tren.
    SHOW_UP,        //da anim vao vi tri tren
    MOVE_UP_TO_DOWN,//dang o tren di chuyen xuong duoi
    SHOW_DOWN,      //da anim vao vi tri duoi
    SHOWING_TO_DOWN,//dang anim vao vi tri o duoi
    HIDING,         //dang an
    HIDE            //chua hien thi
}

class NotificationView(
    var state: StateView,
    var notificationView: View,
    var tvContent: TextView,
    var ivCloseView: View,
    var timerView: CountDownTimer? = null,
    var itemCode: String = ""
)

object GenderId {
    const val DEFAULT_ID = 0
    const val MALE_ID = 1
    const val FEMALE_ID = 2
    const val OTHER_ID = 3
}

object CallStatus {
    const val OFFLINE = 0
    const val ONLINE = 1
    const val INCOMING_CALL = 2
    const val DEFAULT_CALL_STATUS = ONLINE
}

object ChatStatus {
    const val OFFLINE = 0
    const val WAITING = 1
    const val CHATTING = 2
    const val TWO_SHOT_CHAT = 3
    const val DEFAULT_CHAT_STATUS = OFFLINE
}

object CallRestriction {
    const val POSSIBLE = 1
}

object SocketInfo {
    // HEADER
    const val HEADER_PROTOCOL = "Sec-WebSocket-Protocol"
    const val HEADER_PROTOCOL_VALUE = "notify"
    const val HEADER_ORIGIN = "Origin"

    const val URL_GET_CALL_CONFIG =
        BuildConfig.URL_GET_CALL_CONFIG + "flax/fss/${BuildConfig.OWNER_CODE}/memberConfig.json"

    // AUTH
    const val AUTH_OWN_NAME = "ownerName"
    const val AUTH_OWN_NAME_VALUE = "careapp"
    const val AUTH_USER = "user"
    const val AUTH_USER_VALUE = "member"
    const val AUTH_TOKEN = "token"

    // Key
    const val KEY_ACTION = "action"
    const val KEY_RESULT = "result"
    const val KEY_ERROR = "error"
    const val KEY_CHAT = "chat"
    const val KEY_HANDLE = "handle"
    const val KEY_USER_CODE = "userCode"
    const val KEY_USER_TYPE = "userType"
    const val KEY_WHISPER = "whisper"
    const val KEY_TWO_SHOT = "twoshot"
    const val KEY_TWO_SHOT_CANCEL = "2shotCancel"
    const val KEY_TWO_SHOT_CANCEL_2 = "twoshotCancel"
    const val KEY_SESSION_CODE = "sessionCode"
    const val KEY_MEMBER_CODE = "memberCode"
    const val KEY_PERFORMER_CODE = "performerCode"
    const val KEY_MEDIA_SERVER_OWNER_CODE = "mediaServerOwnerCode"
    const val KEY_MEDIA_SERVER = "mediaServer"
    const val KEY_PERFORMER_THUMB_IMAGE = "performerThumbnailImage"
    const val KEY_IS_NEED_CALL = "isNeedCall"
    const val KEY_SHOT_STATUS = "shotStatus"
    const val KEY_MESSAGE_TYPE = "messageType"
    const val KEY_MSG = "msg"
    const val KEY_COLOR = "color"
    const val KEY_KIND = "kind"
    const val VIDEO_KIND = "video"
    const val AUDIO_KIND = "audio"

    // Action
    const val ACTION_READ = "read"
    const val ACTION_SEND = "send"
    const val ACTION_CHANGE_STATUS = "changeStatus"
    const val ACTION_CHANGE_TWO_SHOT_STATUS = "ChangeTwoShotStatus"
    const val ACTION_WRITE = "Write"
    const val ACTION_MESSAGE = "message"
    const val ACTION_WHISPER = "Whisper"
    const val ACTION_CALL = "CALL"
    const val ACTION_LOGIN_REQUEST = "LoginRequest"
    const val ACTION_PERFORMER_LOGIN = "PerformerLogin"
    const val ACTION_LOGIN = "Login"
    const val ACTION_PERFORMER_RESPONSE = "PerformerResponse"
    const val ACTION_CANCEL_CALL = "CancelCall"
    const val ACTION_TWO_SHOT_REQUEST = "2shotRequest"
    const val ACTION_ASK_TWO_SHOT = "AskTwoShot"
    const val ACTION_CANCEL_TWO_SHOT = "CancelTwoShot"
    const val ACTION_DESTROY_TWO_SHOT = "2shotDestroy"
    const val ACTION_CHAT_LOG = "ChatLog"
    const val ACTION_RELOAD = "Reload"
    const val ACTION_CHANGE_CHAT_STATUS = "ChangeChatStatus"
    const val RESULT_NG = "NG"
    const val RESULT_OK = "OK"
    const val RESULT_NONE = "NONE"
    const val MESSAGE_COLOR = "#FFFFFF"

}

object MenuItemType {
    const val TITLE = 1
    const val FIELD = 2
}

object RMCallStatus {
    const val OFFLINE = 0
    const val WAITING_OFFLINE = 1
    const val INCOMING_CALL = 2
}

object RMPresenceStatus {
    const val AWAY = 0
    const val ACCEPTING = 1
}

sealed class ActionState {
    class SaveProfileMessageSuccess(val isSuccess: Boolean) : ActionState()
    class BlockUserSuccess(val isSuccess: Boolean) : ActionState()
    class SendReportSuccess(val isSuccess: Boolean) : ActionState()
    class SendContactSuccess(val isSuccess: Boolean) : ActionState()
}

fun configUrlBuyPoints(token: String): String {
    return "${Define.URL_BUY_POINT}token=${token}&&platform=android"
}

const val CONTACT_US_MAIL = "CONTACT_US_MAIL"
const val CONTACT_US_MODE = "CONTACT_US_MODE"

object ContactUsMode {
    const val CONTACT_WITH_MAIL = 0
    const val CONTACT_WITHOUT_MAIL = 1
}