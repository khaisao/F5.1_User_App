package jp.careapp.counseling.android.ui.profile.detail_user

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.base.NetworkException
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch

class DetailUserProfileViewModel @ViewModelInject constructor(private val apiInterface: ApiInterface) :
    BaseViewModel() {
    val TAG = "DetailUserProfileViewModel"
    val userProfileResult = MutableLiveData<ConsultantResponse?>()
    val statusFavorite = MutableLiveData<Boolean>()
    val statusRemoveFavorite = MutableLiveData<Boolean>()
    val isFirstChat = MutableLiveData<Boolean>()
    private val _newMessage = MutableLiveData<SocketActionSend>()
    val newMessage: LiveData<SocketActionSend> get() = _newMessage
    fun loadDetailUser(code: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.getUserProfileDetail(code)
                response.let {
                    if (it.errors.isEmpty()) {
                        userProfileResult.postValue(it.dataResponse)
                    } else {
                        userProfileResult.postValue(null)
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                userProfileResult.postValue(null)
                isLoading.value = false
            }
        }
    }

    /**
     * add user to list profile
     */
    fun addUserToFavorite(code: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.addUserToFavorite(code)
                response.let {
                    statusFavorite.value = it.errors.isEmpty()
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                statusFavorite.value = false
                isLoading.value = false
                if (throwable !is NetworkException) {
                    loadDetailUser(code)
                }
            }
        }
    }

    /**
     * remove usser in list profile
     */
    fun removeUserToFavorite(code: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.removeUserToFavorite(code)
                response.let {
                    statusRemoveFavorite.value = it.errors.isEmpty()
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                statusRemoveFavorite.value = false
                isLoading.value = false
                if (throwable !is NetworkException) {
                    loadDetailUser(code)
                }
            }
        }
    }

    /**
     * if count email == 0 => don't send message
     */
    fun loadMailInfo(code: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.getEmailSentByMember(code)
                response.let {
                    if (it.errors.isEmpty()) {
                        isFirstChat.value = response.dataResponse.isNullOrEmpty()
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                isLoading.value = false
            }
        }
    }

}
