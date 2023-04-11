package jp.careapp.counseling.android.ui.review_mode.block_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.model.network.RMBlockListResponse
import jp.careapp.counseling.android.utils.extensions.toListData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RMBlockListViewModel @Inject constructor(
    private val mRepository: RMBlockListRepository
) : BaseViewModel() {

    private var blockList = arrayListOf<RMBlockListResponse>()

    private val _blockListLiveData = MutableLiveData<ArrayList<RMBlockListResponse>>()
    val blockListLiveData: LiveData<ArrayList<RMBlockListResponse>> = _blockListLiveData

    private val _isShowNoData = MutableLiveData<Boolean>()
    val isShowNoData: LiveData<Boolean> = _isShowNoData

    init {
        getListBlock()
    }

    private fun getListBlock() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.getBlockList()
                    if (response.errors.isEmpty()) {
                        blockList =
                            ArrayList(response.dataResponse.toListData<RMBlockListResponse>())
                    }
                    withContext(Dispatchers.Main) {
                        _isShowNoData.value = checkShowNoData()
                        _blockListLiveData.value = blockList
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun deleteBlock(position: Int) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val userCode = blockList[position].code.toString()
                    val response = mRepository.deleteBlock(userCode)
                    if (response.errors.isEmpty()) {
                        blockList.removeAt(position)
                        withContext(Dispatchers.Main) {
                            _isShowNoData.value = checkShowNoData()
                            _blockListLiveData.value = blockList
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    private fun checkShowNoData(): Boolean {
        if (blockList.isEmpty()) {
            return true
        }
        return false
    }
}