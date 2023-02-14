package jp.careapp.counseling.android.ui.searchCategory

import androidx.hilt.lifecycle.ViewModelInject
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmResults
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.database.ConsultantDatabase
import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.utils.Define.SearchCondition
import jp.careapp.counseling.android.utils.extensions.asLiveData
import jp.careapp.counseling.android.utils.realmUtil.LiveRealmData

class SearchViewModel @ViewModelInject constructor(
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    var listConsultantResponse = ArrayList<ConsultantResponse>()

    private val listCategory: List<CategoryResponse>? by lazy {
        val listCategoryResponse = rxPreferences.getListCategory()
        val temp = mutableListOf<CategoryResponse>()
        listCategoryResponse?.let {
            for (i in it) {
                if (i.registEnable) temp.add(i)
            }
        }
        temp
    }
    private val rankingIds = SearchCondition.RANKING_IDS

    private var nameConsultant: String = ""
    private var listGenresSelected: ArrayList<Int> = arrayListOf()
    var listGenresSelectedPosition: ArrayList<Int> = arrayListOf()
    private var genderCondition: Int = SearchCondition.DEFAULT
    private var listRankingSelected: ArrayList<Int> = arrayListOf()
    private var reviewAverageCondition: Int = SearchCondition.DEFAULT
    private var haveNumberReview: Int = SearchCondition.DEFAULT
    private var statusConsultant: Int = SearchCondition.DEFAULT

    fun setNameConsultant(name: String) {
        this.nameConsultant = name
    }

    fun getNameConsultant() : String{
        return nameConsultant
    }

    fun setStatusConsultant(status: Int) {
        this.statusConsultant = status
    }

    fun getStatusConsultant(): Int {
        return statusConsultant
    }

    fun setHaveNumberReview(status: Int) {
        this.haveNumberReview = status
    }

    fun getHaveNumberReview(): Int {
        return haveNumberReview
    }

    fun getListConsultantFromDataBase(): LiveRealmData<ConsultantDatabase>? {
        return Realm.getDefaultInstance().where(ConsultantDatabase::class.java).findAllAsync()
            .asLiveData()
    }

    fun getListConsultantResult(realmResult: RealmResults<ConsultantDatabase>): ArrayList<ConsultantResponse> {
        if (!listConsultantResponse.isNullOrEmpty()) {
            listConsultantResponse.clear()
        }
        realmResult.forEach {
            val consultantResponse =
                Gson().fromJson(it.content, ConsultantResponse::class.java)
            listConsultantResponse.add(consultantResponse)
        }
        return listConsultantResponse
    }

    fun setListConsultantResult(listConsultant: List<ConsultantResponse>): ArrayList<ConsultantResponse> {
        if (!listConsultantResponse.isNullOrEmpty()) {
            listConsultantResponse.clear()
        }
        listConsultantResponse.addAll(listConsultant)
        return listConsultantResponse
    }

    fun getResultConsultantAfterSearch(): ArrayList<ConsultantResponse> {
        var listConsultantTemp: ArrayList<ConsultantResponse> = arrayListOf()
        var listConsultantFinal: ArrayList<ConsultantResponse> = arrayListOf()

        // Check status condition
        if (statusConsultant == SearchCondition.ONLY_DURING_RECEPTION) {
            listConsultantFinal.addAll(listConsultantResponse.filter {
                statusConsultant == it.presenceStatus
            })
        } else {
            listConsultantFinal.addAll(listConsultantResponse)
        }

        // Check name condition
        if (nameConsultant.isNotEmpty()) {
            listConsultantFinal = listConsultantFinal.filter {
                it.name!!.toLowerCase()!!.contains(nameConsultant.toLowerCase().trim())
            } as ArrayList<ConsultantResponse>
        }

        // Check have review condition
        if (haveNumberReview == SearchCondition.HAVE_REVIEW) {
            listConsultantFinal = listConsultantFinal.filter {
                it.reviewTotalNumber > 0
            } as ArrayList<ConsultantResponse>
        }

        // Check gender condition
        if (genderCondition > 0) {
            listConsultantFinal = listConsultantFinal.filter {
                it.sex == genderCondition
            } as ArrayList<ConsultantResponse>
        }

        // Check genres condition
        if (listGenresSelected.isNotEmpty()) {
            listConsultantTemp.clear()
            for (consultant in listConsultantFinal) {
                for (j in consultant.genres) {
                    for (i in listGenresSelected) {
                        if (j == i && !listConsultantTemp.contains(consultant)) {
                            listConsultantTemp.add(consultant)
                        }
                    }
                }
            }
            listConsultantFinal.clear()
            listConsultantFinal.addAll(listConsultantTemp)
        }

        // Check ranking condition
        if (listRankingSelected.isNotEmpty()) {
            listConsultantFinal = listConsultantFinal.filter {
                listRankingSelected.contains(it.stage)
            } as ArrayList<ConsultantResponse>
        }

        // Check review average condition
        if (reviewAverageCondition == 1) {
            listConsultantFinal = listConsultantFinal.filter {
                it.reviewAverage >= 3
            } as ArrayList<ConsultantResponse>
        } else if (reviewAverageCondition == 2) {
            listConsultantFinal = listConsultantFinal.filter {
                it.reviewAverage >= 4
            } as ArrayList<ConsultantResponse>
        }
        return listConsultantFinal
    }

    fun updateGenresChecked(i: Int, isChecked: Boolean) {
        listCategory?.let {
            if (it.size > i) {
                var id = it[i].id
                if (isChecked) {
                    if (!listGenresSelected.contains(id)) {
                        listGenresSelected.add(id)
                    }
                    if (!listGenresSelectedPosition.contains(i)) {
                        listGenresSelectedPosition.add(i)
                    }
                } else {
                    if (listGenresSelected.contains(id)) {
                        listGenresSelected.remove(id)
                    }
                    if (listGenresSelectedPosition.contains(i)) {
                        listGenresSelectedPosition.remove(i)
                    }
                }
            }
        }
    }

    fun setGenderChecked(i: Int) {
        genderCondition = i
    }

    fun getGenderChecked(): Int {
        return genderCondition
    }

    fun getListGenresSelected() : ArrayList<Int> {
        return listGenresSelected
    }

    fun getListRankingSelected() : ArrayList<Int> {
        return listRankingSelected
    }

    fun updateRankingChecked(i: Int, isChecked: Boolean) {
        if (rankingIds.size > i) {
            var id = rankingIds[i]
            if (isChecked) {
                if (!listRankingSelected.contains(id)) {
                    listRankingSelected.add(id)
                }
            } else {
                if (listRankingSelected.contains(id)) {
                    listRankingSelected.remove(id)
                }
            }
        }
    }

    fun setReviewAverageChecked(i: Int) {
        reviewAverageCondition = i
    }

    fun getReviewAverageChecked() : Int {
        return reviewAverageCondition
    }
}