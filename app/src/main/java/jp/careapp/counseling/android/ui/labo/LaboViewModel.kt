package jp.careapp.counseling.android.ui.labo

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.data.model.labo.LaboResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.repository.LaboRepository

class LaboViewModel @ViewModelInject constructor(
    private val laboRepository: LaboRepository,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    val tabAllListener = SingleLiveEvent<Boolean>()
    val tabQuestionListener = SingleLiveEvent<Boolean>()
    val tabResolveListener = SingleLiveEvent<Boolean>()


    fun setScrollTab(isScroll: Boolean) {
        tabAllListener.value = isScroll
        tabQuestionListener.value = isScroll
        tabResolveListener.value = isScroll
    }


    private val paramsLaboAll: MutableMap<String, Any> =
        mutableMapOf("order" to "desc", "sort" to "created_at")

    val pagingLaboAll: LiveData<PagingData<LaboResponse>>
        get() = laboRepository(paramsLaboAll).cachedIn(viewModelScope).asLiveData()

    private val paramsResolved: MutableMap<String, Any> =
        mutableMapOf("order" to "desc", "sort" to "created_at", "statuses[0]" to 3)

    val pagingLaboResolved: LiveData<PagingData<LaboResponse>>
        get() = laboRepository(paramsResolved).cachedIn(viewModelScope).asLiveData()

    private val paramsQuestion: MutableMap<String, Any> =
        mutableMapOf("order" to "desc", "sort" to "created_at","member_code" to rxPreferences.getMemberCode().toString())
    val pagingLaboQuestion: LiveData<PagingData<LaboResponse>>
        get() = laboRepository(paramsQuestion).cachedIn(viewModelScope).asLiveData()

    private val _paramsSearch = MutableLiveData<MutableMap<String,Any>>()
    fun setParamsSearch(params:MutableMap<String,Any>){
        _paramsSearch.value = params
    }
    val pagingLaboSearch: LiveData<PagingData<LaboResponse>> = _paramsSearch.switchMap {
        laboRepository(it).cachedIn(viewModelScope).asLiveData()
    }
}