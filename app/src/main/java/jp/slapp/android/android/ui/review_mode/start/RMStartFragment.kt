package jp.slapp.android.android.ui.review_mode.start

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.databinding.FragmentRmStartBinding
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

        binding.tvTermsOfService.setOnClickListener { if (!isDoubleClick) appNavigation.openRmStartToRmTerm() }

        binding.tvPrivacyPolicy.setOnClickListener { if (!isDoubleClick) appNavigation.openRmStartToRmPolicy() }
    }
}