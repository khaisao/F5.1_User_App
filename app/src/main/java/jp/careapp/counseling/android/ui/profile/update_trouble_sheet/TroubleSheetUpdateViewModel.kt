package jp.careapp.counseling.android.ui.profile.update_trouble_sheet

import android.app.Activity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.ConverterUtils.Companion.defaultValue
import jp.careapp.core.utils.SingleLiveEvent
import jp.careapp.counseling.android.data.model.ParamsUpdateMember
import jp.careapp.counseling.android.data.model.user_profile.TroubleSheetRequest
import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.network.TroubleSheetResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TroubleSheetUpdateViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {
    val statusSendTrouble = SingleLiveEvent<Boolean>()
    val memberInfoResult = MutableLiveData<MemberResponse?>()
    private val _genresResult = MutableLiveData<List<CategoryResponse>>()
    val genresResult: MutableLiveData<List<CategoryResponse>> = _genresResult
    private val _isEnableButtonUpdate = MutableLiveData<Boolean>()
    val isEnableButtonUpdate: MutableLiveData<Boolean> = _isEnableButtonUpdate
    private var paramUpdateMember: ParamsUpdateMember? = null
    private var paramUpdatePartner: ParamsUpdateMember? = null
    private var paramUpdateTroubleSheet = TroubleSheetRequest()
    private var paramGenreId: Int = 0
    private var isLoadingData = true

    init {
        loadGenres()
    }

    private fun loadGenres() {
        val items = mutableListOf<CategoryResponse>()
        rxPreferences.getListCategory()?.let {
            for (genre in it) {
                if (genre.registEnable) items.add(genre)
            }
        }
        _genresResult.value = items
    }

    fun getMemberInfo(activity: Activity) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.getMember()
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse.let { member ->
                            memberInfoResult.value = member
                            paramUpdateMember =
                                ParamsUpdateMember(member.name, member.sex, member.birth)
                            member.troubleSheetResponse.let { troubleSheet ->
                                paramUpdatePartner = ParamsUpdateMember(
                                    troubleSheet.partnerName ?: "",
                                    troubleSheet.partnerSex ?: 0,
                                    troubleSheet.partnerBirth ?: ""
                                )
                                paramUpdateTroubleSheet = TroubleSheetRequest(
                                    troubleSheet.content,
                                    troubleSheet.response,
                                    troubleSheet.reply
                                )
                                paramGenreId = troubleSheet.genre ?: 0
                            }
                            isLoadingData = false
                            changeStateButtonUpdate()
                        }
                    }
                }
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                handleThowable(
                    activity,
                    e,
                    reloadData = {
                        getMemberInfo(
                            activity
                        )
                    }
                )
            }
        }
    }

    fun setGenre(position: Int) {
        _genresResult.value?.let {
            paramGenreId = it[position].id
            changeStateButtonUpdate()
        }
    }

    fun changeMemberInfo(name: String, sex: Int, birthday: String) {
        paramUpdateMember = ParamsUpdateMember(name, sex, birthday)
        changeStateButtonUpdate()
    }

    fun changePartnerInfo(name: String, sex: Int, birthday: String) {
        paramUpdatePartner = ParamsUpdateMember(name, sex, birthday)
        changeStateButtonUpdate()
    }

    fun changeTroubleSheet(content: String, response: Int, reply: Int = 1) {
        paramUpdateTroubleSheet = TroubleSheetRequest(content, response, reply)
        changeStateButtonUpdate()
    }

    private fun changeStateButtonUpdate() {
        if (isLoadingData) return
        memberInfoResult.value?.let {
            if (paramUpdateTroubleSheet.content.length > 128) {
                _isEnableButtonUpdate.value = false
                return
            }
            if (it.name != paramUpdateMember?.name || it.sex != paramUpdateMember?.gender || it.birth != paramUpdateMember?.birth) {
                _isEnableButtonUpdate.value = true
                return
            }
            it.troubleSheetResponse.apply {
                if (paramGenreId != genre) {
                    _isEnableButtonUpdate.value = true
                    return
                }
                if (partnerName != paramUpdatePartner?.name || partnerSex.defaultValue() != paramUpdatePartner?.gender || partnerBirth.defaultValue() != paramUpdatePartner?.birth) {
                    _isEnableButtonUpdate.value = true
                    return
                }
                if (content != paramUpdateTroubleSheet.content || response != paramUpdateTroubleSheet.response || reply != paramUpdateTroubleSheet.reply) {
                    _isEnableButtonUpdate.value = true
                    return
                }
            }
        }
        _isEnableButtonUpdate.value = false
    }

    fun updateInfo(activity: Activity) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                withContext(viewModelScope.coroutineContext) {
                    updateTroubleGenre()
                    updateMemberInfo()
                    updatePartnerInfo()
                    updateTroubleSheet()
                }
                statusSendTrouble.postValue(true)
            } catch (e: Exception) {
                isLoading.postValue(false)
                statusSendTrouble.postValue(false)
                handleThowable(activity, e) {
                    updateInfo(activity)
                }
            }
        }
    }

    private suspend fun updateTroubleGenre() {
        apiInterface.updateGenre(paramGenreId).apply {
            if (errors.isEmpty()) {
                rxPreferences.setGenre(paramGenreId)
            }
        }
    }

    private suspend fun updateMemberInfo() {
        paramUpdateMember?.let {
            apiInterface.updateProfile(it)
        }
    }

    private suspend fun updatePartnerInfo() {
        paramUpdatePartner?.let {
            val params = HashMap<String, Any>()
            params["partner_name"] = it.name
            if (it.gender != 0) {
                params["partner_sex"] = it.gender
            }
            params["partner_birth"] = it.birth
            apiInterface.updatePartnerInfo(params)
        }
    }

    private suspend fun updateTroubleSheet() {
        apiInterface.sendTroubleSheet(paramUpdateTroubleSheet).apply {
            if (errors.isEmpty()) {
                rxPreferences.saveTroubleSheet(
                    TroubleSheetResponse(
                        paramUpdateTroubleSheet.content,
                        paramUpdateTroubleSheet.response,
                        paramUpdateTroubleSheet.reply
                    )
                )
            }
        }
    }
}
