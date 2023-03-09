package jp.careapp.counseling.android.ui.contact_us

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.R
import javax.inject.Inject

const val REPLY_NOT_REQUIRED = 0
const val REPLY_REQUIRED = 1

@HiltViewModel
class ContactUsViewModel @Inject constructor(private val application: Application) :
    BaseViewModel() {

    private val _spinnerSelect = MutableLiveData(0)
    val spinnerSelect: LiveData<Int>
        get() = _spinnerSelect

    private val _statusBtnConfirm = MutableLiveData(false)
    val statusBtnConfirm: LiveData<Boolean>
        get() = _statusBtnConfirm

    private var mCategory = ""
    private var mContent = ""
    private var mReply = REPLY_REQUIRED

    val mActionState = SingleLiveEvent<ContactUsActionState>()

    fun setCategory(category: String, spinnerSelect: Int) {
        mCategory = category
        _spinnerSelect.value = spinnerSelect
        checkStatusBtnConfirm()
    }

    fun setContent(content: String) {
        mContent = content
        checkStatusBtnConfirm()
    }

    fun setReply(reply: Int) {
        mReply = reply
    }

    private fun checkStatusBtnConfirm() {
        _statusBtnConfirm.value =
            mCategory != application.getString(R.string.contact_category_please_select) && mContent.isNotBlank()
    }

    fun handleConfirmContent() {
        mActionState.value =
            ContactUsActionState.NavigateToContactUsConfirm(mCategory, mContent, mReply)
    }
}

sealed class ContactUsActionState {
    class NavigateToContactUsConfirm(val category: String, val content: String, val reply: Int) :
        ContactUsActionState()
}