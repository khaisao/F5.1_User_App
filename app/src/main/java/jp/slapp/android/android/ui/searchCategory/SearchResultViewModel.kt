package jp.slapp.android.android.ui.searchCategory

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmResults
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.database.ConsultantDatabase
import jp.slapp.android.android.data.network.ApiObjectResponse
import jp.slapp.android.android.data.network.BlockedConsultantResponse
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.network.socket.SocketActionSend
import jp.slapp.android.android.model.user_profile.ConsultantSearch
import jp.slapp.android.android.model.user_profile.PerformerSearch
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.ui.home.LIMIT_NUMBER
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.Define
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.ceil
import kotlin.math.roundToInt

class SearchResultViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    val listConsultantResult = MutableLiveData<ArrayList<ConsultantResponse>>(arrayListOf())
    private val _newMessage = MutableLiveData<SocketActionSend>()
    val newMessage: LiveData<SocketActionSend> get() = _newMessage

    private val _dataPerformer = MutableLiveData<PerformerSearch>()
    val dataPerformer: LiveData<PerformerSearch> get() = _dataPerformer
    private var consultantSearch: ConsultantSearch = ConsultantSearch()
    var isShowHideLoading= MutableLiveData<Boolean>(false)

    private var currentPage = 1
    var isLoadMoreData: Boolean = false
    private var totalConsultant = 0
    private var totalPage = 0
    private val _listConsultantTemp = MutableLiveData<ArrayList<ConsultantResponse>>(arrayListOf())
    val listConsultantTemp: LiveData<ArrayList<ConsultantResponse>> get() = _listConsultantTemp
    private var params: MutableMap<String, Any>? = null
    private val listBlockedConsultantResult =
        MutableLiveData<ApiObjectResponse<ArrayList<BlockedConsultantResponse>>>()
    var isFirstInit: Boolean = true

    init {
//        listConsultantResult.value = getListConsultantDatabase()!!

    }

    fun listenerEventSocket() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onNewMessageEvent(event: SocketActionSend) {
        _newMessage.value = event
    }

    fun getListConsultantDatabase(): ArrayList<ConsultantResponse>? {
        var listConsultantResponse = ArrayList<ConsultantResponse>()
        Realm.getDefaultInstance().use { realm ->
            val realmResult: RealmResults<ConsultantDatabase> =
                realm.where(ConsultantDatabase::class.java).findAll()

            realmResult.forEach {
                val consultantResponse =
                    Gson().fromJson(it.content, ConsultantResponse::class.java)
                listConsultantResponse.add(consultantResponse)
            }
        }
        return listConsultantResponse
    }

    fun setDataPerformer(data: PerformerSearch) {
        _dataPerformer.value = data
    }

    fun getListBlockedConsultant() {
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
        isShowHideLoading.value=true
        _dataPerformer.value?.let {
            consultantSearch.statusConsultant = if (it.statusConsultant != Define.SearchCondition.DEFAULT) it.statusConsultant else null
            consultantSearch.nameConsultant = if (it.nameConsultant != "") it.nameConsultant else null
            consultantSearch.haveNumberReview = if (it.haveNumberReview != Define.SearchCondition.DEFAULT) it.haveNumberReview else null
            consultantSearch.genderCondition = if (it.genderCondition != Define.SearchCondition.DEFAULT) it.genderCondition else null
            consultantSearch.genresSelectCondition = null
            if (it.listGenresSelected.isNotEmpty()) {
                consultantSearch.listGenresSelected = it.listGenresSelected
                consultantSearch.genresSelectCondition = "or"
            } else consultantSearch.listGenresSelected = null
            consultantSearch.listRankingSelected = if (it.listRankingSelected.isNotEmpty()) it.listRankingSelected else null
            consultantSearch.reviewAverageCondition = if (it.reviewAverageCondition != Define.SearchCondition.DEFAULT) it.reviewAverageCondition else null

            viewModelScope.launch {
                try {
                    apiInterface.getTotalNumberConsultantSearch(
                        statusConsultant = consultantSearch.statusConsultant,
                        nameConsultant = consultantSearch.nameConsultant,
                        haveNumberReview = consultantSearch.haveNumberReview,
                        genderCondition = consultantSearch.genderCondition,
                        genresSelectCondition = consultantSearch.genresSelectCondition,
                        listGenresSelected = consultantSearch.listGenresSelected,
                        listRankingSelected = consultantSearch.listRankingSelected,
                        reviewAverageCondition = consultantSearch.reviewAverageCondition
                    ).let {
                        if (it.errors.isEmpty()) {
                            totalConsultant = it.dataResponse.count
                            totalPage = (ceil((totalConsultant / LIMIT_NUMBER).toFloat()).roundToInt()) + 1
                            getSearchListConsultant()
                        }
                    }
                    isShowHideLoading.value=false
                } catch (throwable: Throwable){
                    isShowHideLoading.value=false
                }
            }

        }
    }

    private fun getSearchListConsultant(page: Int = 1, isShowLoading: Boolean = true) {
        _dataPerformer.value?.let {
            consultantSearch.sort = BUNDLE_KEY.PRESENCE_STATUS
            consultantSearch.order = BUNDLE_KEY.DESC
            consultantSearch.sort2 = BUNDLE_KEY.REVIEW_TOTAL_SCORE
            consultantSearch.order2 = BUNDLE_KEY.DESC
            consultantSearch.limit = LIMIT_NUMBER
            consultantSearch.page = page

            viewModelScope.launch {
                if (isShowLoading) isLoading.value = true
                try {
                    apiInterface.getListConsultantSearch(
                        statusConsultant = consultantSearch.statusConsultant,
                        nameConsultant = consultantSearch.nameConsultant,
                        haveNumberReview = consultantSearch.haveNumberReview,
                        genderCondition = consultantSearch.genderCondition,
                        genresSelectCondition = consultantSearch.genresSelectCondition,
                        listGenresSelected = consultantSearch.listGenresSelected,
                        listRankingSelected = consultantSearch.listRankingSelected,
                        reviewAverageCondition = consultantSearch.reviewAverageCondition,
                        sort = consultantSearch.sort,
                        order = consultantSearch.order,
                        sort2 = consultantSearch.sort2,
                        order2 = consultantSearch.order2,
                        limit = consultantSearch.limit,
                        page = consultantSearch.page
                    ).let {
                        if (it.errors.isEmpty()) {
                            isLoading.value = false
                            val currentListData =
                                if (page == 1) mutableListOf() else _listConsultantTemp.value?.toMutableList()
                                    ?: mutableListOf()
                            currentListData.addAll(it.dataResponse)
                            getListConsultant(currentListData)
                            currentPage = page
                            if (isLoadMoreData) isLoadMoreData = false
                        }
                    }
                } catch (throwable: Throwable) {
                    isLoading.value = false
                }
            }
        }

    }

    private fun getListConsultant(listConsultant: MutableList<ConsultantResponse>) {
        if (listBlockedConsultantResult.value != null && listBlockedConsultantResult.value!!.dataResponse.isNotEmpty()) {
            val dataResult = listConsultant.filterNot { consultant ->
                listBlockedConsultantResult.value!!.dataResponse.any {
                    it.code == consultant.code
                }
            }
            _listConsultantTemp.value = dataResult as ArrayList<ConsultantResponse>
        } else _listConsultantTemp.value = listConsultant as ArrayList<ConsultantResponse>
    }

    fun isCanLoadMoreData(): Boolean {
        return currentPage < totalPage
    }

    fun loadMoreData() {
        if (++currentPage <= totalPage) getSearchListConsultant(currentPage, isShowLoading = false)
    }
}
