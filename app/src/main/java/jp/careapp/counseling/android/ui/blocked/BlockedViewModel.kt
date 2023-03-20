package jp.careapp.counseling.android.ui.blocked

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.network.ApiObjectResponse
import jp.careapp.counseling.android.data.network.FavoriteResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BlockedViewModel @Inject constructor(
    private val mRepository: BlockedRepository
) : BaseViewModel() {

    private val memberList = arrayListOf<FavoriteResponse>()

    private val _blockedList = MutableLiveData<List<FavoriteResponse>>()
    val blockedList: LiveData<List<FavoriteResponse>>
        get() = _blockedList

    private val _isShowNoData = MutableLiveData<Boolean>()
    val isShowNoData: LiveData<Boolean>
        get() = _isShowNoData

    val mActionState = SingleLiveEvent<BlockedActionState>()

    init {
        getMemberBlocked()
    }

    private fun getMemberBlocked() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.getMemberBlocked()
                    val typeResponse =
                        object : TypeToken<ApiObjectResponse<List<FavoriteResponse>>>() {}.type
                    val data = Gson().fromJson(
                        response.toString(), typeResponse
                    ) as ApiObjectResponse<List<FavoriteResponse>>
                    if (data.errors.isEmpty()) {
                        memberList.addAll(data.dataResponse)
                        withContext(Dispatchers.Main) {
                            _isShowNoData.value = false
                            _blockedList.value = memberList
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        _isShowNoData.value = true
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun deleteBlocked(code: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.deleteBlocked(code)
                    if (response.errors.isEmpty()) {
                        memberList.removeIf { it.code == code }
                        if (memberList.isEmpty()) {
                            withContext(Dispatchers.Main) {
                                _isShowNoData.value = true
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                _isShowNoData.value = false
                                _blockedList.value = memberList
                            }
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

    fun onClickShowDialogConfirmDeleteBlock(position: Int) {
        val member = memberList[position]
        mActionState.value =
            BlockedActionState.ShowDialogConfirmDeleteBlock(member.name, member.code)
    }
}

sealed class BlockedActionState {
    class ShowDialogConfirmDeleteBlock(val memberName: String, val memberCode: String) :
        BlockedActionState()
}
