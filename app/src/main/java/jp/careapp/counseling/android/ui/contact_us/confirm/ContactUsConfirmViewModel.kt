package jp.careapp.counseling.android.ui.contact_us.confirm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.R
import jp.careapp.counseling.android.ui.contact_us.REPLY_REQUIRED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val CONTACT_US_CATEGORY = "CONTACT_US_CATEGORY"
const val CONTACT_US_CONTENT = "CONTACT_US_CONTENT"
const val CONTACT_US_REPLY = "CONTACT_US_REPLY"

@HiltViewModel
class ContactUsConfirmViewModel @Inject constructor(
    private val mRepository: ContactUsConfirmRepository,
    private val savedStateHandle: SavedStateHandle,
    private val application: Application
) :
    BaseViewModel() {

    private val _category = MutableLiveData<String>()
    val category: LiveData<String>
        get() = _category

    private val _content = MutableLiveData<String>()
    val content: LiveData<String>
        get() = _content

    private val _replyString = MutableLiveData<String>()
    val replyString: LiveData<String>
        get() = _replyString

    private var reply = REPLY_REQUIRED

    val mActionState = SingleLiveEvent<ContactUsConfirmActionState>()

    init {
        _category.value = savedStateHandle.get<String>(CONTACT_US_CATEGORY)
        _content.value = savedStateHandle.get<String>(CONTACT_US_CONTENT)
        savedStateHandle.get<Int>(CONTACT_US_REPLY)?.let { reply = it }

        if (reply == REPLY_REQUIRED) {
            _replyString.value = application.getString(R.string.necessary)
        } else {
            _replyString.value = application.getString(R.string.unnecessary)
        }
    }

    fun sendContactUs() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response =
                        mRepository.sendContactUs(category.value!!, content.value!!, reply)
                    if (response.errors.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            mActionState.value = ContactUsConfirmActionState.SendContactUsSuccess
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

sealed class ContactUsConfirmActionState {
    object SendContactUsSuccess : ContactUsConfirmActionState()
}