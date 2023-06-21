package jp.slapp.android.android.ui.privacy_policy

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentPrivacyPolicyBinding
import javax.inject.Inject

@AndroidEntryPoint
class PrivacyPolicyFragment : BaseFragment<FragmentPrivacyPolicyBinding, PrivacyPolicyViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_privacy_policy

    private val mViewModel: PrivacyPolicyViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: PrivacyPolicyListAdapter? = null

    override fun initView() {
        super.initView()

        setUpToolBar()

        mAdapter = PrivacyPolicyListAdapter()
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