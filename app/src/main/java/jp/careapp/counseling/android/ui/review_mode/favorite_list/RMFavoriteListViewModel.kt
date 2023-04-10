package jp.careapp.counseling.android.ui.review_mode.favorite_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.model.network.RMBlockListResponse
import jp.careapp.counseling.android.model.network.RMFavoriteResponse
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.ui.review_mode.user_detail.RMUserDetailRepository
import jp.careapp.counseling.android.utils.extensions.toListData
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class RMFavoriteListViewModel @Inject constructor(
    private val mRepository: RMFavoriteListRepository,
) : BaseViewModel() {

    private var favoriteList = arrayListOf<RMFavoriteResponse>()

    private val _favoriteListLiveData =
        MutableLiveData<ArrayList<RMFavoriteResponse>>(arrayListOf())
    val favoriteListLiveData: LiveData<ArrayList<RMFavoriteResponse>>
        get() = _favoriteListLiveData

    private val _isShowNoData = MutableLiveData<Boolean>()
    val isShowNoData: LiveData<Boolean> = _isShowNoData

    val mActionState = SingleLiveEvent<RMFavoriteListActionState>()

    init {
        getListFavorite()
    }

    fun getListFavorite() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val blockResponse = mRepository.getBlockList()
                    val favoriteResponse = mRepository.getFavoriteList()

                    if (favoriteResponse.errors.isEmpty()) {
                        val favoriteData =
                            favoriteResponse.dataResponse.toListData<RMFavoriteResponse>()

                        val blockData = if (blockResponse.errors.isEmpty()) {
                            blockResponse.dataResponse.toListData<RMBlockListResponse>()
                        } else {
                            listOf()
                        }

                        favoriteList = removeBlockListIfNeed(blockData, favoriteData)

                        withContext(Dispatchers.Main) {
                            _isShowNoData.value = checkShowNoData()
                            _favoriteListLiveData.value = favoriteList
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.Main) { isLoading.value = false }
                }
            }
        }
    }

    private fun removeBlockListIfNeed(
        blockList: List<RMBlockListResponse>,
        favoriteList: List<RMFavoriteResponse>
    ): ArrayList<RMFavoriteResponse> {
        return ArrayList(favoriteList.filter { currentFavorite -> currentFavorite.code != blockList.find { it.code == currentFavorite.code }?.code })
    }

    fun handleOnClickUser(position: Int) {
        favoriteList[position].code?.let {
            mActionState.value = RMFavoriteListActionState.NavigateToUserDetail(it)
        }
    }

    fun deleteFavorite(position: Int) {
        isLoading.value = true
        viewModelScope.launch {
            supervisorScope {
                try {
                    val userCode = favoriteList[position].code.toString()
                    val response = mRepository.deleteFavoriteUser(userCode)
                    if (response.errors.isEmpty()) {
                        favoriteList.forEach { user ->
                            if (user.code == userCode) {
                                favoriteList.remove(user)
                            }
                        }
                        withContext(Dispatchers.Main) {
                            _isShowNoData.value = checkShowNoData()
                            _favoriteListLiveData.value = favoriteList
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    withContext(Dispatchers.Main) { isLoading.value = false }
                }
            }
        }
    }

    private fun checkShowNoData(): Boolean {
        if (favoriteList.isEmpty()) {
            return true
        }
        return false
    }
}

sealed class RMFavoriteListActionState {
    class NavigateToUserDetail(val userCode: String) : RMFavoriteListActionState()
}