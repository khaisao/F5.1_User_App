package jp.slapp.android.android.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import jp.careapp.core.base.BaseViewModel
import jp.slapp.android.android.data.model.LoginRequest
import jp.slapp.android.android.data.network.ApiObjectResponse
import jp.slapp.android.android.data.network.LoginResponse
import jp.slapp.android.android.data.network.socket.SocketActionSend
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.network.ApiInterface
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.event.Event
import jp.slapp.android.android.utils.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    private val _newMessage = MutableLiveData<SocketActionSend>()
    val newMessage: LiveData<SocketActionSend> get() = _newMessage
    private val _mail = MutableLiveData<String>()
    val getMail: LiveData<String> = _mail
    fun setMail(mail: String) {
        _mail.value = mail
    }
    private var newFcmToken: String? = null
    var isSuccess = MutableLiveData(false)

    fun registerDeviceToken(newFcmToken: String) {
        this.newFcmToken = newFcmToken
        viewModelScope.launch {
            try {
                val response = apiInterface.registerDeviceToken(BUNDLE_KEY.OS_ANDROID, newFcmToken)
                isSuccess.value = response.errors.isEmpty()
            } catch (throwable: Throwable) {
                isSuccess.value = false
            }
        }
    }

    fun saveDeviceToken() {
        newFcmToken?.let { rxPreferences.saveDeviceToken(it) }
    }

    fun connectSocket() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onNewMessageEvent(event: SocketActionSend) {
        _newMessage.value = event
    }

    private val _paramsLogin = MutableLiveData<LoginRequest>()
    fun setParamsLogin(loginRequest: LoginRequest) {
        _paramsLogin.value = loginRequest
    }
    private val _refreshToken: LiveData<Result<ApiObjectResponse<LoginResponse>>> =
        _paramsLogin.switchMap { data ->
            liveData {
                emit(Result.Loading)
                emit(
                    login(
                        data.email,
                        data.password,
                        data.device_name
                    )
                )
            }
        }
    val loginSuccess: LiveData<LoginResponse> = _refreshToken.map {
        (it as? Result.Success)?.data?.dataResponse ?: LoginResponse()
    }
    private suspend fun login(email: String, password: String, deviceName: String): Result<ApiObjectResponse<LoginResponse>> {
        return try {
            withContext(Dispatchers.IO) {
                apiInterface.login(email, password, deviceName).let {
                    Result.Success(it)
                }
            }
        } catch (e: java.lang.Exception) {
            Result.Error(e)
        }
    }

    private val _error = MediatorLiveData<Event<String>>().apply {
        addSource(_refreshToken) { rs ->
            if (rs is Result.Error)
                value = Event(content = rs.throwable.message ?: "")
        }
    }
    val error: LiveData<Event<String>> = _error
}
