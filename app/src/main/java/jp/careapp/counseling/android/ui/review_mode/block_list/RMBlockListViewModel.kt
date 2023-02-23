package jp.careapp.counseling.android.ui.review_mode.block_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.model.network.RMBlockListResponse
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.utils.extensions.toListData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RMBlockListViewModel @Inject constructor(
    private val rmApiInterface: RMApiInterface
) : BaseViewModel() {
    private val _listBlock = MutableLiveData<List<RMBlockListResponse>>()
    val listBlock: LiveData<List<RMBlockListResponse>> = _listBlock
    private val _iShowNoData = MutableLiveData<Boolean>()
    val iShowNoData: LiveData<Boolean> = _iShowNoData

    init {
        getListBlock()
    }

    private fun getListBlock() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                val response = rmApiInterface.getBlockList()
                withContext(Dispatchers.Main) {
                    if (response.errors.isEmpty()) {
                        response.dataResponse.let {
                            val listBlocks = it.toListData<RMBlockListResponse>()
                            _listBlock.value = listBlocks
                        }
                    } else {
                        _listBlock.value = listOf()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.postValue(false)
                _iShowNoData.postValue(_listBlock.value.isNullOrEmpty())
            }
        }
    }

    fun deleteBlock(code: String) {
        try {
            isLoading.value = true
            viewModelScope.launch(Dispatchers.IO) {
                val response = rmApiInterface.deleteBlock(code)
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    if (response.errors.isEmpty()) {
                        _listBlock.value = _listBlock.value?.filterNot { it.code == code }
                        _iShowNoData.value = _listBlock.value.isNullOrEmpty()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}