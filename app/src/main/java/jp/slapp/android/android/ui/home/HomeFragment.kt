package jp.slapp.android.android.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.adapter.BasePagerAdapter
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.data.shareData.ShareViewModel
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.main.MainViewModel
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.databinding.FragmentHomeBinding
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

    lateinit var pagerAdapter: BasePagerAdapter
    private val fragmentAllPerformer = AllPerformerHomeFragment.newInstance(BUNDLE_KEY.TYPE_ALL_PERFORMER)
    private val fragmentAllPerformerFollow = FavoritePerformerHomeFragment.newInstance()


    @SuppressLint("ResourceType")
    override fun initView() {
        super.initView()

        binding.tlMain.setTabTextColors(Color.parseColor(resources.getString(R.color.gray_dark)), Color.parseColor(resources.getString(R.color.white)))

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

}
