package jp.careapp.counseling.android.ui.calling

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.keystore.KeyService
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.network.socket.CallingWebSocketClient
import jp.careapp.counseling.android.utils.calling.CallSoundManager
import jp.careapp.counseling.android.utils.calling.MediaServerManager
import java.util.*
import javax.inject.Inject

const val TAG = "CallingViewModel"

@HiltViewModel
class CallingViewModel @Inject constructor(
    private val application: Application,
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences,
    private val callSoundManager: CallSoundManager,
    private val keyService: KeyService
) : BaseViewModel() {

    private val _isConfigAudioCall: MutableLiveData<Boolean?> = MutableLiveData(null)
    val isConfigAudioCall: MutableLiveData<Boolean?> = _isConfigAudioCall
    private val _performerInfo = MutableLiveData<PerformerInfo>(null)
    val performerInfo: MutableLiveData<PerformerInfo> = _performerInfo
    private val _isMuteMic = MutableLiveData(false)
    val isMuteMic: MutableLiveData<Boolean> = _isMuteMic
    private val _isMuteSpeaker = MutableLiveData(true)
    val isMuteSpeaker: MutableLiveData<Boolean> = _isMuteSpeaker
    private val _callDuration = MutableLiveData(0)
    val callDuration: MutableLiveData<Int> = _callDuration

    private lateinit var socketClient: CallingWebSocketClient
    private lateinit var mediaServer: MediaServerManager
    private val gson by lazy { Gson() }
    private var performer = PerformerInfo()
    private var previousAudioConfig: AudioConfig? = null
    private var timer: Timer? = null
    private var lastPoint = 0

    fun setPerformerInfo(name: String, performerCode: String, imageUrl: String) {
        performer.let {
            it.name = name
            it.performerCode = performerCode
            it.imageUrl = imageUrl
        }
        _performerInfo.value = performer
    }

    fun getPerformerInfo(): PerformerInfo {
        return performer
    }

    fun savePreviousAudioConfig(mode: Int, isSpeakerphoneOn: Boolean) {
        previousAudioConfig = AudioConfig(mode, isSpeakerphoneOn)
    }

    fun getPreviousAudioConfig(): AudioConfig? {
        return previousAudioConfig
    }

    private fun resetData() {
        performer = PerformerInfo()
        _isMuteMic.postValue(false)
        _isMuteSpeaker.postValue(true)
        _isConfigAudioCall.postValue(false)
        _callDuration.postValue(0)
        lastPoint = 0
    }

    fun isFullMode(): Boolean {
        return rxPreferences.isFullMode()
    }

}

data class AudioConfig(
    var mode: Int,
    var isSpeakerphoneOn: Boolean
)

data class PerformerInfo(
    var name: String = "",
    var performerCode: String = "",
    var imageUrl: String = "",
)