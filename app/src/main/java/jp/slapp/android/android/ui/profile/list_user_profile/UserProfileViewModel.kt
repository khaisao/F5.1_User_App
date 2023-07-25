package jp.slapp.android.android.ui.profile.list_user_profile

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.network.MemberResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.utils.formatDecimalSeparator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class UserProfileViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {
    val memberInFoResult = MutableLiveData<MemberResponse?>()
    val blockUserResult = MutableLiveData<Boolean>()
    val currentPoint = MutableStateFlow("")

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

    fun getMemberInfo() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = apiInterface.getMember()
                    if (response.errors.isEmpty()) {
                        val dataResponse = response.dataResponse
                        rxPreferences.saveMemberInfoEditProfile(
                            dataResponse.name,
                            dataResponse.mail,
                            dataResponse.age,
                            dataResponse.birth,
                            dataResponse.sex,
                            dataResponse.point,
                            dataResponse.pushMail,
                            dataResponse.receiveNoticeMail,
                            dataResponse.receiveNewsletterMail
                        )
                        withContext(Dispatchers.Main) {
                            currentPoint.value = rxPreferences.getPoint().formatDecimalSeparator()
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
}
