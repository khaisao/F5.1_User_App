package jp.careapp.counseling.android.ui.review_mode.block_list

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.model.network.RMBlockListResponse
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.databinding.FragmentBlockListBinding
import jp.careapp.counseling.android.ui.review_mode.online_list.RMPerformerAdapter
import javax.inject.Inject

@AndroidEntryPoint
class RMBlockListFragment : BaseFragment<FragmentBlockListBinding, RMBlockListViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: RMBlockListViewModel by viewModels()

    override val layoutId = R.layout.fragment_block_list

    override fun getVM() = viewModel

    private val _adapter by lazy {
        RMPerformerAdapter(requireContext(),
            onClickListener = {},
            onClickDelete = {
                (it as? RMBlockListResponse)?.code?.let { code ->
                    viewModel.deleteBlock(code)
                }
            }
        )
    }

    override fun initView() {
        super.initView()

        binding.apply {
            toolBar.apply {
                btnLeft.setOnClickListener {
                    if (!isDoubleClick)
                        findNavController().navigateUp()
                }
            }

            rvBlock.apply {
                setHasFixedSize(true)
                adapter = _adapter
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.listBlock.observe(viewLifecycleOwner) {
            it?.let {
                _adapter.submitList(it)
            }
        }

        viewModel.iShowNoData.observe(viewLifecycleOwner) {
            it?.let {
                binding.tvNoData.isVisible = it
            }
        }
    }

    override fun onDestroyView() {
        binding.rvBlock.adapter = null
        super.onDestroyView()
    }
}