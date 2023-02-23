package jp.careapp.counseling.android.ui.review_mode.user_detail_message

import android.annotation.SuppressLint
import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.data.event.EventBusAction
import jp.careapp.counseling.android.data.model.message.*
import jp.careapp.counseling.android.data.model.user_profile.ActionLoadProfile
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.PaginationResponse
import jp.careapp.counseling.android.data.network.socket.SocketActionRead
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.model.network.RMConsultantResponse
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class UserDetailMsgViewModel @ViewModelInject constructor(
    private val rmApiInterface: RMApiInterface
) : BaseViewModel() {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    val actionState = SingleLiveEvent<ActionState>()
    val responseSendSocket = MutableLiveData<SocketActionSend>()
    val responseReadSocket = MutableLiveData<SocketActionRead>()
    var hiddenLoadMoreHandle = MutableLiveData<Int>()
    var messageResult = MutableLiveData<DataMessage>()
    val sendMessageResult = MutableLiveData<Boolean>()
    val isEnableButtonSend = MutableLiveData<Boolean>()

    var userProfileResult = RMConsultantResponse()
    var dataMessageMap = LinkedHashMap<String, MutableList<BaseMessageResponse>>()
    var isLoadMessageAfterSend = false
    var isMessageFromSocket = false
    var isListMessageChange = false
    var needLoadUserInfo = false
    var disableLoadMore = false
    var jobLoadMsgAfterSend: Job? = null
    private var performerCode = ""

    @SuppressLint("StaticFieldLeak")
    private var activity: Activity? = null

    var page = 1

    private suspend fun getMessages(params: HashMap<String, Any>): ApiObjectResponse<List<RMMessageResponse>> {
        val default = ApiObjectResponse<List<RMMessageResponse>>(
            errors = listOf(),
            dataResponse = listOf(),
            pagination = PaginationResponse(
                total = 0,
                page = 1,
                limit = 0
            )
        )
        return try {
            rmApiInterface.loadMessage(params)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            default
        }
    }

    private suspend fun getUserProfile(memberCode: String): ApiObjectResponse<RMConsultantResponse> {
        val default = ApiObjectResponse(
            errors = listOf(),
            dataResponse = RMConsultantResponse(),
            pagination = PaginationResponse(
                total = 0,
                page = 1,
                limit = 0
            )
        )
        if (memberCode.isEmpty() || memberCode == "0") return default
        return try {
            rmApiInterface.getUserProfileDetail(memberCode)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            default
        }
    }

    fun loadMessage(
        activity: Activity,
        performerCode: String,
        isLoadMore: Boolean,
        isShowLoading: Boolean = true,
        needLoadUserInfo: Boolean = false
    ) {
        this.activity = activity
        this.performerCode = performerCode
        this.isLoadMessageAfterSend = false
        this.needLoadUserInfo = needLoadUserInfo

        if (!isLoadMore) {
            page = 1
            dataMessageMap.clear()
        }

        val params = HashMap<String, Any>()
        if (performerCode.isNotEmpty()) {
            params[BUNDLE_KEY.PERFORMER_CODE] = performerCode
        } else {
            params[BUNDLE_KEY.PERFORMER_CODE] = "0"
        }
        params[BUNDLE_KEY.PAGE] = page
        params[BUNDLE_KEY.LIMIT] = BUNDLE_KEY.LIMIT_30
        params[BUNDLE_KEY.PARAM_SORT] = BUNDLE_KEY.SEND_DATE
        params[BUNDLE_KEY.PARAM_ODER] = BUNDLE_KEY.DESC

        viewModelScope.launch {
            if (!isLoadMore && isShowLoading) {
                isLoading.value = true
            }
            try {
                if (needLoadUserInfo) {
                    val responses = awaitAll(
                        async { getMessages(params) },
                        async { getUserProfile(performerCode) }
                    )

                    val listMessage = mutableListOf<RMMessageResponse>()
                    (responses[0] as ApiObjectResponse<*>?)?.let {
                        if (it.errors.isEmpty()) {
                            val listData = it.dataResponse as List<*>
                            for (data in listData) {
                                if (data is RMMessageResponse) {
                                    listMessage.add(data)
                                }
                            }
                        }
                    }

                    var userInfo = RMConsultantResponse()
                    (responses[1] as ApiObjectResponse<*>?)?.let {
                        if (it.errors.isEmpty()) {
                            (it.dataResponse as? RMConsultantResponse)?.let { userInfoResponse ->
                                userInfo = userInfoResponse
                            }
                        }
                    }
                    userProfileResult = userInfo

                    if (listMessage.isEmpty()) {
                        handleDataMessageNullOrEmpty(isLoadMore)
                    } else {
                        handleDataMessageNotNullOrEmpty(listMessage, isLoadMore)
                    }
                } else {
                    val responseMessage = rmApiInterface.loadMessage(params)
                    if (responseMessage.errors.isEmpty()) {
                        if (responseMessage.dataResponse.isEmpty()) {
                            handleDataMessageNullOrEmpty(isLoadMore)
                        } else {
                            handleDataMessageNotNullOrEmpty(
                                responseMessage.dataResponse,
                                isLoadMore
                            )
                        }
                    }
                }
            } catch (throwable: Throwable) {
                if (!isLoadMore && isShowLoading) {
                    isLoading.value = false
                }
                throwable.printStackTrace()
            } finally {
                if (isLoadMore) {
                    hiddenLoadMoreHandle.value = HIDDEN_LOAD_MORE
                } else {
                    isLoading.value = false
                }
            }
        }
    }

    private fun handleDataMessageNotNullOrEmpty(
        data: List<RMMessageResponse>,
        isLoadMore: Boolean
    ) {
        hiddenLoadMoreHandle.value = ENABLE_LOAD_MORE
        page += 1

        data.forEach {
            val time = DateUtil.convertStringToDateString(
                it.sendDate,
                DateUtil.DATE_FORMAT_1,
                DateUtil.DATE_FORMAT_3
            )

            if (dataMessageMap.containsKey(time)) {
                dataMessageMap[time]?.add(it)
            } else {
                val listMessage = mutableListOf<BaseMessageResponse>()
                listMessage.add(it)
                dataMessageMap[time] = listMessage
            }
        }

        val messageResultConvert = mutableListOf<BaseMessageResponse>()
        for ((key, value) in dataMessageMap) {
            messageResultConvert.addAll(value)
            messageResultConvert.add(TimeMessageResponse(time = key))
        }

        messageResult.value = DataMessage(isLoadMore, messageResultConvert.reversed())
    }

    private fun handleDataMessageNullOrEmpty(isLoadMore: Boolean) {
        if (isLoadMore) {
            disableLoadMore = true
            hiddenLoadMoreHandle.value = DISABLE_LOAD_MORE
        } else messageResult.value = DataMessage(isLoadMore = false, mutableListOf())
    }

    fun sendMessage(messageRequest: RMMessageRequest) {
        viewModelScope.launch {
            try {
                isEnableButtonSend.postValue(false)
                val response = rmApiInterface.sendMessage(messageRequest)
                response.let {
                    sendMessageResult.value = it.errors.isEmpty()
                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            } finally {
                isEnableButtonSend.postValue(true)
            }
        }
    }

    fun loadMessageAfterSend(performerCode: String) {
        this.isListMessageChange = true
        this.isLoadMessageAfterSend = true

        val params = getParamsRequest(performerCode)

        jobLoadMsgAfterSend = viewModelScope.launch {
            try {
                val responseMessage = rmApiInterface.loadMessage(params)
                if (responseMessage.errors.isEmpty()) {
                    if (!responseMessage.dataResponse.isNullOrEmpty()) {
                        val data = responseMessage.dataResponse
                        data.forEach {
                            val time = DateUtil.convertStringToDateString(
                                it.sendDate,
                                DateUtil.DATE_FORMAT_1,
                                DateUtil.DATE_FORMAT_3
                            )
                            if (dataMessageMap.containsKey(time)) {
                                dataMessageMap[time]?.add(0, it)
                            } else {
                                val newMap = dataMessageMap.clone()
                                dataMessageMap.clear()
                                val listMessage = mutableListOf<BaseMessageResponse>()
                                listMessage.add(0, it)
                                dataMessageMap[time] = listMessage
                                dataMessageMap.putAll(newMap as LinkedHashMap<String, MutableList<BaseMessageResponse>>)
                            }
                        }
                        val messageResultConvert = mutableListOf<BaseMessageResponse>()
                        for ((key, value) in dataMessageMap) {
                            messageResultConvert.addAll(value)
                            messageResultConvert.add(
                                TimeMessageResponse(
                                    key
                                )
                            )
                        }
                        messageResult.value =
                            DataMessage(false, messageResultConvert.reversed())
                    }
                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
    }

    private fun getParamsRequest(performerCode: String): HashMap<String, Any> {
        return HashMap<String, Any>().apply {
            put(BUNDLE_KEY.PERFORMER_CODE, performerCode)
            put(BUNDLE_KEY.LIMIT, 1)
            put(BUNDLE_KEY.PARAM_SORT, BUNDLE_KEY.SEND_DATE)
            put(BUNDLE_KEY.PARAM_ODER, BUNDLE_KEY.DESC)
        }
    }

    fun blockUser(memberCode: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = rmApiInterface.blockUser(memberCode)
            withContext(Dispatchers.Main) {
                isLoading.value = false
                actionState.value = ActionState.BlockUserSuccess(response.errors.isEmpty())
            }
        }
    }

    fun listenEventSocket() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    fun closeListenEvent() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: SocketActionSend) {
        Timber.tag(TAG).e("onMessageEvent: $event")
        responseSendSocket.value = event
        isMessageFromSocket = true
        this.isListMessageChange = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: SocketActionRead) {
        jobLoadMsgAfterSend?.cancel()
        Timber.tag(TAG).e("onMessageEvent: $event")
        isMessageFromSocket = true
        responseReadSocket.value = event
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(actionLoadProfile: ActionLoadProfile) {
        Timber.tag(TAG).e("onMessageEvent - actionLoadProfile: $actionLoadProfile")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(actionReloadMessage: EventBusAction.ReloadMessage) {
        activity?.let { loadMessage(it, performerCode, false) }
    }

    companion object {
        const val HIDDEN_LOAD_MORE = 0
        const val DISABLE_LOAD_MORE = 1
        const val ENABLE_LOAD_MORE = 2
        const val TAG = "UserDetailMsgViewModel"
    }
}
