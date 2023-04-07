package jp.careapp.counseling.android.ui.review_mode.settingContact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.network.RMApiInterface
import jp.careapp.counseling.android.ui.review_mode.settingContact.RMSettingContactFragment.Companion.REPLY_REQUIRED
import jp.careapp.counseling.android.utils.ActionState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RMSettingContactViewModel @Inject constructor(
    private val rmApiInterface: RMApiInterface
) : BaseViewModel() {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    val actionState = SingleLiveEvent<ActionState>()

    private val _isEnableBtnSend = MutableLiveData<Boolean>()
    val isEnableBtnSend: LiveData<Boolean> = _isEnableBtnSend

    private var mCategory = ""
    private var mContent = ""
    private var mReply = REPLY_REQUIRED

    fun sendContact() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response =
                rmApiInterface.sendContact(mCategory, mContent, mReply)
            withContext(Dispatchers.Main) {
                isLoading.value = false
                val isError = response.errors.isEmpty()
                actionState.value = ActionState.SendContactSuccess(isError)
            }
        }
    }

    fun setCategory(category: String) {
        mCategory = category
        _isEnableBtnSend.value =
            mCategory.isNotEmpty() && mContent.isNotEmpty()
    }

    fun setContent(content: String) {
        mContent = content
        _isEnableBtnSend.value =
            mCategory.isNotEmpty() && mContent.isNotEmpty()
    }

    fun setReply(reply: Int) {
        mReply = reply
    }
}