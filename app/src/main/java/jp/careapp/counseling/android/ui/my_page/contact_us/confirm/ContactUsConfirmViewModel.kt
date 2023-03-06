package jp.careapp.counseling.android.ui.my_page.contact_us.confirm

import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ContactUsConfirmViewModel @Inject constructor(private val mRepository: ContactUsConfirmRepository) :
    BaseViewModel()