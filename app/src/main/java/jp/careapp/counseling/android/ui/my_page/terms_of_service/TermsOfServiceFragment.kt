package jp.careapp.counseling.android.ui.my_page.terms_of_service

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.my_page.privacy_policy.PrivacyPolicyListAdapter
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentTermsOfServiceBinding
import javax.inject.Inject

@AndroidEntryPoint
class TermsOfServiceFragment :
    BaseFragment<FragmentTermsOfServiceBinding, TermsOfServiceViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_terms_of_service

    private val mViewModel: TermsOfServiceViewModel by viewModels()
    override fun getVM() = mViewModel

    private val mAdapter by lazy { PrivacyPolicyListAdapter() }

    override fun initView() {
        super.initView()

        setUpToolBar()

        binding.rcvTermsOfService.apply {
            itemAnimator = null
            adapter = mAdapter
        }
        mViewModel.dataLiveData.observe(viewLifecycleOwner) {
            mAdapter.submitList(it)
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
}