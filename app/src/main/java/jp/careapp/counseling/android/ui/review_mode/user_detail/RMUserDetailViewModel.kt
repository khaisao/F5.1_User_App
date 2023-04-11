package jp.careapp.counseling.android.ui.review_mode.user_detail

import androidx.hilt.Assisted
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.model.network.RMUserDetailResponse
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RMUserDetailViewModel @Inject constructor(
    private val mRepository: RMUserDetailRepository,
    @Assisted
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private val _user = MutableLiveData<RMUserDetailResponse>()
    val user: LiveData<RMUserDetailResponse>
        get() = _user

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    val mActionState = SingleLiveEvent<RMUserDetailActionState>()

    var userCode: String = ""

    init {
        userCode = savedStateHandle.get<String>(BUNDLE_KEY.PERFORMER_CODE).toString()
        loadUserDetail(userCode)
    }

    private fun loadUserDetail(userCode: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = mRepository.loadUserDetail(userCode)
            withContext(Dispatchers.Main) {
                isLoading.value = false
                if (response.errors.isEmpty()) {
                    response.dataResponse.let { dataResponse ->
                        _user.value = dataResponse
                        dataResponse.isFavorite?.let { _isFavorite.value = it }
                    }
                }
            }
        }
    }

    fun blockUser() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = mRepository.blockUser(userCode)
            withContext(Dispatchers.Main) {
                isLoading.value = false
                if (response.errors.isEmpty()) {
                    mActionState.value = RMUserDetailActionState.BlockUserSuccess
                }
            }
        }
    }

    fun onClickUserFavorite() {
        if (_isFavorite.value == true) deleteFavoriteUser(userCode)
        else addFavoriteUser(userCode)
    }

    private fun addFavoriteUser(userCode: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = mRepository.addFavoriteUser(userCode)
            withContext(Dispatchers.Main) {
                isLoading.value = false
                if (response.errors.isEmpty()) {
                    _isFavorite.value = true
                    mActionState.value = RMUserDetailActionState.AddAndDeleteFavoriteSuccess
                }
            }
        }
    }

    private fun deleteFavoriteUser(userCode: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = mRepository.deleteFavoriteUser(userCode)
            withContext(Dispatchers.Main) {
                isLoading.value = false
                if (response.errors.isEmpty()) {
                    _isFavorite.value = false
                    mActionState.value = RMUserDetailActionState.AddAndDeleteFavoriteSuccess
                }
            }
        }
    }

    fun onClickMessageChat() {
        mActionState.value = RMUserDetailActionState.NavigateToUserDetailMessage(
            _user.value?.code.toString(),
            _user.value?.name.toString()
        )
    }

    fun onClickCallVideo() {}
}

sealed class RMUserDetailActionState {
    class NavigateToUserDetailMessage(val userCode: String, val userName: String) :
        RMUserDetailActionState()

    object BlockUserSuccess : RMUserDetailActionState()

    object AddAndDeleteFavoriteSuccess : RMUserDetailActionState()
}
