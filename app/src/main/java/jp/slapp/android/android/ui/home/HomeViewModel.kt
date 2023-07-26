package jp.slapp.android.android.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.network.BannerResponse
import jp.slapp.android.android.data.network.BlockedConsultantResponse
import jp.slapp.android.android.data.network.ConsultantOnlineResponse
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.utils.BUNDLE_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.ceil

const val LIMIT_NUMBER = 100

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    private val _listBanner = MutableLiveData<List<BannerResponse>>()
    val lisBanner: MutableLiveData<List<BannerResponse>> = _listBanner
    private var totalPage = 0
    private var totalConsultant = 0
    private var currentPage = 1
    var listConsultant = MutableLiveData<List<ConsultantResponse>>(emptyList())
    private var listBlockedConsultantResponse: List<BlockedConsultantResponse> = emptyList()

    init {
        getListBanner()
        getMemberInfo()
    }

    fun getListBanner() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apiInterface.getListBanner().apply {
                    if (errors.isEmpty()) {
                        if (dataResponse != null) {
                            _listBanner.postValue(dataResponse!!)
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun getMemberInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = apiInterface.getMember()
                    if (response.errors.isEmpty()) {
                        val dataResponse = response.dataResponse
                        rxPreferences.saveMemberInfoEditProfile(
                            dataResponse.name,
                            dataResponse.mail,
                            dataResponse.age,
                            dataResponse.birth,
                            dataResponse.sex,
                            dataResponse.point,
                            dataResponse.pushMail,
                            dataResponse.receiveNoticeMail,
                            dataResponse.receiveNewsletterMail
                        )
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    private suspend fun getAllListConsultantOnline(): ConsultantOnlineResponse {
        try {
            apiInterface.getListConsultantOnline().let {
                if (it.errors.isEmpty()) {
                    return it.dataResponse
                } else {
                    return ConsultantOnlineResponse()
                }
            }
        } catch (throwable: Throwable) {
            return ConsultantOnlineResponse()
        }
    }

    private suspend fun getAllListConsultantWait(): List<ConsultantResponse> {
        try {
            apiInterface.getListConsultantWait().let {
                if (it.errors.isEmpty()) {
                    return it.dataResponse
                } else {
                    return emptyList()
                }
            }
        } catch (throwable: Throwable) {
            return emptyList()
        }
    }

    fun getAllData(isLoadMore: Boolean = false) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                if (!isLoadMore) {
                    val onlineDeferred = async { getAllListConsultantOnline() }
                    val waitDeferred = async { getAllListConsultantWait() }
                    val offlineDeferred = async { getAllListConsultantOffline() }
                    val blockedDeferred = async { getListBlockedConsultant() }
                    val onlineDataResponse = onlineDeferred.await()
                    val waitDataResponse = waitDeferred.await()
                    val offlineResponse = offlineDeferred.await()
                    listBlockedConsultantResponse = blockedDeferred.await()
                    listConsultant.value =
                        onlineDataResponse.listChat + onlineDataResponse.listTwoShot
                    listConsultant.value =
                        (listConsultant.value ?: emptyList()).sortedByDescending {
                            it.loginMemberCount + it.peepingMemberCount
                        }
                    listConsultant.value =
                        (listConsultant.value ?: emptyList()) + waitDataResponse + offlineResponse
                    filterBlockConsultant(listConsultant, listBlockedConsultantResponse)
                    isLoading.value = false
                } else {
                    val offlineDeferred =
                        async { getAllListConsultantOffline(page = currentPage + 1) }
                    awaitAll(offlineDeferred)
                    val offlineResponse = offlineDeferred.await()
                    listConsultant.value =
                        (listConsultant.value ?: emptyList()).plus(offlineResponse)
                    filterBlockConsultant(listConsultant, listBlockedConsultantResponse)
                    isLoading.value = false
                }
            } catch (throwable: Throwable) {
                isLoading.value = false
            }
        }
    }

    private fun filterBlockConsultant(
        listConsultant: MutableLiveData<List<ConsultantResponse>>,
        listBlockedConsultantResponse: List<BlockedConsultantResponse>
    ) {
        val nonNullConsultantList = listConsultant.value?.filterNotNull() ?: emptyList()
        listConsultant.value = nonNullConsultantList.filterNot { consultant ->
            listBlockedConsultantResponse.any {
                it.code == consultant.code
            }
        }
    }

    private suspend fun getListBlockedConsultant(): List<BlockedConsultantResponse> {
        try {
            apiInterface.getListBlockedConsultant().let {
                if (it.errors.isEmpty()) {
                    return it.dataResponse
                } else {
                    return emptyList()
                }
            }
        } catch (throwable: Throwable) {
            return emptyList()
        }
    }

    private suspend fun getAllListConsultantOffline(
        page: Int = 1,
        limit: Int = LIMIT_NUMBER
    ): List<ConsultantResponse> {
        currentPage = page
        var limitTemp = limit
        if (totalPage != 0 && totalConsultant != 0) {
            if ((totalConsultant - (limitTemp * page)) < limitTemp) {
                limitTemp = totalConsultant - ((page - 1) * LIMIT_NUMBER)
            }
        }
        val params: MutableMap<String, Any> = HashMap()
        params[BUNDLE_KEY.LIMIT] = limitTemp
        params[BUNDLE_KEY.PAGE] = page
        try {
            apiInterface.getListConsultantOffline(params).let {
                if (it.errors.isEmpty()) {
                    totalPage =
                        ceil(it.pagination.total.toDouble() / LIMIT_NUMBER.toDouble()).toInt()
                    totalConsultant = it.pagination.total
                    return it.dataResponse
                } else {
                    return emptyList()
                }
            }
        } catch (throwable: Throwable) {
            return emptyList()
        }
    }

    fun isCanLoadMoreData(): Boolean {
        return currentPage < totalPage
    }

    fun clearData() {
        totalPage = 0
        currentPage = 0
        totalConsultant = 0
        getAllData()
    }
}
