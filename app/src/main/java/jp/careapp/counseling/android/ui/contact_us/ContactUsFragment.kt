package jp.careapp.counseling.android.ui.contact_us

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.databinding.FragmentContactUsBinding
import javax.inject.Inject

@AndroidEntryPoint
class ContactUsFragment : BaseFragment<FragmentContactUsBinding, ContactUsViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_contact_us

    private val mViewModel: ContactUsViewModel by viewModels()
    override fun getVM() = mViewModel

    override fun initView() {
        super.initView()
    }
}