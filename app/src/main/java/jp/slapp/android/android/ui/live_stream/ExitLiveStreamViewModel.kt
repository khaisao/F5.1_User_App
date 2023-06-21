package jp.slapp.android.android.ui.live_stream

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.network.ApiObjectResponse
import jp.slapp.android.android.data.network.BlockedConsultantResponse
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.ui.home.LIMIT_NUMBER
import jp.slapp.android.android.utils.BUNDLE_KEY
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.roundToInt

@HiltViewModel
class ExitLiveStreamViewModel @Inject constructor(private val apiInterface: ApiInterface) :
    BaseViewModel() {
    var listConsultantResult = MutableLiveData<ArrayList<ConsultantResponse>>(arrayListOf())
    private val listBlockedConsultantResult =
        MutableLiveData<ApiObjectResponse<ArrayList<BlockedConsultantResponse>>>()

    private lateinit var listConsultantTemp: ArrayList<ConsultantResponse>
    private var totalConsultant = 0
    private var totalPage = 0
    private val mHandler = Handler(Looper.getMainLooper())
    private var isFirstTimeLoadData = false
    var isShowHideLoading: Boolean = false
    private var currentPage = 1
    var isLoadMoreData: Boolean = false

    init {
        isFirstTimeLoadData = true
        isShowHideLoading = true
        getListBlockedConsultant()
    }

    fun getListBlockedConsultant() {
        listConsultantTemp = arrayListOf()
        listBlockedConsultantResult.value?.dataResponse?.clear()
        viewModelScope.launch {
            try {
                apiInterface.getListBlockedConsultant().let {
                    if (it.errors.isEmpty()) {
                        listBlockedConsultantResult.value = it
                        getTotalNumberConsultant()
                    }
                }
            } catch (throwable: Throwable) {
                getTotalNumberConsultant()
            }
        }
    }

    private fun getTotalNumberConsultant() {
        val params: MutableMap<String, Any> = HashMap()
        viewModelScope.launch {
            isLoading.value = isShowHideLoading
            try {
                apiInterface.getTotalNumberConsultant(params).let {
                    totalConsultant = it.dataResponse.count
                    totalPage = (ceil((totalConsultant / LIMIT_NUMBER).toFloat()).roundToInt()) + 1
                    getAllListConsultant()
                }
            } catch (throwable: Throwable) {
                isLoading.value = false
            }
        }
    }

    private fun getAllListConsultant(page: Int = 1, isShowLoading: Boolean = true) {
        val params: MutableMap<String, Any> = HashMap()
        params[BUNDLE_KEY.CHAT_STATUS] = 2
        params[BUNDLE_KEY.PARAM_SORT] = BUNDLE_KEY.LAST_LOGIN_DATE
        params[BUNDLE_KEY.PARAM_ODER] = BUNDLE_KEY.DESC
        params[BUNDLE_KEY.LIMIT] = LIMIT_NUMBER
        params[BUNDLE_KEY.PAGE] = page
        viewModelScope.launch {
            if (isShowLoading) isLoading.value = true
            try {
                apiInterface.getListConsultant(params).let {
                    isLoading.value = false
                    val currentListData =
                        if (page == 1) mutableListOf() else listConsultantTemp.toMutableList()
                    currentListData.addAll(it.dataResponse)
                    listConsultantTemp = currentListData as ArrayList<ConsultantResponse>
                    currentPage = page
                    getListConsultant()
                    if (isLoadMoreData) isLoadMoreData = false
                }
            } catch (throwable: Throwable) {
                isLoading.value = false
            }
        }
    }

    fun isCanLoadMoreData(): Boolean {
        return currentPage < totalPage
    }

    fun loadMoreData() {
        if (++currentPage <= totalPage) getAllListConsultant(
            page = currentPage,
            isShowLoading = false
        )
    }

    private fun getListConsultant() {
        if (listBlockedConsultantResult.value != null && listBlockedConsultantResult.value!!.dataResponse.isNotEmpty()) {
            val dataResult = listConsultantTemp.filterNot { consultant ->
                listBlockedConsultantResult.value!!.dataResponse.any {
                    it.code == consultant.code
                }
            }.sortedByDescending {
                it.loginMemberCount + it.peepingMemberCount
            }

            listConsultantTemp.clear()
            listConsultantTemp.addAll(dataResult)
        }
        loadDataSuccess()
    }

    private fun loadDataSuccess() {
        isShowHideLoading = false
        listConsultantResult.value = listConsultantTemp
        if (isFirstTimeLoadData) {
            mHandler.postDelayed(runnable, 500)
            isFirstTimeLoadData = false
        } else {
            isLoading.value = false
        }
    }

    private val runnable = Runnable {
        isLoading.value = false
    }

    override fun onCleared() {
        mHandler.removeCallbacks(runnable)
        super.onCleared()
    }

    fun clearData() {
        totalConsultant = 0
        totalPage = 0
        isFirstTimeLoadData = false
        currentPage = 1
        isLoadMoreData = false
    }

}
