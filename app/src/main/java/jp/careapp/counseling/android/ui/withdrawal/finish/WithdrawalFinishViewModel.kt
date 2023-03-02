package jp.careapp.counseling.android.ui.withdrawal.finish

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class WithdrawalFinishViewModel @Inject constructor(private val mRepository: WithdrawalFinishRepository) :
    BaseViewModel() {

    init {
        saveListCategory()
    }

    private fun saveListCategory() {
        isLoading.value = true
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            supervisorScope {
                try {
                    val response = mRepository.getListCategory()
                    if (response.errors.isEmpty()) {
                        mRepository.saveListCategory(response.dataResponse)
                    }
                } catch (e: Exception) {
                    mRepository.saveListCategoryDummy()
                } finally {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }
}