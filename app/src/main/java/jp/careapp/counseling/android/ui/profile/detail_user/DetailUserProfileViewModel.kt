package jp.careapp.counseling.android.ui.profile.detail_user

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.base.NetworkException
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.GalleryResponse
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailUserProfileViewModel @ViewModelInject constructor(private val apiInterface: ApiInterface) :
    BaseViewModel() {
    val TAG = "DetailUserProfileViewModel"
    val userProfileResult = MutableLiveData<ConsultantResponse?>()
    val userGallery = MutableLiveData<List<GalleryResponse>?>()
    val statusFavorite = MutableLiveData<Boolean>()
    val statusRemoveFavorite = MutableLiveData<Boolean>()
    val isFirstChat = MutableLiveData<Boolean>()


    private val _newMessage = MutableLiveData<SocketActionSend>()
    val blockUserResult = MutableLiveData<Boolean>()

    val newMessage: LiveData<SocketActionSend> get() = _newMessage
    fun loadDetailUser(code: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.getUserProfileDetail(code)
                val galleryResponse = apiInterface.getUserGallery(code)
                response.let {
                    if (it.errors.isEmpty()) {
                        userProfileResult.postValue(it.dataResponse)
                    } else {
                        userProfileResult.postValue(null)
                    }
                }
                galleryResponse.let {
                    if (it.errors.isEmpty()) {
                        userGallery.postValue(it.dataResponse)
                    } else {
                        userGallery.postValue(null)
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                userProfileResult.postValue(null)
                userGallery.postValue(null)
                isLoading.value = false
            }
        }
    }

    fun handleClickBlock(performerCode: String, activity: Activity) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.handleClickblock(performerCode)
                response.let {
                    if (it.errors.isEmpty()) {
                        blockUserResult.value = true
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                handleThowable(
                    activity,
                    e,
                    reloadData = {
                        handleClickBlock(
                            performerCode,
                            activity
                        )
                    }
                )
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
                        isFirstChat.value = response.dataResponse.isEmpty()
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                isLoading.value = false
            }
        }
    }

}
