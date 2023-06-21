package jp.slapp.android.android.ui.profile.block_report

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.network.ApiInterface
import kotlinx.coroutines.launch

class ReportViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface
) : BaseViewModel() {
    val reportUserResult = MutableLiveData<Boolean>()

    fun sendReport(performerCode: String, content: String, activity: Activity) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.sendReport(performerCode, content)
                response.let {
                    if (it.errors.isEmpty()) {
                        reportUserResult.value = true
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                handleThowable(
                    activity,
                    e,
                    reloadData = {
                        sendReport(performerCode, content, activity)
                    }
                )
            }
        }
    }
}
