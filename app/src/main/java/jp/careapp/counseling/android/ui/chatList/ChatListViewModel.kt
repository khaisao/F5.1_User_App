package jp.careapp.counseling.android.ui.chatList

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.model.history_chat.HistoryChatResponse
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.BlockedConsultantResponse
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.HistoryMessage.NON_PAID_MESS
import jp.careapp.counseling.android.utils.HistoryMessage.PAID_MESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

const val TAG = "ChatListViewModel"

class ChatListViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    var listHistoryChatResult = MutableLiveData<List<HistoryChatResponse>>()
    var loadMoreState = MutableLiveData<Int>()
    var isShowNoData = MutableLiveData(false)
    private var isLoadedAllPaidMess = false
    private var paidMessPage = 1
    private var nonPaidMessPage = 1

    private suspend fun getListBlockedConsultant(): List<BlockedConsultantResponse> {
        return try {
            apiInterface.getListBlockedConsultant().dataResponse
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    fun loadListMessage(isLoadMore: Boolean, isShowLoading: Boolean = true) {
        viewModelScope.launch {
            try {
                if (!isLoadMore) resetDataLoadMore()
                if (isShowLoading) isLoading.postValue(true)
                val listBlock = withContext(Dispatchers.Default) { getListBlockedConsultant() }
                if (!isLoadedAllPaidMess) {
                    withContext(Dispatchers.Default) {
                        getHistoryMessages(paidMessPage, PAID_MESS)
                    }?.apply {
                        // Check load more
                        pagination.apply {
                            if (dataResponse.size >= limit && limit * page < total) {
                                paidMessPage += 1
                            } else {
                                isLoadedAllPaidMess = true
                            }
                        }
                        // Add data
                        addHistoryMessage(dataResponse, listBlock, isLoadMore, PAID_MESS)
                    }
                }
                if (isLoadedAllPaidMess) {
                    getHistoryMessages(nonPaidMessPage, NON_PAID_MESS)?.apply {
                        viewModelScope.launch(Dispatchers.Main) {
                            // Check load more
                            pagination.apply {
                                if (dataResponse.size >= limit && limit * page < total) {
                                    nonPaidMessPage += 1
                                } else {
                                    loadMoreState.value = DISABLE_LOAD_MORE
                                }
                                isShowNoData.postValue(pagination.total == 0)
                            }
                            // Add data
                            addHistoryMessage(dataResponse, listBlock, true, NON_PAID_MESS)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.tag(TAG).d(e)
            } finally {
                if (isShowLoading) isLoading.postValue(false)
                if (isLoadMore) loadMoreState.postValue(HIDDEN_LOAD_MORE)
            }
        }
    }

    private fun addHistoryMessage(
        messages: List<HistoryChatResponse>,
        blocked: List<BlockedConsultantResponse>,
        isPlusData: Boolean,
        payOpen: Int
    ) {
        messages.filterNot { data ->
            blocked.any { it.code == data.performer?.code }
        }.let { data ->
            data.forEach { it.payOpen = payOpen }
            if (isPlusData) {
                listHistoryChatResult.postValue(listHistoryChatResult.value?.plus(data))
            } else {
                listHistoryChatResult.postValue(data)
            }
        }
    }

    private suspend fun getHistoryMessages(
        page: Int,
        payOpen: Int
    ): ApiObjectResponse<List<HistoryChatResponse>>? {
        val params = HashMap<String, Any>()
        params[BUNDLE_KEY.IS_HIDDEN_OWNER_MAIL] = 0
        params[BUNDLE_KEY.PAGE] = page
        params[BUNDLE_KEY.LIMIT] = BUNDLE_KEY.LIMIT_20
        params[BUNDLE_KEY.PARAM_SORT] = BUNDLE_KEY.SEND_DATE
        params[BUNDLE_KEY.PARAM_ODER] = BUNDLE_KEY.DESC
        params[BUNDLE_KEY.PARAM_PAY_OPEN] = payOpen
        return try {
            apiInterface.getListHistoryMessage(params)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
            null
        }
    }

    private fun resetDataLoadMore() {
        paidMessPage = 1
        nonPaidMessPage = 1
        isLoadedAllPaidMess = false
        loadMoreState.value = ENABLE_LOAD_MORE
    }

    fun getPaidMessPage(): Int = paidMessPage

    /**
     * handle socket
     */
    fun registerEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    //
    fun removeEvent() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: SocketActionSend) {
        loadListMessage(isLoadMore = false, isShowLoading = false)
    }

    companion object {
        const val HIDDEN_LOAD_MORE = 0
        const val DISABLE_LOAD_MORE = 1
        const val ENABLE_LOAD_MORE = 2
    }
}
