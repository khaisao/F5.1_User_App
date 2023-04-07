package jp.careapp.counseling.android.ui.review_mode.terms_of_service

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.review_mode.privacy_policy.RMPrivacyPolicyListAdapter
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmTermsOfServiceBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMTermsOfServiceFragment :
    BaseFragment<FragmentRmTermsOfServiceBinding, RMTermsOfServiceViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_terms_of_service

    private val mViewModel: RMTermsOfServiceViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: RMPrivacyPolicyListAdapter? = null

    override fun initView() {
        super.initView()

        setUpToolBar()

        mAdapter = RMPrivacyPolicyListAdapter()
        binding.rcvTermsOfService.apply {
            adapter = mAdapter
            setHasFixedSize(true)
        }
        mViewModel.dataLiveData.observe(viewLifecycleOwner) {
            mAdapter?.submitList(it)
        }
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }
        })
    }

    override fun onDestroyView() {
        binding.rcvTermsOfService.adapter = null
        mAdapter = null
        super.onDestroyView()
    }
}