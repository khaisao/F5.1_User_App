package jp.slapp.android.android.ui.message

import android.graphics.Color
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.adapter.BasePagerAdapter
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.chatList.ChatListFragment
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.databinding.FragmentMessageMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MessageMainFragment : BaseFragment<FragmentMessageMainBinding, ChatMessageViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId: Int = R.layout.fragment_message_main

    private val viewModels: ChatMessageViewModel by viewModels()

    override fun getVM() = viewModels

    lateinit var pagerAdapter: BasePagerAdapter

    private val fragmentMessageAll = ChatListFragment.newInstance(BUNDLE_KEY.TYPE_ALL_MESSAGE)

    override fun initView() {
        super.initView()
        setupViewPager()
        binding.tlMain.setTabTextColors(
            Color.parseColor(resources.getString(R.color.gray_dark)),
            Color.parseColor(resources.getString(R.color.white))
        )
    }

    private fun setupViewPager() {
        pagerAdapter = BasePagerAdapter(childFragmentManager)
        pagerAdapter.addFragment(fragmentMessageAll, resources.getString(R.string.title_navigation_list_message))
        binding.vpMain.adapter = pagerAdapter
        binding.tlMain.setupWithViewPager(binding.vpMain)
        binding.vpMain.apply {
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
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