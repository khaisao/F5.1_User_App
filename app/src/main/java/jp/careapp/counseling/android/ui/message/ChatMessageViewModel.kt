package jp.careapp.counseling.android.ui.message

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.DateUtil
import jp.careapp.counseling.android.AppApplication
import jp.careapp.counseling.android.data.event.EventBusAction
import jp.careapp.counseling.android.data.model.message.*
import jp.careapp.counseling.android.data.model.user_profile.ActionLoadProfile
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.ui.chatList.ChatListViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.PERFORMER_MSG_SHOW_REVIEW_APP
import jp.careapp.counseling.android.utils.dummyFreeTemplateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import me.leolin.shortcutbadger.ShortcutBadger
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChatMessageViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) :
    BaseViewModel() {
    var page = 1

    // result handle show/hide/disable loadmore
    var hiddenLoadMoreHandle = MutableLiveData<Int>()

    // result list message
    var messageResult = MutableLiveData<DataMessage>()

    // user detail info chating
    val userProfileResult = MutableLiveData<ConsultantResponse>()

    // result when send 1 message
    val sendMessageResult = MutableLiveData<SendMessageResponse?>()
    var subtractPoint = MutableLiveData<Boolean>()

    // listten result of socket
    val responseSocket = MutableLiveData<SocketActionSend>()

    // result load info of self when update point
    val memberInFoResult = MutableLiveData<MemberResponse?>()

    // result when first send message,if send first success,remove first message
    val isCloseFirstMessageInLocal = MutableLiveData<Boolean>()

    // hash map contain list message fillter to date
    var dataMessageMap = LinkedHashMap<String, MutableList<BaseMessageResponse>>()

    var openPayMessageResult = MutableLiveData<SendMessageResponse>()

    // result when load transmission message
    val isHasTransmissionMessage = MutableLiveData<Boolean>()

    // result when load transmission message
    val isEnoughMessageForReview = MutableLiveData<Boolean>()

    var isLoadMessageAfterSend = false
    var isMessageFromSocket = false

    private var activity: Activity? = null
    private var performerCode = ""

    val isEnableButtonSend = MutableLiveData<Boolean>()

    init {
        saveListFreeTemplate()
    }

    fun loadMessage(
        activity: Activity,
        performerCode: String,
        isLoadmore: Boolean,
        isShowLoading: Boolean = true
    ) {
        this.isLoadMessageAfterSend = false
        this.activity = activity
        this.performerCode = performerCode
        if (!isLoadmore) {
            page = 1
            dataMessageMap.clear()
        }
        var params = HashMap<String, Any>()
        if (performerCode != "")
            params.put(BUNDLE_KEY.PERFORMER_CODE, performerCode)
        else
            params.put(BUNDLE_KEY.PERFORMER_CODE, "0")
        params.put(BUNDLE_KEY.PAGE, page)
        params.put(BUNDLE_KEY.LIMIT, BUNDLE_KEY.LIMIT_20)
        params.put(BUNDLE_KEY.PARAM_SORT, BUNDLE_KEY.SEND_DATE)
        params.put(BUNDLE_KEY.PARAM_ODER, BUNDLE_KEY.DESC)
        viewModelScope.launch {
            if (!isLoadmore && isShowLoading) {
                isLoading.value = true
            }
            try {
                var responseMessage = apiInterface.loadMessage(params)
                if (responseMessage.errors.isEmpty()) {
                    if (responseMessage.pagination.total > PERFORMER_MSG_SHOW_REVIEW_APP) {
                        isEnoughMessageForReview.value = true;
                    }
                    if (!responseMessage.dataResponse.isNullOrEmpty()) {
                        hiddenLoadMoreHandle.value = ChatListViewModel.ENABLE_LOAD_MORE
                        page = page + 1
                        val data = responseMessage.dataResponse
                        data.forEach {
                            val time = DateUtil.convertStringToDateString(
                                it.sendDate,
                                DateUtil.DATE_FORMAT_1,
                                DateUtil.DATE_FORMAT_3
                            )
                            if (dataMessageMap.containsKey(time)) {
                                dataMessageMap.get(time)?.add(it)
                            } else {
                                var listMessage = mutableListOf<BaseMessageResponse>()
                                listMessage.add(it)
                                dataMessageMap.put(time, listMessage)
                            }
                        }
                        var messageResultConvert = mutableListOf<BaseMessageResponse>()
                        for ((key, value) in dataMessageMap) {
                            messageResultConvert.addAll(value)
                            messageResultConvert.add(
                                TimeMessageResponse(
                                    time = key
                                )
                            )
                        }
                        messageResult.value =
                            DataMessage(isLoadmore, messageResultConvert.reversed())
                    } else {
                        if (isLoadmore) {
                            hiddenLoadMoreHandle.value = ChatListViewModel.DISABLE_LOAD_MORE
                        } else {
                            messageResult.value =
                                DataMessage(isLoadmore, mutableListOf())
                        }
                    }
                }
            } catch (throwable: Throwable) {
                if (!isLoadmore && isShowLoading) {
                    isLoading.value = false
                }
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        loadMessage(activity, performerCode, isLoadmore, isShowLoading)
                    }
                )
            } finally {
                if (isLoadmore) {
                    hiddenLoadMoreHandle.value = ChatListViewModel.HIDDEN_LOAD_MORE
                } else {
                    isLoading.value = false
                }
                AppApplication.getAppContext()?.let { loadUnreadCount(it) }
            }
        }
    }

    fun loadTransmissionMessage(activity: Activity, performerCode: String, isShowLoading: Boolean) {
        val params = HashMap<String, Any>()
        if (performerCode.isNotEmpty())
            params[BUNDLE_KEY.PERFORMER_CODE] = performerCode
        else
            params[BUNDLE_KEY.PERFORMER_CODE] = "0"
        params[BUNDLE_KEY.PAGE] = 1
        params[BUNDLE_KEY.LIMIT] = BUNDLE_KEY.LIMIT_20
        params[BUNDLE_KEY.PARAM_SORT] = BUNDLE_KEY.SEND_DATE
        params[BUNDLE_KEY.PARAM_ODER] = BUNDLE_KEY.DESC
        viewModelScope.launch {
            if (isShowLoading) {
                isLoading.value = true
            }
            try {
                val responseMessage = apiInterface.loadTransmissionMessage(params)
                val transmissionMessageCount = responseMessage.pagination.total
                if (transmissionMessageCount > 0) {
                    isHasTransmissionMessage.value = true
                }
            } catch (throwable: Throwable) {
                if (isShowLoading) {
                    isLoading.value = false
                }
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        loadTransmissionMessage(activity, performerCode, isShowLoading)
                    }
                )
            } finally {
                isLoading.value = false
            }
        }
    }

    fun openPayMessage(activity: Activity, mailCode: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.openPayMessage(mailCode)
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse?.let { data ->
                            openPayMessageResult.value = data
                            subtractPoint.value = true
                        }
                    }
                }
            } catch (throwable: Throwable) {
                isLoading.value = false
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        openPayMessage(activity, performerCode)
                    }
                )
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loadMessageAfterSend(activity: Activity, performerCode: String) {
        this.isLoadMessageAfterSend = true
        var params = HashMap<String, Any>()
        params.put(BUNDLE_KEY.PERFORMER_CODE, performerCode)
        params.put(BUNDLE_KEY.LIMIT, 1)
        params.put(BUNDLE_KEY.PARAM_SORT, BUNDLE_KEY.SEND_DATE)
        params.put(BUNDLE_KEY.PARAM_ODER, BUNDLE_KEY.DESC)
        viewModelScope.launch {
            try {
                var responseMessage = apiInterface.loadMessage(params)
                if (responseMessage.errors.isEmpty()) {
                    if (responseMessage.pagination.total >= PERFORMER_MSG_SHOW_REVIEW_APP) {
                        isEnoughMessageForReview.value = true;
                    }
                    if (!responseMessage.dataResponse.isNullOrEmpty()) {
                        val data = responseMessage.dataResponse
                        data.forEach {
                            val time = DateUtil.convertStringToDateString(
                                it.sendDate,
                                DateUtil.DATE_FORMAT_1,
                                DateUtil.DATE_FORMAT_3
                            )
                            if (dataMessageMap.containsKey(time)) {
                                dataMessageMap.get(time)?.add(0, it)
                            } else {
                                val newMap = dataMessageMap.clone()
                                dataMessageMap.clear()
                                var listMessage = mutableListOf<BaseMessageResponse>()
                                listMessage.add(0, it)
                                dataMessageMap.put(time, listMessage)
                                dataMessageMap.putAll(newMap as LinkedHashMap<String, MutableList<BaseMessageResponse>>)
                            }
                        }
                        var messageResultConvert = mutableListOf<BaseMessageResponse>()
                        for ((key, value) in dataMessageMap) {
                            messageResultConvert.addAll(value)
                            messageResultConvert.add(
                                TimeMessageResponse(
                                    key
                                )
                            )
                        }
                        messageResult.postValue(
                            DataMessage(false, messageResultConvert.reversed())
                        )
                    }
                }
            } catch (throwable: Throwable) {
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        loadMessageAfterSend(activity, performerCode)
                    }
                )
            }
        }
    }

    fun sendMessage(messageRequest: MessageRequest, activity: Activity) {
        viewModelScope.launch {
            try {
                isEnableButtonSend.postValue(false)
                val response = apiInterface.sendMessage(messageRequest)
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse?.let { data ->
                            sendMessageResult.value = data
                            subtractPoint.value = true
                        }
                    }
                }
            } catch (throwable: Throwable) {
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        sendMessage(messageRequest, activity)
                    }
                )
            } finally {
                isEnableButtonSend.postValue(true)
            }
        }
    }

    fun loadDetailUser(activity: Activity, code: String) {
        if (code.isEmpty()) return
        viewModelScope.launch {
            try {
                val response = apiInterface.getUserProfileDetail(code)
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse?.let { data ->
                            userProfileResult.postValue(data)
                        }
                    }
                }
            } catch (throwable: Throwable) {
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        loadDetailUser(activity, performerCode)
                    }
                )
            }
        }
    }

    fun sendFirstMessage(messageRequest: MessageRequest, activity: Activity) {
        viewModelScope.launch {
            try {
                val response = apiInterface.sendFirstMessage(messageRequest)
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse?.let { data ->
                            isCloseFirstMessageInLocal.value = true
                            sendMessageResult.value = data
                        }
                    }
                }
            } catch (throwable: Throwable) {
                handleThowable(
                    activity,
                    throwable,
                    reloadData = {
                        sendFirstMessage(messageRequest, activity)
                    }
                )
            }
        }
    }

    fun sendFreeTemplate(freeTemplateRequest: FreeTemplateRequest, activity: Activity) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.sendFreeTemplate(freeTemplateRequest)
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse?.let { data ->
                            sendMessageResult.value = data
                        }
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                isLoading.value = false
            }
        }
    }

    /**
     * handle socket
     */

    fun listenEventSocket() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    fun closeListenEvent() {
        EventBus.getDefault().unregister(this)
    }

    fun loadMemberInfo() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.getMember()
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse?.let { data ->
                            memberInFoResult.value = data
                        }
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: SocketActionSend) {
        Log.e("ChatMessageViewModel", "onMessageEvent: $event")
        responseSocket.value = event
        isMessageFromSocket = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(actionLoadProfile: ActionLoadProfile) {
        loadMemberInfo()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(actionReloadMessage: EventBusAction.ReloadMessage) {
        activity?.let {
            loadMessage(it, performerCode, isLoadmore = false, isShowLoading = false)
        }
    }

    private fun loadUnreadCount(context: Context) {
        viewModelScope.launch {
            try {
                val response = apiInterface.getUnreadCount()
                response.let {
                    if (it.errors.isEmpty()) {
                        val unread = it.dataResponse.unread_count
                        rxPreferences.saveNumberUnreadMessage(unread)
                        try {
                            ShortcutBadger.applyCount(context, unread)
                        } catch (e: java.lang.Exception) {
                        }
                    }
                }
            } catch (e: Exception) { // Ignore}
            }
        }
    }

    private fun saveListFreeTemplate() {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            try {
                val response = apiInterface.getListTemplate()
                response.let {
                    if (it.errors.isEmpty()) {
                        if (rxPreferences.getListTemplate() == null) {
                            rxPreferences.saveListTemplate(it.dataResponse)
                        }
                    }
                }
            } catch (e: Exception) {
                rxPreferences.saveListTemplate(dummyFreeTemplateData())
            }
        }
    }

    fun getCurrentConsultant(): ConsultantResponse? {
        return userProfileResult.value
    }

    companion object {
        const val HIDDEN_LOAD_MORE = 0
        const val DISABLE_LOAD_MORE = 1
        const val ENABLE_LOAD_MORE = 2
    }
}
