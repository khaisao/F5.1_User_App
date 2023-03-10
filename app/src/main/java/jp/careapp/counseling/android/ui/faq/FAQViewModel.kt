package jp.careapp.counseling.android.ui.faq

import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class FAQViewModel @Inject constructor(private val mRepository: FAQRepository) : BaseViewModel() {
}