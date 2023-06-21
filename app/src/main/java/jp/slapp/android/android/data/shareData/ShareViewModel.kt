package jp.slapp.android.android.data.shareData

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import kotlinx.coroutines.launch
import timber.log.Timber

const val TAG = "ShareViewModel"

class ShareViewModel @ViewModelInject constructor(
    private val rxPreferences: RxPreferences,
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    companion object {
        const val TAB_HOME_SELECTED = 0
        const val TAB_RANKING_SELECTED = 1
        const val TAB_CHAT_LIST_SELECTED = 2
        const val TAB_MY_PAGE_SELECTED = 3
        const val TAB_FAVORITE_SELECTED = 4
    }

    val tabSelected = MutableLiveData<Int>()
    val laboTabSelected = MutableLiveData<Int>()
    var saveFocusTab: Int = 0

    // value of performer when click notifi
    val valuePassFromMain = MutableLiveData<Int>()
    val isHaveToken = SingleLiveEvent<Boolean>()

    val isFocusEmail = MutableLiveData(false)
    val isFocusVerifyCode = MutableLiveData(false)

    val isScrollToTop = SingleLiveEvent<Boolean>()

    var performerCode = MutableLiveData<String>()
    var messagePerformerCode = MutableLiveData<String>()
    var listPerformer = MutableLiveData<List<ConsultantResponse>>()
    var listPerformerSearch = MutableLiveData<List<ConsultantResponse>>()
    var isShowBuyPoint = MutableLiveData(false)
    var isShowRankLoading = MutableLiveData<Boolean>()
    val needUpdateProfile = MutableLiveData(false)
    var isBlockConsultant = MutableLiveData(false)
    val detectRefreshDataFollowerHome = MutableLiveData(true)
    val detectRefreshDataFavorite = MutableLiveData(true)
    val detectRefreshDataHistory = MutableLiveData(true)


    fun setTabSelected(tab: Int) {
        tabSelected.value = tab
    }

    fun setFocusEditText(isFocus: Boolean) {
        isFocusEmail.value = isFocus
    }

    fun setFocusVerifyCode(isFocus: Boolean) {
        isFocusVerifyCode.value = isFocus
    }

    fun setHaveToken(isSuccess: Boolean) {
        isHaveToken.value = isSuccess
    }

    fun setScrollToTop(isScroll: Boolean) {
        isScrollToTop.value = isScroll
    }

    fun doneScrollView() {
        isScrollToTop.value = false
    }

    fun setPerformerCode(code: String) {
        performerCode.value = code
    }

    fun getPerformerCode(): String {
        return performerCode.value.toString()
    }

    fun setMessagePerformerCode(code: String) {
        messagePerformerCode.value = code
    }

    fun getMessagePerformerCode(): String {
        return messagePerformerCode.value.toString()
    }

    fun setListPerformer(listData: List<ConsultantResponse>) {
        listPerformer.value = listData
    }

    fun getListPerformer(): List<ConsultantResponse>? {
        return listPerformer.value
    }

    fun saveListPerformerSearch(listData: List<ConsultantResponse>) {
        listPerformerSearch.value = listData
    }

    fun getListPerformerSearch(): List<ConsultantResponse>? {
        return listPerformerSearch.value
    }

    fun setRankLoading(isLoading: Boolean) {
        isShowRankLoading.value = isLoading
    }

    fun getCreditPrices(newFirstBuyCredit: Boolean) {
        val currentFirstBuyCredit = rxPreferences.getFirstBuyCredit()
        if (currentFirstBuyCredit && !newFirstBuyCredit) {
            viewModelScope.launch {
                try {
                    apiInterface.getCreditPrices(firstBuyCredit = false).let {
                        if (it.errors.isEmpty()) {
                            rxPreferences.saveCreditPrices(it.dataResponse)
                        }
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).d(e)
                }
            }
        }
    }
}
