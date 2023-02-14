package jp.careapp.counseling.android.data.pref

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.careapp.core.utils.Constants
import jp.careapp.counseling.android.data.model.CreditItem
import jp.careapp.counseling.android.data.model.HistorySelection
import jp.careapp.counseling.android.data.network.*
import jp.careapp.counseling.android.utils.MODE_USER
import jp.careapp.counseling.android.utils.SignedUpStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext context: Context
) : RxPreferences {

    companion object {
        const val PARAM_BEARER = "Bearer "

        const val PREF_PARAM_ACCESS_TOKEN = "PREF_PARAM_ACCESS_TOKEN"
        const val PREF_PARAM_EMAIL_USER = "PREF_PARAM_EMAIL_USER"
        const val PREF_PARAM_TOKEN_EXPIRE = "PREF_PARAM_TOKEN_EXPIRE"
        const val PREF_PARAM_PASSWORD = "PREF_PARAM_PASSWORD"
        const val PREF_PARAM_USER_INFO = "PREF_PARAM_USER_INFO"
        const val PREF_KEY_IS_FIRST_TIME_USE = "PREF_KEY_IS_FIRST_TIME_USE"
        const val PREF_KEY_IS_FIRST_TIME_USE_LAB = "PREF_KEY_IS_FIRST_TIME_USE_LAB"
        const val PREF_NEW_LAST_VIEW_DATE_TIME = "PREF_NEW_LAST_VIEW_DATE_TIME"
        const val PREF_PARAM_MEMBER_CODE = "PREF_PARAM_MEMBER_CODE"
        const val PREF_KEY_POINT = "PREF_KEY_POINT"
        const val PREF_KEY_BUY_TIME = "PREF_KEY_BUY_TIME"
        const val PREF_KEY_DEVICE_TOKEN = "PREF_KEY_DEVICE_TOKEN"
        const val PREF_KEY_UNREAD_MESSAGE = "PREF_KEY_UNREAD_MESSAGE"
        const val PREF_KEY_DEEP_LINK = "PREF_KEY_DEEP_LINK"
        const val PREF_KEY_FIRST_SHOW_GUIDE_USE_USER_PROFILE =
            "PREF_KEY_FIRST_SHOW_GUIDE_USE_USER_PROFILE"
        const val PREF_KEY_TROUBLE_INFO = "PREF_KEY_TROUBLE_INFO"
        const val PREF_KEY_CATEGORY = "PREF_KEY_CATEGORY"
        const val PREF_KEY_TEMPLATE = "PREF_KEY_TEMPLATE"
        const val PREF_KEY_SELECTION = "PREF_KEY_SELECTION"
        const val PREF_KEY_THE_REGISTER = "KEY_THE_REGISTER"
        const val PREF_FIRST_BUY_CREDIT = "FIRST_BUY_CREDIT"
        const val PREF_KEY_IS_FULL_MODE = "PREF_KEY_IS_FULL_MODE"
        const val PREF_KEY_IS_FIRST_REVIEW = "PREF_KEY_IS_FIRST_REVIEW"
        const val PREF_SIGNED_UP_STATUS = "PREF_SIGNED_UP_STATUS"
        const val PREF_DESIRED_RESPONSE = "PREF_DESIRED_RESPONSE"
        const val PREF_GENRE_SELECTED = "PREF_GENRE_SELECTED"
        const val PREF_KEY_LAST_BUY_LOG = "PREF_KEY_LAST_BUY_LOG"
        const val PREF_KEY_CREDIT_PRICES = "PREF_KEY_CREDIT_PRICES"
        const val PREF_KEY_CALL_TOKEN = "PREF_KEY_CALL_TOKEN"
        const val PREF_KEY_CONFIG_CALL = "PREF_KEY_CONFIG_CALL"
    }

    private val mPrefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREF_FILE_NAME,
        Context.MODE_PRIVATE
    )

    override fun put(key: String, value: String) {
        val editor: SharedPreferences.Editor = mPrefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun get(key: String): String? {
        return mPrefs.getString(key, "")
    }

    override fun clear() {
        val editor: SharedPreferences.Editor = mPrefs.edit()
        editor.clear()
        editor.apply()
    }

    override fun remove(key: String) {
        val editor: SharedPreferences.Editor = mPrefs.edit()
        editor.remove(key)
        editor.apply()
    }

    override fun getToken() = get(PREF_PARAM_ACCESS_TOKEN)

    override fun setUserToken(userToken: String) =
        put(PREF_PARAM_ACCESS_TOKEN, PARAM_BEARER + userToken)

    override fun logout() {
        remove(PREF_PARAM_USER_INFO)
        remove(PREF_KEY_UNREAD_MESSAGE)
    }

    override fun saveEmail(email: String) = put(PREF_PARAM_EMAIL_USER, email)

    override fun getEmail() = get(PREF_PARAM_EMAIL_USER)

    override fun saveUserInfor(
        token: String,
        tokenExpire: String,
        passWord: String,
        memberCode: String
    ) {
        mPrefs.edit().putString(PREF_PARAM_ACCESS_TOKEN, PARAM_BEARER + token).apply()
        mPrefs.edit().putString(PREF_PARAM_TOKEN_EXPIRE, tokenExpire).apply()
        if (passWord.isNotEmpty()) {
            mPrefs.edit().putString(PREF_PARAM_PASSWORD, passWord).apply()
        }
        if (!memberCode.isNullOrEmpty()) {
            mPrefs.edit().putString(PREF_PARAM_MEMBER_CODE, memberCode).apply()
        }
    }

    override fun getTokenExpire() = get(PREF_PARAM_TOKEN_EXPIRE)

    override fun getPassword() = get(PREF_PARAM_PASSWORD)

    override fun isFirstTimeUse(): Boolean {
        return mPrefs.getBoolean(PREF_KEY_IS_FIRST_TIME_USE, true)
    }

    override fun isFirstTimeUseLab(): Boolean {
        return mPrefs.getBoolean(PREF_KEY_IS_FIRST_TIME_USE_LAB, false)
    }

    override fun setIsFirstTimeUse(state: Boolean) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_FIRST_TIME_USE, state).apply()
    }

    override fun setIsFirstTimeUseLab(state: Boolean) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_FIRST_TIME_USE_LAB, state).apply()
    }

    override fun getMemberCode() = get(PREF_PARAM_MEMBER_CODE)

    override fun saveNewLastViewDateTime(dateTime: String) {
        mPrefs.edit().putString(PREF_NEW_LAST_VIEW_DATE_TIME, dateTime).apply()
    }

    override fun getNewLastViewDateTime() = get(PREF_NEW_LAST_VIEW_DATE_TIME)

    override fun saveMemberInfo(memberResponse: MemberResponse) {
        mPrefs.edit().putInt(PREF_KEY_POINT, memberResponse.point).apply()
        mPrefs.edit().putLong(PREF_KEY_BUY_TIME, memberResponse.buyTime).apply()
        mPrefs.edit().putBoolean(PREF_FIRST_BUY_CREDIT, memberResponse.firstBuyCredit).apply()
        mPrefs.edit()
            .putBoolean(PREF_KEY_IS_FULL_MODE, memberResponse.disPlay == MODE_USER.MODE_ALL).apply()
        mPrefs.edit()
            .putInt(PREF_SIGNED_UP_STATUS, memberResponse.signupStatus ?: SignedUpStatus.UNKNOWN)
            .apply()
        put(PREF_KEY_LAST_BUY_LOG, Gson().toJson(memberResponse.lastBuyLog))
        saveTroubleSheet(memberResponse.troubleSheetResponse)
    }

    override fun saveTroubleSheet(troubleSheetResponse: TroubleSheetResponse) {
        val gson = Gson()
        val json = gson.toJson(troubleSheetResponse)
        mPrefs.edit().putString(PREF_KEY_TROUBLE_INFO, json).apply()
    }

    override fun getPoint(): Int {
        return mPrefs.getInt(PREF_KEY_POINT, 0)
    }

    override fun setPoint(point: Int) {
        mPrefs.edit().putInt(PREF_KEY_POINT, point).apply()
    }

    override fun getTimeBuy(): Long {
        return mPrefs.getLong(PREF_KEY_BUY_TIME, 0)
    }

    override fun getFirstBuyCredit(): Boolean {
        return mPrefs.getBoolean(PREF_FIRST_BUY_CREDIT, true)
    }

    override fun isFullMode(): Boolean {
        return mPrefs.getBoolean(PREF_KEY_IS_FULL_MODE, false)
    }

    override fun saveDeviceToken(deviceToken: String) {
        mPrefs.edit().putString(PREF_KEY_DEVICE_TOKEN, deviceToken).apply()
    }

    override fun getDeviceToken() = get(PREF_KEY_DEVICE_TOKEN)

    override fun saveNumberUnreadMessage(number: Int) {
        mPrefs.edit().putInt(PREF_KEY_UNREAD_MESSAGE, number).apply()
    }

    override fun getNumberUnreadMessage(): Int {
        return mPrefs.getInt(PREF_KEY_UNREAD_MESSAGE, 0)
    }

    override fun saveDeepLink(deepLink: String) {
        mPrefs.edit().putString(PREF_KEY_DEEP_LINK, deepLink).apply()
    }

    override fun getDeepLink() = get(PREF_KEY_DEEP_LINK)

    override fun isFirstShowGuideUser(): Boolean =
        mPrefs.getBoolean(PREF_KEY_FIRST_SHOW_GUIDE_USE_USER_PROFILE, true)

    override fun saveFirstShowGuideUser(isFirstUse: Boolean) {
        mPrefs.edit().putBoolean(PREF_KEY_FIRST_SHOW_GUIDE_USE_USER_PROFILE, isFirstUse).apply()
    }

    override fun getTroubleInfo(): TroubleSheetResponse? {
        var troubleSheet: TroubleSheetResponse? = null
        try {
            val troubleJson = mPrefs.getString(PREF_KEY_TROUBLE_INFO, "")
            val gson = Gson()
            troubleSheet = gson.fromJson(troubleJson, TroubleSheetResponse::class.java)
        } catch (e: Exception) {
        }
        return troubleSheet
    }

    override fun saveListCategory(listCategory: List<CategoryResponse>) {
        val gson = Gson()
        val json = gson.toJson(listCategory)
        mPrefs.edit().putString(PREF_KEY_CATEGORY, json).apply()
    }

    override fun getListCategory(): List<CategoryResponse>? {
        var list: List<CategoryResponse>? = null
        try {
            val categoryJson = mPrefs.getString(PREF_KEY_CATEGORY, "")
            val gson = Gson()
            val myType = object : TypeToken<List<CategoryResponse>>() {}.type
            list = gson.fromJson(categoryJson, myType)
        } catch (e: Exception) {
        }
        return list
    }

    override fun saveListTemplate(listCategory: List<FreeTemplateResponse>) {
        val gson = Gson()
        val json = gson.toJson(listCategory)
        mPrefs.edit().putString(PREF_KEY_TEMPLATE, json).apply()
    }

    override fun getListTemplate(): List<FreeTemplateResponse>? {
        var list: List<FreeTemplateResponse>? = null
        try {
            val templateJson = mPrefs.getString(PREF_KEY_TEMPLATE, "")
            val gson = Gson()
            val myType = object : TypeToken<List<FreeTemplateResponse>>() {}.type
            list = gson.fromJson(templateJson, myType)
        } catch (e: Exception) {
        }
        return list
    }

    override fun saveHistorySearchSelection(historySelection: HistorySelection) {
        val gson = Gson()
        val json = gson.toJson(historySelection)
        mPrefs.edit().putString(PREF_KEY_SELECTION, json).apply()
    }

    override fun getHistorySearchSelection(): HistorySelection {
        var historySelection = HistorySelection()
        try {
            val historySelectionJson = mPrefs.getString(PREF_KEY_SELECTION, "")
            val gson = Gson()
            val myType = object : TypeToken<HistorySelection>() {}.type
            historySelection = gson.fromJson(historySelectionJson, myType)
        } catch (e: Exception) {
        }
        return historySelection
    }

    @SuppressLint("CommitPrefEdits")
    override fun setFirstRegister(isTheFirst: Boolean) {
        mPrefs.edit().putBoolean(PREF_KEY_THE_REGISTER, isTheFirst).apply()
    }

    override fun getFirstRegister(): Boolean = mPrefs.getBoolean(PREF_KEY_THE_REGISTER, false)

    override fun saveFirstReview(isTheFirst: Boolean, memberCode: String?, performerCode: String) {
        val key: String = PREF_KEY_IS_FIRST_REVIEW + "_" + memberCode + "_" + performerCode
        mPrefs.edit().putBoolean(key, isTheFirst).apply()
    }

    override fun isFirstReview(memberCode: String?, performerCode: String): Boolean {
        val key: String = PREF_KEY_IS_FIRST_REVIEW + "_" + memberCode + "_" + performerCode
        return mPrefs.getBoolean(key, false)
    }

    override fun getSignedUpStatus(): Int =
        mPrefs.getInt(PREF_SIGNED_UP_STATUS, SignedUpStatus.UNKNOWN)


    override fun setSignedUpStatus(status: Int) {
        mPrefs.edit().putInt(PREF_SIGNED_UP_STATUS, status).apply()
    }

    override fun getDesiredResponse(): Int =
        mPrefs.getInt(PREF_DESIRED_RESPONSE, 0)

    override fun setDesiredResponse(status: Int) {
        mPrefs.edit().putInt(PREF_DESIRED_RESPONSE, status).apply()
    }

    override fun getGenre(): Int = mPrefs.getInt(PREF_GENRE_SELECTED, -1)

    override fun setGenre(id: Int) {
        mPrefs.edit().putInt(PREF_GENRE_SELECTED, id).apply()
    }

    override fun getLastBuyLog(): LastBuyLog? {
        return try {
            return Gson().fromJson(get(PREF_KEY_LAST_BUY_LOG), LastBuyLog::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    override fun saveCreditPrices(data: List<CreditItem>) {
        val dataStr = Gson().toJson(data)
        put(PREF_KEY_CREDIT_PRICES, dataStr)
    }

    override fun getCreditPrices(): List<CreditItem> {
        return try {
            val dataStr = get(PREF_KEY_CREDIT_PRICES)
            val type = object : TypeToken<List<CreditItem>>() {}.type
            return Gson().fromJson(dataStr, type)
        } catch (e: JsonSyntaxException) {
            emptyList()
        }
    }

    override fun setCallToken(token: String) {
        put(PREF_KEY_CALL_TOKEN, token)
    }

    override fun getCallToken(): String {
        return get(PREF_KEY_CALL_TOKEN) ?: ""
    }

    override fun saveConfigCall(config: ConfigCallResponse) {
        put(PREF_KEY_CONFIG_CALL, Gson().toJson(config))
    }

    override fun getConfigCall(): ConfigCallResponse {
        return try {
            Gson().fromJson(get(PREF_KEY_CONFIG_CALL), ConfigCallResponse::class.java)
        } catch (e: Exception) {
            defaultConfigCall()
        }
    }
}
