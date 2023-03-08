package jp.careapp.counseling.android.ui.favourite

import android.graphics.Color
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BasePagerAdapter
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.home.PerformerFragment
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.FragmentFavoriteMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteMainFragment :  BaseFragment<FragmentFavoriteMainBinding, FavoriteViewModel>()  {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    private val shareViewModel: ShareViewModel by activityViewModels()


    override val layoutId: Int=R.layout.fragment_favorite_main

    private val viewModels: FavoriteViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by activityViewModels()

    override fun getVM()=viewModels

    lateinit var pagerAdapter: BasePagerAdapter

    private val fragmentHistory = FavoriteFragment.newInstance(BUNDLE_KEY.TYPE_HISTORY)
    private val fragmentAllPerformerFollow = FavoriteFragment.newInstance(BUNDLE_KEY.TYPE_ALL_PERFORMER_FOLLOW_FAVORITE)
    override fun initView() {
        super.initView()
        setupViewPager()
        binding.tlMain.setTabTextColors(Color.parseColor(resources.getString(R.color.gray_dark)), Color.parseColor(resources.getString(R.color.white)));

    }

    override fun setOnClick() {
        super.setOnClick()
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (binding.vpMain.currentItem == 0) {
                if (!binding.progressBar.isVisible) {
                    shareViewModel.detectRefreshDataHistory.value =
                        !shareViewModel.detectRefreshDataHistory.value!!
                }
            } else {
                shareViewModel.detectRefreshDataFavorite.value =
                    !shareViewModel.detectRefreshDataFavorite.value!!
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
    }

    private fun setupViewPager() {
        pagerAdapter = BasePagerAdapter(childFragmentManager)
        pagerAdapter.addFragment(fragmentHistory, resources.getString(R.string.view_history))
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

}