package jp.slapp.android.android.ui.review_mode.block_list

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.databinding.FragmentBlockListBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMBlockListFragment : BaseFragment<FragmentBlockListBinding, RMBlockListViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val mViewModel: RMBlockListViewModel by viewModels()

    override val layoutId = R.layout.fragment_block_list

    override fun getVM() = mViewModel

    private var mAdapter: RMBlockListAdapter? = null

    override fun initView() {
        super.initView()

        mAdapter = RMBlockListAdapter(
            onClickDelete = {
                mViewModel.deleteBlock(it)
            }
        )
        binding.apply {
            toolBar.apply {
                btnLeft.setOnClickListener {
                    if (!isDoubleClick)
                        findNavController().navigateUp()
                }
            }

            rvBlock.apply {
                setHasFixedSize(true)
                adapter = mAdapter
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.blockListLiveData.observe(viewLifecycleOwner) {
            mAdapter?.submitList(it)
        }

        mViewModel.isShowNoData.observe(viewLifecycleOwner) {
            binding.tvNoData.isVisible = it
        }
    }

    override fun onDestroyView() {
        binding.rvBlock.adapter = null
        mAdapter = null
        super.onDestroyView()
    }
}