package jp.careapp.counseling.android.ui.tutorial

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import jp.careapp.core.base.BaseViewModel
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.TutorialItem

class TutorialViewModel @ViewModelInject constructor() : BaseViewModel() {
    val listTutorial = MutableLiveData<List<TutorialItem>>()

    init {
        getDataTutorial()
    }

    private fun getDataTutorial() {
        val list = ArrayList<TutorialItem>()
        list.add(
            TutorialItem(
                R.string.title_step_1,
                R.string.content_step_1,
                R.drawable.ic_sheep
            )
        )
        list.add(
            TutorialItem(
                R.string.title_step_2,
                R.string.content_step_2,
                R.drawable.logo_new_step_3
            )
        )
        listTutorial.value = list
    }
}
