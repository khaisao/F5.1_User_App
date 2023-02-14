package jp.careapp.counseling.android.ui.search_lab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.model.LabRequest
import jp.careapp.counseling.android.utils.SORT
import javax.inject.Inject

class SearchLabViewModel @Inject constructor() : BaseViewModel() {

    var labRequest = LabRequest()
    var genre = SaveStateSelection(content = SORT.DEFAULT)
    var status = SaveStateSelection(content = SORT.DEFAULT)
    var sort = SaveStateSelection(content = SORT.DEFAULT)
    var wordSearch = ""

    fun clearSelection() {
        genre.apply {
            content = SORT.DEFAULT
            itemSelect = listOf(0)
        }
        status.apply {
            content = SORT.DEFAULT
            itemSelect = listOf(0)
        }
        sort.apply {
            content = SORT.DEFAULT
            itemSelect = listOf(0)
        }
        labRequest = LabRequest()
    }

    private val _isResultSearch = MutableLiveData(false)
    val isResultSearch: LiveData<Boolean> = _isResultSearch
    fun setIsResultSearch(isResultSearch: Boolean) {
        _isResultSearch.value = isResultSearch
    }
    val paramsSearch : MutableMap<String,Any> = mutableMapOf()
}

data class SaveStateSelection(
    var content: String,
    var itemSelect: List<Long> = listOf(0)
)
