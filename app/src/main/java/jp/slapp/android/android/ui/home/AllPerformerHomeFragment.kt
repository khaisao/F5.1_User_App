package jp.slapp.android.android.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.URLUtil
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.adapter.BannerAdapter
import jp.slapp.android.android.adapter.ConsultantAdapter
import jp.slapp.android.android.data.network.BannerResponse
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.data.shareData.ShareViewModel
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.main.MainActivity
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.Define
import jp.slapp.android.databinding.FragmentPerformerBinding
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AllPerformerHomeFragment : BaseFragment<FragmentPerformerBinding, HomeViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId: Int = R.layout.fragment_performer

    private val viewModel: HomeViewModel by activityViewModels()

    override fun getVM(): HomeViewModel = viewModel

    private val shareViewModel: ShareViewModel by activityViewModels()

    private var typeOnlineListScreen: Int? = 0

    private val mConsultantAdapter: ConsultantAdapter by lazy {
        ConsultantAdapter(requireContext(), listener = { position, listData ->
            if (!isDoubleClick) {
                onClickDetailConsultant(position, listData)
            }
        })
    }

    private val intervalSwipeBanner = 5000L
    private val bannerAdapter: BannerAdapter by lazy {
        BannerAdapter { banner ->
            onClickBanner(banner)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val swipeBannerRunnable: Runnable by lazy {
        object : Runnable {
            override fun run() {
                try {
                    if (binding.vpBanner.currentItem < bannerAdapter.itemCount - 1) {
                        binding.vpBanner.setCurrentItem(binding.vpBanner.currentItem + 1, true)
                    } else {
                        binding.vpBanner.setCurrentItem(0, false)
                    }
                    handler.postDelayed(this, intervalSwipeBanner)
                } catch (_: Exception) {

                }
            }
        }
    }


    override fun initView() {
        super.initView()
        arguments?.let {
            typeOnlineListScreen = it.getInt(BUNDLE_KEY.TYPE_ONLINE_LIST_SCREEN)
        }

        binding.vpBanner.apply {
            adapter = bannerAdapter
        }

        binding.rvConsultant.layoutManager = GridLayoutManager(context, 2)
        binding.rvConsultant.adapter = mConsultantAdapter
        viewModel.getAllData()
        binding.rvConsultant.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val listDataSize = viewModel.listConsultant.value?.size ?: 0
                if (listDataSize > 0) {
                    val layoutManager = binding.rvConsultant.layoutManager as LinearLayoutManager
                    if ((layoutManager.findLastCompletelyVisibleItemPosition() == listDataSize - 1) && viewModel.isCanLoadMoreData()) {
                        viewModel.getAllData(true)
                    }
                }
            }
        })
    }

    override fun bindingStateView() {
        super.bindingStateView()
        if (typeOnlineListScreen == BUNDLE_KEY.TYPE_ALL_PERFORMER) viewModel.listConsultant.observe(
            viewLifecycleOwner
        ) {
            if (!it.isNullOrEmpty()) {
                shareViewModel.saveListPerformerSearch(it)
                binding.rvConsultant.visibility = View.VISIBLE
            } else {
                binding.rvConsultant.visibility = View.GONE
            }
            mConsultantAdapter.submitList(it)
        }

        viewModel.lisBanner.observe(viewLifecycleOwner) {
            it?.let {
                binding.vpBanner.isVisible = it.isNotEmpty()
                val newList: List<BannerResponse> = listOf(it.last()) + it + listOf(it.first())
                bannerAdapter.submitList(newList) {
                    binding.vpBanner.setCurrentItem(1, false)
                }
                onInfinitePageChangeCallback(newList.size)
            }
        }

        shareViewModel.isScrollToTopHomeScreen.observe(viewLifecycleOwner) {
            binding.rvConsultant.smoothScrollToPosition(0)
            binding.alRv.setExpanded(true, true)
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.clearData()
            shareViewModel.detectRefreshDataFollowerHome.value =
                !shareViewModel.detectRefreshDataFollowerHome.value!!
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(swipeBannerRunnable)

    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(swipeBannerRunnable, intervalSwipeBanner)

    }

    private fun onClickDetailConsultant(
        position: Int, listData: List<ConsultantResponse>
    ) {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, position)
        shareViewModel.setListPerformer(listData)
        appNavigation.openTopToUserProfileScreen(bundle)
    }

    companion object {
        @JvmStatic
        fun newInstance(typeOnlineListScreen: Int) = AllPerformerHomeFragment().apply {
            arguments = Bundle().apply {
                putInt(BUNDLE_KEY.TYPE_ONLINE_LIST_SCREEN, typeOnlineListScreen)
            }
        }
    }

    private fun onInfinitePageChangeCallback(listSize: Int) {
        binding.vpBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                handler.removeCallbacks(swipeBannerRunnable)
                handler.postDelayed(swipeBannerRunnable, intervalSwipeBanner)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    when (binding.vpBanner.currentItem) {
                        listSize - 1 -> binding.vpBanner.setCurrentItem(1, false)
                        0 -> binding.vpBanner.setCurrentItem(listSize - 2, false)
                    }
                }
            }
        })
    }

    private fun onClickBanner(banner: BannerResponse) {
        banner.androidLink?.let { androidLink ->
            try {
                if (URLUtil.isNetworkUrl(androidLink)) {
                    val bundle = Bundle().apply {
                        putString(Define.TITLE_WEB_VIEW, banner.title)
                        putString(Define.URL_WEB_VIEW, banner.androidLink)
                    }
                    appNavigation.openScreenToWebview(bundle)
                } else if (androidLink.startsWith(Define.PREFIX_CARE_APP)) {
                    (activity as MainActivity).handleDeepLink(androidLink.toUri())
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(androidLink))
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}