package jp.careapp.counseling.android.ui.contact_us

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.R
import jp.careapp.counseling.android.utils.CONTACT_US_MAIL
import jp.careapp.counseling.android.utils.CONTACT_US_MODE
import jp.careapp.counseling.android.utils.ContactUsMode
import javax.inject.Inject

const val REPLY_NOT_REQUIRED = 0
const val REPLY_REQUIRED = 1

@HiltViewModel
class ContactUsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val application: Application
) :
    BaseViewModel() {

    private val _statusBtnConfirm = MutableLiveData(false)
    val statusBtnConfirm: LiveData<Boolean>
        get() = _statusBtnConfirm

    private var mCategory = application.getString(R.string.contact_category_please_select)
    private var mContent = ""
    private var mReply = REPLY_REQUIRED

    private var contactUsMode = ContactUsMode.CONTACT_WITH_MAIL
    private var mail = ""

    val mActionState = SingleLiveEvent<ContactUsActionState>()

    init {
        mail = savedStateHandle.get<String>(CONTACT_US_MAIL) ?: ""
        contactUsMode =
            savedStateHandle.get<Int>(CONTACT_US_MODE) ?: ContactUsMode.CONTACT_WITHOUT_MAIL
    }

    fun setCategory(category: String) {
        mCategory = category
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
        mActionState.value = ContactUsActionState.NavigateToContactUsConfirm(
            mCategory,
            mContent,
            mReply,
            mail,
            contactUsMode
        )
    }
}

sealed class ContactUsActionState {
    class NavigateToContactUsConfirm(
        val category: String,
        val content: String,
        val reply: Int,
        val mail: String,
        val contactUsMode: Int
    ) :
        ContactUsActionState()
}