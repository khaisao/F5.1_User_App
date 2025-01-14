package jp.slapp.android.android.ui.favourite

import android.os.Handler
import android.os.Looper
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.network.ApiObjectResponse
import jp.slapp.android.android.data.network.BlockedConsultantResponse
import jp.slapp.android.android.data.network.HistoryResponse
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.utils.BUNDLE_KEY
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.roundToInt

const val LIMIT_NUMBER = 100

class HistoryViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
) : BaseViewModel() {

    var listHistoryConsultantResult = MutableLiveData<ArrayList<HistoryResponse>>(arrayListOf())
    private val listBlockedConsultantResult =
        MutableLiveData<ApiObjectResponse<ArrayList<BlockedConsultantResponse>>>()

    private lateinit var listConsultantTemp: ArrayList<HistoryResponse>
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
            isLoading.value=true
            try {
                apiInterface.getListBlockedConsultant().let {
                    if (it.errors.isEmpty()) {
                        listBlockedConsultantResult.value = it
                        getTotalNumberConsultant()
                    }
                    isLoading.value=false
                }
            } catch (throwable: Throwable) {
                getTotalNumberConsultant()
                isLoading.value=false

            }
        }
    }

    private fun getTotalNumberConsultant() {
        val params: MutableMap<String, Any> = HashMap()
        viewModelScope.launch {
            try {
                apiInterface.getTotalNumberConsultant(params).let {
                    totalConsultant = it.dataResponse.count
                    totalPage = (ceil((totalConsultant / LIMIT_NUMBER).toFloat()).roundToInt()) + 1
                    getHistoryMember()
                }
            } catch (throwable: Throwable) {
            }
        }
    }

    private fun getHistoryMember(page: Int = 1, isShowLoading: Boolean = true) {
        val params: MutableMap<String, Any> = HashMap()
        params[BUNDLE_KEY.LIMIT] = 100
        params[BUNDLE_KEY.PAGE] = page
        viewModelScope.launch {
            try {
                apiInterface.getPerformerHaveSeen(params).let {
                    val currentListData =
                        if (page == 1) mutableListOf() else listConsultantTemp.toMutableList()
                    currentListData.addAll(it.dataResponse)
                    listConsultantTemp = currentListData as ArrayList<HistoryResponse>
                    currentPage = page
                    getListConsultant()
                    if (isLoadMoreData) isLoadMoreData = false
                }
            } catch (e: Exception) {
            }
        }
    }

    fun isCanLoadMoreData(): Boolean {
        return currentPage < totalPage
    }

    fun loadMoreData() {
        if (++currentPage <= totalPage) getHistoryMember(
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
            }
            listConsultantTemp.clear()
            listConsultantTemp.addAll(dataResult)
        }
        loadDataSuccess()
    }

    private fun loadDataSuccess() {
        isShowHideLoading = false
        listHistoryConsultantResult.value = listConsultantTemp
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
