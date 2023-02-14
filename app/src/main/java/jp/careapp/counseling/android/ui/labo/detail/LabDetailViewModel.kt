package jp.careapp.counseling.android.ui.labo.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.AnswerResponse
import jp.careapp.counseling.android.data.network.LabDetailResponse
import jp.careapp.counseling.android.data.network.PaginationResponse
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.LoadMoreState.Companion.DISABLE_LOAD_MORE
import jp.careapp.counseling.android.utils.LoadMoreState.Companion.ENABLE_LOAD_MORE
import jp.careapp.counseling.android.utils.LoadMoreState.Companion.HIDDEN_LOAD_MORE
import jp.careapp.counseling.android.utils.LoadMoreState.Companion.SHOW_LOAD_MORE
import kotlinx.coroutines.launch

class LabDetailViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {

    var labDetailResult = MutableLiveData<LabDetailResponse?>()
    var listAnswerResult = MutableLiveData<List<AnswerResponse>>()
    var visibleLoadMoreResult = MutableLiveData<Int>()
    var enableLoadMoreResult = MutableLiveData<Int>()
    var totalAnswerResult = MutableLiveData<Int>()
    var noDataResult = MutableLiveData(false)
    var chooseBestAnswerResult = MutableLiveData<Boolean>()
    var checkFavoriteResult = MutableLiveData<Boolean?>()
    var isFavorite = false

    private var id = 0
    private var pagination = PaginationResponse(0, 1, 10)

    fun setLabId(labId: Int) {
        this.id = labId
    }

    fun getLabId(): Int = id

    fun loadLabDetail(isLoadMore: Boolean) {
        viewModelScope.launch {
            try {
                val params = HashMap<String, Any>()
                params[BUNDLE_KEY.PAGE] = pagination.page
                params[BUNDLE_KEY.LIMIT] = pagination.limit
                isLoading.value = !isLoadMore
                if (isLoadMore) {
                    visibleLoadMoreResult.postValue(SHOW_LOAD_MORE)
                }
                val response = apiInterface.getLabDetail(id, params)
                response.let {
                    // Set data
                    if (it.errors.isEmpty()) {
                        // Check load more
                        pagination = it.pagination
                        if (pagination.page * pagination.limit < pagination.total) {
                            enableLoadMoreResult.postValue(ENABLE_LOAD_MORE)
                            pagination.page += 1
                        } else {
                            enableLoadMoreResult.postValue(DISABLE_LOAD_MORE)
                        }
                        // Set data
                        if (!isLoadMore) {
                            totalAnswerResult.postValue(it.pagination.total)
                            noDataResult.postValue(it.dataResponse.answers.isEmpty())
                            labDetailResult.postValue(it.dataResponse)
                            listAnswerResult.value = it.dataResponse.answers
                        } else {
                            listAnswerResult.postValue(listAnswerResult.value?.plus(it.dataResponse.answers))
                        }
                    } else {
                        labDetailResult.postValue(null)
                    }
                }
            } catch (throwable: Throwable) {
                labDetailResult.postValue(null)
            } finally {
                isLoading.value = false
                if (isLoadMore) visibleLoadMoreResult.postValue(HIDDEN_LOAD_MORE)
            }
        }
    }

    fun chooseBestAnswer(answerId: Int) {
        viewModelScope.launch {
            try {
                val params = HashMap<String, Any>()
                params[BUNDLE_KEY.ANSWER_ID] = answerId
                val response = apiInterface.chooseBestAnswer(id, params)
                response.let {
                    chooseBestAnswerResult.postValue(it.errors.isEmpty())
                }
            } catch (throwable: Throwable) {
                chooseBestAnswerResult.postValue(false)
            }
        }
    }

    fun changeLabFavorite() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val isEnable = if (isFavorite) 0 else 1
                val params = HashMap<String, Any>()
                params[BUNDLE_KEY.IS_ENABLE] = isEnable
                val response = apiInterface.changeLabFavorite(id, params)
                response.let {
                    if (it.errors.isEmpty()) {
                        isFavorite = !isFavorite
                        checkFavoriteResult.postValue(isFavorite)
                    } else {
                        checkFavoriteResult.postValue(null)
                    }
                }
            } catch (throwable: Throwable) {
                checkFavoriteResult.postValue(null)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun resetDataLoadMore() {
        pagination = PaginationResponse(0, 1, 10)
    }
}