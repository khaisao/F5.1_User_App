package jp.careapp.counseling.android.ui.home

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BannerAdapter
import jp.careapp.counseling.android.data.network.BannerResponse
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.FavoriteResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.favourite.FavoriteViewModel
import jp.careapp.counseling.android.ui.main.MainActivity
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.databinding.FragmentFavoritePerformerHomeBinding
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class FavoritePerformerHomeFragment :
    BaseFragment<FragmentFavoritePerformerHomeBinding, FavoriteViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId: Int = R.layout.fragment__favorite_performer_home

    private val viewModels: FavoriteViewModel by viewModels()

    private val homeViewModel: HomeViewModel by activityViewModels()


    override fun getVM(): FavoriteViewModel = viewModels

    private val shareViewModel: ShareViewModel by activityViewModels()

    private lateinit var adapterFavoriteHome: FavoriteHomeAdapter

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
        viewModels.forceRefresh()

        binding.vpBanner.apply {
            adapter = bannerAdapter
        }

        binding.rvConsultant.layoutManager = GridLayoutManager(context, 2)
        adapterFavoriteHome = FavoriteHomeAdapter(
            context = requireContext(),
            listener = { position, listData ->
                if (!isDoubleClick) {
                    onClickFavouriteItem(position, listData)
                }
            })
        binding.rvConsultant.adapter = adapterFavoriteHome

    }

    override fun setOnClick() {
        super.setOnClick()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModels.forceRefresh()
            shareViewModel.detectRefreshDataFollowerHome.value =
                !shareViewModel.detectRefreshDataFollowerHome.value!!
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModels.uiDataResult.observe(
            viewLifecycleOwner,
            Observer {
                if (it.size <= 2) {
                    isEnableScrollOfCoordinatorLayout(false)
                } else {
                    isEnableScrollOfCoordinatorLayout(true)
                }
                adapterFavoriteHome.submitList(it.toMutableList())
            }
        )

        viewModels.favoriteLoading.observe(
            viewLifecycleOwner
        ) {
            showHideLoading(it)
        }

        viewModels.isShowNoData.observe(viewLifecycleOwner) {
            showNoData(it)
        }

        homeViewModel.lisBanner.observe(viewLifecycleOwner) {
            it?.let {
                binding.vpBanner.isVisible = it.isNotEmpty()
                val newList: List<BannerResponse> =
                    listOf(it.last()) + it + listOf(it.first())
                bannerAdapter.submitList(newList) {
                    binding.vpBanner.setCurrentItem(1, false)
                }
                onInfinitePageChangeCallback(newList.size)
            }
        }
    }

    private fun showNoData(isShow: Boolean) {
        if (isShow) {
            binding.llNoResult.visibility = View.VISIBLE
            binding.rvConsultant.visibility = View.GONE
        } else {
            binding.llNoResult.visibility = View.GONE
            binding.rvConsultant.visibility = View.VISIBLE
        }
    }

    private fun isEnableScrollOfCoordinatorLayout(isEnable: Boolean) {
        val collapsingToolbarLayout: CollapsingToolbarLayout = binding.collapsingToolbarLayout
        val layoutParams = collapsingToolbarLayout.layoutParams as AppBarLayout.LayoutParams
        if (!isEnable) {
            layoutParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        } else {
            layoutParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
        }
        collapsingToolbarLayout.layoutParams = layoutParams
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(swipeBannerRunnable)

    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(swipeBannerRunnable, intervalSwipeBanner)

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavoritePerformerHomeFragment()
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

    private fun onClickFavouriteItem(
        position: Int,
        listData: List<FavoriteResponse>
    ) {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, position)
        val listConsultantResponse = arrayListOf<ConsultantResponse>()
        for (item in listData) {
            val consultantItem = ConsultantResponse(
                code = item.code
            )
            listConsultantResponse.add(consultantItem)
        }
        shareViewModel.setListPerformer(listConsultantResponse)
        appNavigation.openTopToUserProfileScreen(bundle)
    }

}