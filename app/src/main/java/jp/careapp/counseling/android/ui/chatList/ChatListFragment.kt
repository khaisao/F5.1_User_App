package jp.careapp.counseling.android.ui.chatList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BaseAdapterLoadMore
import jp.careapp.counseling.android.data.model.history_chat.HistoryChatResponse
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.ChatListFragmentBinding
import javax.inject.Inject

@AndroidEntryPoint
class ChatListFragment : BaseFragment<ChatListFragmentBinding, ChatListViewModel>() {

    private val viewModel: ChatListViewModel by viewModels()
    override fun getVM(): ChatListViewModel = viewModel
    private val shareViewModel: ShareViewModel by activityViewModels()

    override val layoutId = R.layout.chat_list_fragment

    @Inject
    lateinit var appNavigation: AppNavigation

    private val mAdapter: ChatListAdapter by lazy {
        ChatListAdapter(
            requireContext(),
            onClickListener = (
                    { historyChatResponse ->
                        if (!isDoubleClick) {
                            activity?.let {
                                val data = Bundle()
                                data.putString(
                                    BUNDLE_KEY.PERFORMER_CODE,
                                    historyChatResponse.performer?.code
                                )
                                data.putString(
                                    BUNDLE_KEY.PERFORMER_NAME,
                                    historyChatResponse.performer?.name
                                )
                                appNavigation.openChatMessageToChatListScreen(data)
                            }
                        }
                    }
                    )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            viewModel.registerEvent()
        }
    }

    override fun initView() {
        super.initView()
        viewModel.loadListMessage(false)
        binding.historyChatRv.adapter = mAdapter
        binding.historyChatRv.itemAnimator = null
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.refreshRl.setOnRefreshListener {
            viewModel.loadListMessage(false)
            binding.refreshRl.isRefreshing = false
        }
        if (mAdapter.getLoadMorelistener() == null) {
            mAdapter.setLoadMorelistener(
                object : BaseAdapterLoadMore.LoadMorelistener {
                    override fun onLoadMore() {
                        if (!mAdapter.disableLoadMore && !binding.refreshRl.isRefreshing) {
                            mAdapter.setIsLoading(true)
                            viewModel.loadListMessage(true)
                        }
                    }
                }
            )
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.listHistoryChatResult.observe(viewLifecycleOwner, handleHistoryChat)
        viewModel.loadMoreState.observe(viewLifecycleOwner, handleLoadMore)
        viewModel.isShowNoData.observe(viewLifecycleOwner, isShowNoDataObserver)
        shareViewModel.isScrollToTop.observe(viewLifecycleOwner) {
            if (it) {
                binding.historyChatRv.smoothScrollToPosition(0)
                shareViewModel.doneScrollView()
            }
        }
    }

    private var isShowNoDataObserver: Observer<Boolean> = Observer {
        showNoData(it)
    }

    private fun showNoData(isShow: Boolean) {
        if (isShow) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.historyChatRv.visibility = View.GONE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.historyChatRv.visibility = View.VISIBLE
        }
    }

    private var handleHistoryChat: Observer<List<HistoryChatResponse>> = Observer {
        mAdapter.run {
            submitList(it)
            if (viewModel.getPaidMessPage() == 2 && mAdapter.itemCount > 0) {
                binding.historyChatRv.smoothScrollToPosition(0)
            }
        }
    }

    private var handleLoadMore: Observer<Int> = Observer {
        when (it) {
            ChatListViewModel.HIDDEN_LOAD_MORE -> {
                if (mAdapter.isLoading) {
                    mAdapter.setIsLoading(false)
                }
            }
            ChatListViewModel.DISABLE_LOAD_MORE -> {
                mAdapter.setDisableLoadmore(true)
            }
            ChatListViewModel.ENABLE_LOAD_MORE -> {
                mAdapter.setDisableLoadmore(false)
            }
        }
    }

    override fun onDestroy() {
        activity?.let {
            viewModel.removeEvent()
        }
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(typeMessageScreen: Int) =
            ChatListFragment().apply {
                arguments = Bundle().apply {
                    putInt(BUNDLE_KEY.TYPE_MESSAGE_SCREEN, typeMessageScreen)
                }
            }
    }
}
