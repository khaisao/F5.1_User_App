package jp.careapp.counseling.android.ui.registerName

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.network.ApiInterface

class RegisterNameViewModel @ViewModelInject constructor(
    private val apiService: ApiInterface,
) : BaseViewModel() {

    var isComeBackFromBackGround = MutableLiveData(false)

    fun setComebackFromBackGround(isComback: Boolean) {
        isComeBackFromBackGround.value = isComback
    }
}
