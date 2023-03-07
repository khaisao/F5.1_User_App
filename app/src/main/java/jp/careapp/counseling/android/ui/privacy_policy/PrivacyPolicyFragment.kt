package jp.careapp.counseling.android.ui.privacy_policy

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentPrivacyPolicyBinding
import javax.inject.Inject

@AndroidEntryPoint
class PrivacyPolicyFragment : BaseFragment<FragmentPrivacyPolicyBinding, PrivacyPolicyViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_privacy_policy

    private val mViewModel: PrivacyPolicyViewModel by viewModels()
    override fun getVM() = mViewModel

    private val mAdapter by lazy { PrivacyPolicyListAdapter() }

    override fun initView() {
        super.initView()

        setUpToolBar()

        binding.rcvPrivacyPolicy.apply {
            adapter = mAdapter
            setHasFixedSize(true)
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