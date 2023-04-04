package jp.careapp.counseling.android.ui.review_mode.privacy_policy

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmPrivacyPolicyBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMPrivacyPolicyFragment :
    BaseFragment<FragmentRmPrivacyPolicyBinding, RMPrivacyPolicyViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_privacy_policy

    private val mViewModel: RMPrivacyPolicyViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: RMPrivacyPolicyListAdapter? = null

    override fun initView() {
        super.initView()

        setUpToolBar()

        mAdapter = RMPrivacyPolicyListAdapter()
        binding.rcvPrivacyPolicy.apply {
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
        binding.rcvPrivacyPolicy.adapter = null
        mAdapter = null
        super.onDestroyView()
    }
}