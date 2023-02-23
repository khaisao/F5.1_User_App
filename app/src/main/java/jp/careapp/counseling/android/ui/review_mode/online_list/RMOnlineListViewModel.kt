package jp.careapp.counseling.android.ui.review_mode.online_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.model.network.RMBlockListResponse
import jp.careapp.counseling.android.model.network.RMPerformerResponse
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.LoadMoreState
import jp.careapp.counseling.android.utils.extensions.toListData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RMOnlineListViewModel @Inject constructor(
    private val rmApiInterface: RMApiInterface
) : BaseViewModel() {
    private val _listPerformers = MutableLiveData<List<RMPerformerResponse>>(listOf())
    val listPerformers: LiveData<List<RMPerformerResponse>> = _listPerformers
    private val _loadMoreState = MutableLiveData<Int>()
    val loadMoreState: LiveData<Int> = _loadMoreState
    private val _iShowNoData = MutableLiveData(false)
    val iShowNoData: LiveData<Boolean> = _iShowNoData

    var page = 1

    init {
        getDummyPerformers(true)
    }

    fun getDummyPerformers(isShowLoading: Boolean = false, isLoadMore: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(isShowLoading)
            try {
                val blockList = withContext(Dispatchers.IO) { fetchBlocks() }
                blockList ?: return@launch

                if (!isLoadMore) page = 1
                val response = rmApiInterface.getDummyPerformers(getParamsRequest(page))
                withContext(Dispatchers.Main) {
                    if (response.errors.isEmpty()) {
                        response.dataResponse.let { listPerformers ->
                            when {
                                listPerformers.size < BUNDLE_KEY.LIMIT_20 -> {
                                    _loadMoreState.value = LoadMoreState.DISABLE_LOAD_MORE
                                }
                                else -> {
                                    _loadMoreState.value = LoadMoreState.ENABLE_LOAD_MORE
                                    page++
                                }
                            }
                            if (!isLoadMore) {
                                _listPerformers.value =
                                    removeBlockListIfNeed(blockList, listPerformers)
                            } else {
                                _listPerformers.value =
                                    removeBlockListIfNeed(
                                        blockList,
                                        _listPerformers.value?.plus(listPerformers) ?: listOf()
                                    )
                            }
                        }
                    }
                    _iShowNoData.value = _listPerformers.value.isNullOrEmpty()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.postValue(false)
                _loadMoreState.postValue(LoadMoreState.HIDDEN_LOAD_MORE)
            }
        }
    }

    private fun removeBlockListIfNeed(
        blockList: List<RMBlockListResponse>,
        performerList: List<RMPerformerResponse>
    ): List<RMPerformerResponse> {
        return performerList.filter { currentPerformer ->
            currentPerformer.code != blockList.find { it.code == currentPerformer.code }?.code
        }
    }

    private fun getParamsRequest(page: Int): HashMap<String, Any> {
        return HashMap<String, Any>().apply {
            put(BUNDLE_KEY.PARAM_SORT, BUNDLE_KEY.PRESENCE_STATUS)
            put(BUNDLE_KEY.PARAM_ODER, BUNDLE_KEY.DESC)
            put(BUNDLE_KEY.PARAM_SORT_2, BUNDLE_KEY.REVIEW_TOTAL_NUMBER)
            put(BUNDLE_KEY.PARAM_ODER_2, BUNDLE_KEY.DESC)
            put(BUNDLE_KEY.LIMIT, BUNDLE_KEY.LIMIT_20)
            put(BUNDLE_KEY.PAGE, page)
        }
    }

    private suspend fun fetchBlocks(): List<RMBlockListResponse>? {
        return try {
            val response = rmApiInterface.getBlockList()
            response.let {
                if (it.errors.isEmpty()) {
                    it.dataResponse.toListData()
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
}