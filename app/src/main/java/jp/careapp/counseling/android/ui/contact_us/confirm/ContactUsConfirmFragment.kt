package jp.careapp.counseling.android.ui.contact_us.confirm

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.databinding.FragmentContactUsConfirmBinding
import javax.inject.Inject

@AndroidEntryPoint
class ContactUsConfirmFragment :
    BaseFragment<FragmentContactUsConfirmBinding, ContactUsConfirmViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_withdrawal

    private val mViewModel: ContactUsConfirmViewModel by viewModels()
    override fun getVM() = mViewModel
}