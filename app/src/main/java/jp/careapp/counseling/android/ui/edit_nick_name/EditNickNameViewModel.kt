package jp.careapp.counseling.android.ui.edit_nick_name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class EditNickNameViewModel @Inject constructor(private val mRepository: EditNickNameRepository) :
    BaseViewModel() {

    private val _memberNickName = MutableLiveData<String>()
    val memberNickName: LiveData<String>
        get() = _memberNickName

    init {

    }
}