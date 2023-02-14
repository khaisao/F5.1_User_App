package jp.careapp.counseling.android.ui.blocked

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.executeAfter
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.FavoriteResponse
import jp.careapp.counseling.databinding.FragmentBlockedBinding
import jp.careapp.counseling.android.utils.event.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockedFragment : BaseFragment<FragmentBlockedBinding, BlockedViewModel>() {

    private val viewModels: BlockedViewModel by viewModels()
    private lateinit var adapter: BlockedAdapter
    override val layoutId: Int = R.layout.fragment_blocked
    override fun getVM(): BlockedViewModel = viewModels
    private var blockeds: MutableList<FavoriteResponse> = mutableListOf()
    override fun bindingStateView() {
        super.bindingStateView()
        viewModels.forceRefresh()
        adapter = BlockedAdapter(lifecycleOwner = viewLifecycleOwner, events = viewModels)
        binding.apply {
            appBar.apply {
                btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_back_left))
                btnLeft.setOnClickListener {
                    if (!isDoubleClick)
                        findNavController().navigateUp()
                }
                tvTitle.text = getString(R.string.block_list)
                viewStatusBar.visibility = View.GONE
            }
            rvBlocked.setHasFixedSize(true)
            rvBlocked.adapter = adapter
        }
        viewModels.blockedLoading.observe(
            viewLifecycleOwner,
            Observer {
                binding.executeAfter {
                    loading = it
                }
            }
        )
        viewModels.error.observe(
            viewLifecycleOwner,
            Observer {
            }
        )
        viewModels.uiBlocked.observe(
            viewLifecycleOwner,
            Observer {
                it ?: return@Observer
                blockeds.clear()
                blockeds.addAll(it)
                adapter.submitList(it.toMutableList())
            }
        )
        viewModels.showDialogAction.observe(
            viewLifecycleOwner,
            EventObserver { data ->
                CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                    .showDialog()
                    .setDialogTitleWithString("${data.name} ${resources.getString(R.string.unblock)}")
                    .setTextNegativeButton(R.string.cancel_block_alert)
                    .setTextPositiveButton(R.string.to_release)
                    .setOnNegativePressed {
                        it.dismiss()
                    }
                    .setOnPositivePressed {
                        viewModels.setCodeBlocked(data.code)
                        if (blockeds.removeIf {
                            it.code == data.code
                        }
                        )
                            adapter.submitList(blockeds)
                        if (blockeds.isEmpty())
                            viewModels.isShowNoData.postValue(true)
                        it.dismiss()
                    }
            }
        )
        viewModels.deleteBlockedLoading.observe(
            viewLifecycleOwner,
            Observer {
                showHideLoading(it)
            }
        )
        viewModels.isShowNoData.observe(viewLifecycleOwner, isShowNoDataObserver)
    }

    private var isShowNoDataObserver: Observer<Boolean> = Observer {
        showNoData(it)
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
}
