package jp.slapp.android.android.ui.terms_of_service

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.privacy_policy.PrivacyPolicyListAdapter
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentTermsOfServiceBinding
import javax.inject.Inject

@AndroidEntryPoint
class TermsOfServiceFragment :
    BaseFragment<FragmentTermsOfServiceBinding, TermsOfServiceViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_terms_of_service

    private val mViewModel: TermsOfServiceViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: PrivacyPolicyListAdapter? = null

    override fun initView() {
        super.initView()

        setUpToolBar()

        mAdapter = PrivacyPolicyListAdapter()
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