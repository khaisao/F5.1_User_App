package jp.careapp.counseling.android.ui.review_mode.user_detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.model.network.RMUserDetailResponse
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RMUserDetailViewModel @ViewModelInject constructor(
    private val mRepository: RMUserDetailRepository,
    @Assisted
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val actionState = SingleLiveEvent<ActionState>()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private val _user = MutableLiveData<RMUserDetailResponse>()
    val user: LiveData<RMUserDetailResponse> = _user

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

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
                    actionState.value = ActionState.BlockUserSuccess(true)
                } else {
                    actionState.value = ActionState.BlockUserSuccess(false)
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
                    actionState.value = ActionState.AddAndDeleteFavoriteSuccess(true)
                } else {
                    actionState.value = ActionState.AddAndDeleteFavoriteSuccess(false)
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
                    actionState.value = ActionState.AddAndDeleteFavoriteSuccess(true)
                } else {
                    actionState.value = ActionState.AddAndDeleteFavoriteSuccess(false)
                }
            }
        }
    }
}
