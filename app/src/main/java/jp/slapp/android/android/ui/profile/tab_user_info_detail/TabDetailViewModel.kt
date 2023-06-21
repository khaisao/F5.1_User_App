package jp.slapp.android.android.ui.profile.tab_user_info_detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.network.ApiInterface
import kotlinx.coroutines.launch

class TabDetailViewModel @ViewModelInject constructor(private val apiInterface: ApiInterface) :
    BaseViewModel() {
    val userProfileResult = MutableLiveData<ConsultantResponse?>()

    fun loadDetailUser(code: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.getUserProfileDetail(code)
                response.let {
                    if (it.errors.isEmpty()) {
                        userProfileResult.value = it.dataResponse
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                isLoading.value = false
            }
        }
    }
}
