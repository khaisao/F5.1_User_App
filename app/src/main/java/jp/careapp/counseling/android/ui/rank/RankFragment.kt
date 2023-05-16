package jp.careapp.counseling.android.ui.rank

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BasePagerAdapter
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.RankFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RankFragment : BaseFragment<RankFragmentBinding, RankViewModel>() {
    private val viewModel: RankViewModel by viewModels()

    private val shareViewModel: ShareViewModel by activityViewModels()

    override val layoutId = R.layout.rank_fragment

    override fun getVM() = viewModel

    lateinit var pagerAdapter: BasePagerAdapter

    private lateinit var tabItems: Array<Pair<ImageView, TextView>>

    private val fragmentDaily = ListTypeRankingFragment.newInstance(BUNDLE_KEY.TYPE_DAILY)
    private val fragmentWeekly = ListTypeRankingFragment.newInstance(BUNDLE_KEY.TYPE_WEEKLY)
    private val fragmentMonthly = ListTypeRankingFragment.newInstance(BUNDLE_KEY.TYPE_MONTHLY)
    private val fragmentRecommend = ListTypeRankingFragment.newInstance(BUNDLE_KEY.TYPE_RECOMMEND)

    override fun initView() {
        super.initView()
        setupViewPager()

        shareViewModel.isScrollToTop.observe(viewLifecycleOwner) {
            if (it) {
                when (binding.viewPager.currentItem) {
                    0 -> {
                        fragmentDaily.scrollToTop()
                    }
                    1 -> {
                        fragmentMonthly.scrollToTop()
                    }
                    2 -> {
                        fragmentWeekly.scrollToTop()
                    }
                    3 -> {
                        fragmentRecommend.scrollToTop()
                    }
                }
            }
        }

         tabItems = arrayOf(
            Pair(binding.ivTabBarYesterdayActive, binding.tvTabBarYesterdayActive),
            Pair(binding.ivTabBarWeeklyActive, binding.tvTabBarWeeklyActive),
            Pair(binding.ivTabBarMonthlyActive, binding.tvTabBarMonthlyActive),
            Pair(binding.ivTabBarRecommendActive, binding.tvTabBarRecommendActive)
        )

        shareViewModel.isShowRankLoading.observe(viewLifecycleOwner) {
            showHideLoading(it)
        }

        lifecycleScope.launch {
            viewModel.positionTabLayout.collect {
                setUpViewTabLayout(it)
            }
        }

    }

    override fun setOnClick() {
        super.setOnClick()
        binding.apply {
            tabBarYesterday.setOnClickListener {
                viewPager.currentItem = 0
            }
            tabBarWeekly.setOnClickListener {
                viewPager.currentItem = 1

            }
            tabBarMonthly.setOnClickListener {
                viewPager.currentItem = 2

            }
            tabBarRecommend.setOnClickListener {
                viewPager.currentItem = 3

            }
        }
    }

    private fun setupViewPager() {
        pagerAdapter = BasePagerAdapter(childFragmentManager)
        pagerAdapter.addFragment(fragmentDaily, resources.getString(R.string.yesterday))
        pagerAdapter.addFragment(fragmentWeekly, resources.getString(R.string.weekly))
        pagerAdapter.addFragment(fragmentMonthly, resources.getString(R.string.monthly))
        pagerAdapter.addFragment(fragmentRecommend, resources.getString(R.string.recommend))
        binding.viewPager.adapter = pagerAdapter
        binding.tabRanking.setupWithViewPager(binding.viewPager)
        binding.viewPager.apply {
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        forceLoadData(position)
                        delay(150)
                        viewModel.positionTabLayout.value = position
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                }
            })
            currentItem = 0
            binding.viewPager.post {
                forceLoadData(0)
            }
        }
    }

    var dailyFirstLoading = true
    var weeklyFirstLoading = true
    var monthlyFirstLoading = true
    var recommendFirstLoading = true

    private fun setUpViewTabLayout(position: Int) {
        tabItems.forEachIndexed { index, item ->
            val (imageView, textView) = item
            imageView.visibility = if (position == index) View.VISIBLE else View.GONE
            textView.visibility = if (position == index) View.VISIBLE else View.GONE
        }
        binding.ivBackgroundBottomTabBar.setBackgroundResource(
            when (position) {
                0 -> R.drawable.bg_rank_daily_behind
                1 -> R.drawable.bg_rank_weekly_behind
                2 -> R.drawable.bg_rank_monthly_behind
                else -> R.drawable.bg_rank_best_behind
            }
        )
    }

    private fun forceLoadData(position: Int) {
        when (position) {
            0 -> {
                fragmentDaily.loadData(dailyFirstLoading)
                if (dailyFirstLoading) {
                    dailyFirstLoading = false
                }
            }
            1 -> {
                fragmentWeekly.loadData(weeklyFirstLoading)
                if (weeklyFirstLoading) {
                    weeklyFirstLoading = false
                }
            }
            2 -> {
                fragmentMonthly.loadData(monthlyFirstLoading)
                if (monthlyFirstLoading) {
                    monthlyFirstLoading = false
                }
            }
            3 -> {
                fragmentRecommend.loadData(recommendFirstLoading)
                if (recommendFirstLoading) {
                    recommendFirstLoading = false
                }
            }
        }
    }
}
