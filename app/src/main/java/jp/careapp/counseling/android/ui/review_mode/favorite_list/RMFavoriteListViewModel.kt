package jp.careapp.counseling.android.ui.review_mode.favorite_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.model.network.RMBlockListResponse
import jp.careapp.counseling.android.model.network.RMFavoriteResponse
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.ui.review_mode.userDetail.RMUserDetailRepository
import jp.careapp.counseling.android.utils.extensions.toListData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RMFavoriteListViewModel @Inject constructor(
    private val rmApiInterface: RMApiInterface,
    private val mRepository: RMUserDetailRepository,
) : BaseViewModel() {
    private val _listFavorite = MutableLiveData<List<RMFavoriteResponse>>(listOf())
    val listFavorite: LiveData<List<RMFavoriteResponse>> = _listFavorite
    private val _iShowNoData = MutableLiveData(false)
    val iShowNoData: LiveData<Boolean> = _iShowNoData

    init {
        getListFavorite()
    }

    fun getListFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                val blockList = withContext(Dispatchers.IO) { fetchBlocks() }
                blockList ?: return@launch

                val response = rmApiInterface.getFavorites()
                withContext(Dispatchers.Main) {
                    if (response.errors.isEmpty()) {
                        response.dataResponse.let {
                            _listFavorite.value = removeBlockListIfNeed(blockList, it.toListData())
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _iShowNoData.postValue(_listFavorite.value.isNullOrEmpty())
                isLoading.postValue(false)
            }
        }
    }

    private fun removeBlockListIfNeed(
        blockList: List<RMBlockListResponse>,
        favoriteList: List<RMFavoriteResponse>
    ): List<RMFavoriteResponse> {
        return favoriteList.filter { currentFavorite ->
            currentFavorite.code != blockList.find { it.code == currentFavorite.code }?.code
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

    fun deleteFavorite(code: String) {
        try {
            isLoading.value = true
            viewModelScope.launch(Dispatchers.IO) {
                val response = mRepository.deleteFavoriteUser(code)
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    if (response.errors.isEmpty()) {
                        _listFavorite.value = _listFavorite.value?.filterNot { it.code == code }
                        _iShowNoData.value = _listFavorite.value.isNullOrEmpty()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}