package jp.careapp.counseling.android.ui.message

import android.graphics.Color
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.BasePagerAdapter
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.chatList.ChatListFragment
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.databinding.FragmentMessageMainBinding
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
        pagerAdapter.addFragment(fragmentMessageAll, resources.getString(R.string.all))
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