package jp.careapp.counseling.android.ui.rank

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BasePagerAdapter
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.RankFragmentBinding

@AndroidEntryPoint
class RankFragment : BaseFragment<RankFragmentBinding, RankViewModel>() {
    private val viewModel: RankViewModel by viewModels()

    private val shareViewModel: ShareViewModel by activityViewModels()

    override val layoutId = R.layout.rank_fragment

    override fun getVM() = viewModel

    lateinit var pagerAdapter: BasePagerAdapter

    private val fragmentDaily = ListTypeRankingFragment.newInstance(BUNDLE_KEY.TYPE_DAILY)
    private val fragmentWeekly = ListTypeRankingFragment.newInstance(BUNDLE_KEY.TYPE_WEEKLY)
    private val fragmentMonthly = ListTypeRankingFragment.newInstance(BUNDLE_KEY.TYPE_MONTHLY)
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
                        fragmentWeekly.scrollToTop()
                    }
                    2 -> {
                        fragmentMonthly.scrollToTop()
                    }
                }
            }
        }

        shareViewModel.isShowRankLoading.observe(viewLifecycleOwner) {
            showHideLoading(it)
        }
    }

    private fun setupViewPager() {
        pagerAdapter = BasePagerAdapter(childFragmentManager)
        pagerAdapter.addFragment(fragmentDaily, resources.getString(R.string.yesterday))
        pagerAdapter.addFragment(fragmentWeekly, resources.getString(R.string.weekly))
        pagerAdapter.addFragment(fragmentMonthly, resources.getString(R.string.monthly))
        binding.viewPager.adapter = pagerAdapter
        binding.tabRanking.setupWithViewPager(binding.viewPager)
        binding.viewPager.apply {
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    forceLoadData(position)
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
        }
    }
}
