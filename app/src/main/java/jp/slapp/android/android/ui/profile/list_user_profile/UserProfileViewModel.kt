package jp.slapp.android.android.ui.profile.list_user_profile

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.network.MemberResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import kotlinx.coroutines.launch

class UserProfileViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {
    val memberInFoResult = MutableLiveData<MemberResponse?>()
    val blockUserResult = MutableLiveData<Boolean>()

    fun loadMemberInfo(activity: Activity) {
        viewModelScope.launch {
            try {
                val response = apiInterface.getMember()
                response.let {
                    if (it.errors.isEmpty()) {
                        memberInFoResult.value = it.dataResponse
                        rxPreferences.saveMemberInfo(it.dataResponse)
                    }
                }
            } catch (e: Exception) {
                handleThowable(
                    activity,
                    e,
                    reloadData = {
                        loadMemberInfo(
                            activity
                        )
                    }
                )
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
}
