package jp.careapp.counseling.android.ui.rank

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.TypeRankingResponse
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import kotlinx.coroutines.launch
import java.util.*

const val NUMBER_RANKING_PEOPLE: Int = 30
class ListTypeRankingViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    val listRankingResult = MutableLiveData<ArrayList<TypeRankingResponse>>(arrayListOf())
    val rankingTopResult = MutableLiveData<ArrayList<TypeRankingResponse>>(arrayListOf())
    var isReadyShowResult = MutableLiveData(false)
    var isRankShowLoading = MutableLiveData<Boolean>()
    private lateinit var listConsultant: ArrayList<ConsultantResponse>

    fun getListTypeRanking(type: Int, activity: Activity, isShowLoading: Boolean) {
        val params: MutableMap<String, Any> = HashMap()
        params[BUNDLE_KEY.INTERVAL] = type
        params[BUNDLE_KEY.LIMIT] = NUMBER_RANKING_PEOPLE
        isReadyShowResult.value = false
        viewModelScope.launch {
            if (isShowLoading) {
                isRankShowLoading.postValue(true)
            }
            try {
                val rankingResponse = apiInterface.getListRanking(params)
                rankingResponse.let {
                    val listRankingTop: ArrayList<TypeRankingResponse> = arrayListOf()
                    val listRankingTemp: ArrayList<TypeRankingResponse> = arrayListOf()
                    var haveRankLargeThanThree = false
                    if (it.dataResponse.isNotEmpty()) {
                        for (i in 0 until it.dataResponse.size) {
                            if (it.dataResponse[i].ranking <= 3) {
                                listRankingTop.add(it.dataResponse[i])
                            } else {
                                haveRankLargeThanThree = true
                                listRankingTemp.add(it.dataResponse[i])
                            }
                        }
                        rankingTopResult.value = listRankingTop
                        listRankingResult.value = listRankingTemp
                        if (!haveRankLargeThanThree) {
                            rankingTopResult.value = it.dataResponse!!
                        }
                        setListConsultant(it.dataResponse)
                    } else {
                        rankingTopResult.value = arrayListOf()
                    }
                }
                isReadyShowResult.value = true
            } catch (throwable: Throwable) {
                isReadyShowResult.value = true
            } finally {
                if (isShowLoading) {
                    isRankShowLoading.postValue(false)
                }
            }
        }
    }

    fun getListRecommendRanking( activity: Activity, isShowLoading: Boolean) {
        val params: MutableMap<String, Any> = HashMap()
        params[BUNDLE_KEY.LIMIT] = NUMBER_RANKING_PEOPLE
        isReadyShowResult.value = false
        viewModelScope.launch {
            if (isShowLoading) {
                isRankShowLoading.postValue(true)
            }
            try {
                val rankingResponse = apiInterface.getListRecommendRanking(params)
                rankingResponse.let {
                    val listRankingTop: ArrayList<TypeRankingResponse> = arrayListOf()
                    val listRankingTemp: ArrayList<TypeRankingResponse> = arrayListOf()
                    var haveRankLargeThanThree = false
                    if (it.dataResponse.isNotEmpty()) {
                        for(i in 0 until it.dataResponse.size){
                            if(it.dataResponse[i].recommendRanking != null){
                                if(it.dataResponse[i].recommendRanking!! <=3){
                                    listRankingTop.add(it.dataResponse[i])
                                } else {
                                    haveRankLargeThanThree = true
                                    listRankingTemp.add(it.dataResponse[i])
                                }
                            }
                        }
                        rankingTopResult.value = listRankingTop
                        listRankingResult.value = listRankingTemp
                        if(!haveRankLargeThanThree){
                            rankingTopResult.value = it.dataResponse!!
                        }
                        setListConsultant(it.dataResponse)
                    } else {
                        rankingTopResult.value = arrayListOf()
                    }
                }
                isReadyShowResult.value = true
            } catch (throwable: Throwable) {
                isReadyShowResult.value = true
            } finally {
                if (isShowLoading) {
                    isRankShowLoading.postValue(false)
                }
            }
        }
    }

    private fun setListConsultant(listTypeRanking: List<TypeRankingResponse>) {
        listConsultant = arrayListOf()
        for (typeRanking in listTypeRanking) {
            typeRanking.performerResponse?.let { listConsultant.add(it) }
        }
    }

    fun getListConsultant(): ArrayList<ConsultantResponse> {
        return listConsultant
    }

}
