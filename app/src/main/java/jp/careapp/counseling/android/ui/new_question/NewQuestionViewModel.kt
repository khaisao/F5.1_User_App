package jp.careapp.counseling.android.ui.new_question

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.android.data.model.NewQuestionRequest
import jp.careapp.counseling.android.data.model.message.SendMessageResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.network.ApiInterface
import jp.careapp.counseling.android.utils.Define
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NewQuestionViewModel @ViewModelInject constructor(
    private val apiInterface: ApiInterface,
    private val rxPreferences: RxPreferences
) : BaseViewModel() {

    private val newQuestionEventChannel = Channel<NewQuestionEvent>()
    val newQuestionEvent = newQuestionEventChannel.receiveAsFlow()

    val countContentCharacter = MutableLiveData<Int>()

    fun sendNewQuestion(newQuestionRequest: NewQuestionRequest) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiInterface.senNewQuestion(newQuestionRequest)
                response.let {
                    if (it.errors.isEmpty()) {
                        it.dataResponse?.let { data ->
                            newQuestionEventChannel.send(NewQuestionEvent.SendNewQuestionResult(data))
                        }
                    }
                }
                isLoading.value = false
            } catch (throwable: Throwable) {
                isLoading.value = false

            }
        }
    }

    sealed class NewQuestionEvent {
        data class SendNewQuestionResult(val data: SendMessageResponse) : NewQuestionEvent()
    }
}