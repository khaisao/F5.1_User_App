package jp.careapp.counseling.android.ui.contact_confirm

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.model.ContactMemberRequest
import jp.careapp.counseling.android.data.model.ContactRequest
import jp.careapp.counseling.android.data.model.ContactRequestWithoutMail
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.event.Event
import jp.careapp.counseling.android.utils.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConfirmContactViewModel @ViewModelInject constructor(
    private val apiService: ApiInterface
) : BaseViewModel() {

    private val _paramsContact = MutableLiveData<ContactRequest>()
    fun setParamsContact(contactRequest: ContactRequest) {
        _paramsContact.value = contactRequest
    }

    private val _addContact: LiveData<Result<ApiObjectResponse<Any>>> =
        _paramsContact.switchMap { data ->
            liveData {
                emit(Result.Loading)
                emit(addContact(data))
            }
        }
    val addContactLoading: LiveData<Boolean> = _addContact.map {
        it == Result.Loading
    }
    val addContactSuccess: LiveData<ContactRequest> = _addContact.switchMap {
        if (it is Result.Success)
            _paramsContact
        else
            _paramsContact
    }

    private suspend fun addContact(contactRequest: ContactRequest): Result<ApiObjectResponse<Any>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.addContact(contactRequest).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private val _paramsContactWithoutMail = MutableLiveData<ContactRequestWithoutMail>()
    fun setParamsContactWithoutMail(request: ContactRequestWithoutMail) {
        _paramsContactWithoutMail.value = request
    }

    private val _addContactWithoutMail: LiveData<Result<ApiObjectResponse<Any>>> =
        _paramsContactWithoutMail.switchMap { data ->
            liveData {
                emit(Result.Loading)
                emit(addContactWithoutEmail(data))
            }
        }
    val addContactWithoutMailLoading: LiveData<Boolean> = _addContactWithoutMail.map {
        it == Result.Loading
    }
    val addContactWithoutMailSuccess: LiveData<ContactRequestWithoutMail> =
        _addContactWithoutMail.switchMap {
            if (it is Result.Success)
                _paramsContactWithoutMail
            else
                _paramsContactWithoutMail
        }

    private suspend fun addContactWithoutEmail(contactRequest: ContactRequestWithoutMail): Result<ApiObjectResponse<Any>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.addContactWithoutMail(contactRequest).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private val _paramsContactMember = MutableLiveData<ContactMemberRequest>()
    fun setParamsContactMember(contactMemberRequest: ContactMemberRequest) {
        _paramsContactMember.value = contactMemberRequest
    }

    private val _addContactMember: LiveData<Result<ApiObjectResponse<Any>>> =
        _paramsContactMember.switchMap { data ->
            liveData {
                emit(Result.Loading)
                emit(addContactMember(data))
            }
        }
    val addContactMemberSuccess: LiveData<ContactMemberRequest> = _addContactMember.switchMap {
        if (it is Result.Success)
            _paramsContactMember
        else
            _paramsContactMember
    }
    val addContactMemberLoading: LiveData<Boolean> = _addContact.map {
        it == Result.Loading
    }
    private suspend fun addContactMember(contactMemberRequest: ContactMemberRequest): Result<ApiObjectResponse<Any>> {
        return try {
            withContext(Dispatchers.IO) {
                apiService.addContactMember(contactMemberRequest).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private val _error = MediatorLiveData<Event<String>>().apply {
        addSource(_addContactMember) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
        addSource(_addContact) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
    }
    val error: LiveData<Event<String>> = _error
}
