package jp.slapp.android.android.ui.faq

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentFaqBinding
import javax.inject.Inject

@AndroidEntryPoint
class FAQFragment : BaseFragment<FragmentFaqBinding, FAQViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_faq

    private val mViewModel: FAQViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: FAQListAdapter? = null

    override fun initView() {
        super.initView()

        setUpToolBar()

        mAdapter = FAQListAdapter {
            mViewModel.handleOnClickItemContent(it)
        }
        binding.rcvFAQ.adapter = mAdapter
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.faqList.observe(viewLifecycleOwner) { mAdapter?.submitList(it) }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is FAQActionState.NavigateToWithdrawal -> appNavigation.openFAQToWithdrawal()
            }
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
        binding.rcvFAQ.adapter = null
        mAdapter = null
        super.onDestroyView()
    }
}