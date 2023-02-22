package jp.careapp.counseling.android.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.URLUtil
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BannerAdapter
import jp.careapp.counseling.android.adapter.ConsultantAdapter
import jp.careapp.counseling.android.data.network.BannerResponse
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.main.MainActivity
import jp.careapp.counseling.android.ui.main.MainViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.Define.Companion.PREFIX_CARE_APP
import jp.careapp.counseling.android.utils.GenresUtil
import jp.careapp.counseling.databinding.FragmentHomeBinding
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    private val viewModel: HomeViewModel by viewModels()
    override fun getVM(): HomeViewModel = viewModel
    private val mainViewModels: MainViewModel by activityViewModels()
    override val layoutId = R.layout.fragment_home

    private val shareViewModel: ShareViewModel by activityViewModels()
    private val intervalSwipeBanner = 5000L
    private val bannerAdapter: BannerAdapter by lazy {
        BannerAdapter { banner ->
            onClickBanner(banner)
        }
    }

    private val mConsultantAdapter: ConsultantAdapter by lazy {
        ConsultantAdapter(
            requireContext(),
            GenresUtil.getListGenres(),
            listener = { position, listData ->
                if (!isDoubleClick) {
                    onClickDetailConsultant(position, listData)
                }
            }
        )
    }

    private val handler = Handler(Looper.getMainLooper())
    private val swipeBannerRunnable: Runnable by lazy {
        object : Runnable {
            override fun run() {
                if (binding.vpBanner.currentItem < bannerAdapter.itemCount - 1) {
                    binding.vpBanner.setCurrentItem(binding.vpBanner.currentItem + 1, true)
                } else {
                    binding.vpBanner.setCurrentItem(0, false)
                }
                handler.postDelayed(this, intervalSwipeBanner)
            }
        }
    }

    override fun initView() {
        super.initView()
        binding.vpBanner.apply {
            adapter = bannerAdapter
            TabLayoutMediator(binding.tabIndicator, binding.vpBanner) { _, _ ->

            }.attach()
        }
//        val layoutManager = LinearLayoutManager(context)
//        binding.rvConsultant.layoutManager = layoutManager
        binding.rvConsultant.layoutManager = GridLayoutManager(context, 2)
        binding.rvConsultant.adapter = mConsultantAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (!binding.progressBar.isVisible) {
                viewModel.isShowHideLoading = true
                viewModel.clearData()
                viewModel.getListBlockedConsultant()
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.rvConsultant.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val listDataSize = viewModel.listConsultantResult.value?.size ?: 0
                if (listDataSize > 0) {
                    val layoutManager = binding.rvConsultant.layoutManager as LinearLayoutManager
                    if (!viewModel.isLoadMoreData) {
                        if (layoutManager != null && (layoutManager.findLastCompletelyVisibleItemPosition() == listDataSize - 1) && viewModel.isCanLoadMoreData()) {
                            viewModel.isLoadMoreData = true
                            viewModel.loadMoreData()
                        }
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        binding.rvConsultant.clearOnScrollListeners()
        viewModel.clearData()
        super.onDestroyView()
    }

    private fun onClickDetailConsultant(
        position: Int,
        listData: List<ConsultantResponse>
    ) {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, position)
        shareViewModel.setListPerformer(listData)
        appNavigation.openTopToUserProfileScreen(bundle)
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.lisBanner.observe(viewLifecycleOwner) {
            it?.let {
                binding.vpBanner.isVisible = it.isNotEmpty()
                binding.tabIndicator.isVisible = it.size > 1
                bannerAdapter.submitList(it) {
                    binding.vpBanner.setCurrentItem(0, false)
                }
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner, isLoadingObserver)

        viewModel.listConsultantResult.observe(
            viewLifecycleOwner,
            Observer {
                if (!it.isNullOrEmpty()) {
                    shareViewModel.saveListPerformerSearch(it)
                }
                mConsultantAdapter.submitList(it)
            }
        )

        mainViewModels.currentFragment.observe(
            viewLifecycleOwner,
            Observer {
                if (isVisible)
                    viewModel.getListBlockedConsultant()
                else
                    return@Observer
            }
        )

        shareViewModel.isScrollToTop.observe(viewLifecycleOwner) {
            if (it) {
                shareViewModel.doneScrollView()
            }
        }

        shareViewModel.needUpdateProfile.observe(viewLifecycleOwner) {
            if (it) viewModel.getListBanner()
        }

        shareViewModel.isBlockConsultant.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getListBlockedConsultant()
                shareViewModel.isBlockConsultant.value = false
            }
        }
    }

    private var isLoadingObserver: Observer<Boolean> = Observer {
        showHideLoading(it)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(swipeBannerRunnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(swipeBannerRunnable, intervalSwipeBanner)
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
                } else if (androidLink.startsWith(PREFIX_CARE_APP)) {
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
