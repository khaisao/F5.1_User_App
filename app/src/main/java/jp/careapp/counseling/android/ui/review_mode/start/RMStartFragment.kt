package jp.careapp.counseling.android.ui.review_mode.start

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.databinding.FragmentRmStartBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMStartFragment : BaseFragment<FragmentRmStartBinding, RMStartViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_start

    private val mViewModel: RMStartViewModel by viewModels()
    override fun getVM(): RMStartViewModel = mViewModel

    override fun setOnClick() {
        super.setOnClick()

        binding.btnStartNow.setOnClickListener { if (!isDoubleClick) appNavigation.openRMStartToRMEnterName() }

        binding.tvTermsOfService.setOnClickListener { if (!isDoubleClick) appNavigation.openRMTermOfService() }

        binding.tvPrivacyPolicy.setOnClickListener { if (!isDoubleClick) appNavigation.openRMPrivacyPolicy() }
    }
}