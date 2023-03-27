package jp.careapp.counseling.android.ui.review_mode.start

import android.os.Bundle
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.Define
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

        binding.btnStartNow.setOnClickListener { appNavigation.openRMStartToRMEnterName() }
        binding.tvTermsOfService.setOnClickListener {
            val bundle = Bundle().apply {
                putString(Define.TITLE_WEB_VIEW, getString(R.string.TermsOfService))
                putString(Define.URL_WEB_VIEW, Define.URL_TERMS)
            }
            appNavigation.openRMStartToTermOfService(bundle)
        }
        binding.tvPrivacyPolicy.setOnClickListener {
            val bundle = Bundle().apply {
                putString(Define.TITLE_WEB_VIEW, getString(R.string.PrivacyPolicy))
                putString(Define.URL_WEB_VIEW, Define.URL_PRIVACY)
            }
            appNavigation.openRMStartToPrivacyPolicy(bundle)
        }
    }
}