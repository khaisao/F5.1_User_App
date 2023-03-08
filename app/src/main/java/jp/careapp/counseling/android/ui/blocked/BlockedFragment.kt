package jp.careapp.counseling.android.ui.blocked

import android.view.View
import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentBlockedBinding
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import javax.inject.Inject

@AndroidEntryPoint
class BlockedFragment : BaseFragment<FragmentBlockedBinding, BlockedViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_blocked

    private val mViewModel: BlockedViewModel by viewModels()
    override fun getVM() = mViewModel

    private var mAdapter: BlockedAdapter? = null

    override fun initView() {
        super.initView()

        setUpToolBar()

        mAdapter = BlockedAdapter { mViewModel.onClickShowDialogConfirmDeleteBlock(it) }
        binding.rvBlocked.adapter = mAdapter
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.isShowNoData.observe(viewLifecycleOwner) {
            showNoData(it)
        }

        mViewModel.blockedList.observe(viewLifecycleOwner) {
            mAdapter?.submitList(it)
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is BlockedActionState.ShowDialogConfirmDeleteBlock -> showDialogConfirmDeleteBlock(
                    it.memberName,
                    it.memberCode
                )
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

    private fun showDialogConfirmDeleteBlock(memberName: String, memberCode: String) {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitleWithString("$memberName ${resources.getString(R.string.unblock)}")
            .setTextNegativeButton(R.string.cancel_block_alert)
            .setTextPositiveButton(R.string.to_release)
            .setOnNegativePressed {
                it.dismiss()
            }
            .setOnPositivePressed {
                it.dismiss()
                mViewModel.deleteBlocked(memberCode)
            }
    }

    private fun showNoData(isShow: Boolean) {
        if (isShow) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvBlocked.visibility = View.GONE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.rvBlocked.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        binding.rvBlocked.adapter = null
        mAdapter = null
        super.onDestroyView()
    }
}
