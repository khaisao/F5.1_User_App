package jp.careapp.counseling.android.ui.tutorial

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.TutorialViewPagerAdapter
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.databinding.FragmentTutorialBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TutorialFragment : BaseFragment<FragmentTutorialBinding, TutorialViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId = R.layout.fragment_tutorial

    private val viewModel: TutorialViewModel by viewModels()

    override fun getVM(): TutorialViewModel = viewModel

    private val shareViewModel: ShareViewModel by activityViewModels()

    override fun setOnClick() {
        super.setOnClick()
        with(binding.btnNext) {
            setOnClickListener {
                if (binding.viewPagerTutorial.currentItem < binding.viewPagerTutorial.offscreenPageLimit - 1) {
                    binding.viewPagerTutorial.setCurrentItem(getItem(+1), true)
                } else {
                    appNavigation.openTutorialToTopScreen()
                    shareViewModel.setHaveToken(true)
                }
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.listTutorial.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                binding.viewPagerTutorial.adapter = context?.let { it1 -> TutorialViewPagerAdapter(it1, it) }
                binding.viewPagerTutorial.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                binding.viewPagerTutorial.offscreenPageLimit = it.size
                TabLayoutMediator(binding.tabLayout, binding.viewPagerTutorial) { tab, position ->
                }.attach()
            }
        )
    }

    private fun getItem(i: Int): Int {
        return binding.viewPagerTutorial.currentItem + i
    }
}
