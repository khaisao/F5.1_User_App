package jp.careapp.counseling.android.ui.favourite

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.*
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.roundToInt

const val LIMIT_NUMBER = 50

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
        params[BUNDLE_KEY.LIMIT] = 10
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
            } catch (throwable: Throwable) {
                Log.d("arehgaerhaerh", "getHistoryMember: $throwable")
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
        }
    }

    private val runnable = Runnable {
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
