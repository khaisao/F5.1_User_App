package jp.careapp.counseling.android.ui.home

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.URLUtil
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BannerAdapter
import jp.careapp.counseling.android.adapter.BasePagerAdapter
import jp.careapp.counseling.android.adapter.ConsultantAdapter
import jp.careapp.counseling.android.data.network.BannerResponse
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.favourite.FavoriteFragment
import jp.careapp.counseling.android.ui.main.MainActivity
import jp.careapp.counseling.android.ui.main.MainViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.Define.Companion.PREFIX_CARE_APP
import jp.careapp.counseling.databinding.FragmentHomeBinding
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    private val viewModel: HomeViewModel by activityViewModels()

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

    lateinit var pagerAdapter: BasePagerAdapter
    private val fragmentAllPerformer = PerformerFragment.newInstance(BUNDLE_KEY.TYPE_ALL_PERFORMER)
    private val fragmentAllPerformerFollow = FavoriteFragment.newInstance(BUNDLE_KEY.TYPE_ALL_PERFORMER_FOLLOW_HOME)


    override fun initView() {
        super.initView()
        binding.vpBanner.apply {
            adapter = bannerAdapter
            TabLayoutMediator(binding.tabIndicator, binding.vpBanner) { _, _ ->

            }.attach()
        }

        binding.tlMain.setTabTextColors(Color.parseColor(resources.getString(R.color.gray_dark)), Color.parseColor(resources.getString(R.color.white)));

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (binding.vpMain.currentItem == 0) {
                if (!binding.progressBar.isVisible) {
                    viewModel.isShowHideLoading = true
                    viewModel.clearData()
                    viewModel.getListBlockedConsultant()
                }
            } else {
                shareViewModel.detectRefreshDataFollowerHome.value = !shareViewModel.detectRefreshDataFollowerHome.value!!
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        setupViewPager()
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.ivSearch.setOnClickListener {
            if (!isDoubleClick) {
                appNavigation.openTopToSearchScreen()
            }
        }
    }

    private fun setupViewPager() {
        pagerAdapter = BasePagerAdapter(childFragmentManager)
        pagerAdapter.addFragment(fragmentAllPerformer, resources.getString(R.string.all))
        pagerAdapter.addFragment(fragmentAllPerformerFollow, resources.getString(R.string.follow))
        binding.vpMain.adapter = pagerAdapter
        binding.tlMain.setupWithViewPager(binding.vpMain)
        binding.vpMain.apply {
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

            })
            currentItem = 0

        }
    }

    override fun onDestroyView() {
        viewModel.clearData()
        super.onDestroyView()
    }


    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.lisBanner.observe(viewLifecycleOwner) {
            it?.let {
                binding.vpBanner.isVisible = it.isNotEmpty()
                bannerAdapter.submitList(it) {
                    binding.vpBanner.setCurrentItem(0, false)
                }
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner, isLoadingObserver)

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
