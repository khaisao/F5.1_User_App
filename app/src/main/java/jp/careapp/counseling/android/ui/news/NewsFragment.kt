package jp.careapp.counseling.android.ui.news

import android.view.View
import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentNewsBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.event.EventObserver
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewsFragment : BaseFragment<FragmentNewsBinding, NewsViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId = R.layout.fragment_news
    private val viewModels: NewsViewModel by viewModels()
    override fun getVM(): NewsViewModel = viewModels
    private lateinit var fragmentStateAdapterNews: FragementStateAdapterNews

    private var isFromNotification = false

    override fun initView() {
        super.initView()
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(Define.Intent.OPEN_DIRECT_FROM_NOTIFICATION)) {
                isFromNotification = bundle.getBoolean(Define.Intent.OPEN_DIRECT_FROM_NOTIFICATION, false)
            }
        }
    }
    override fun bindingStateView() {
        super.bindingStateView()
        viewModels.forceRefresh()
        viewModels.error.observe(
            viewLifecycleOwner,
            EventObserver {
            }
        )
        val title = listOf("お知らせ", "重要")
        fragmentStateAdapterNews = FragementStateAdapterNews(
            listOf(
                SubNewsFragment(),
                ImportantNewsFragment()
            ),
            this
        )
        binding.apply {
            viewPagger.adapter = fragmentStateAdapterNews
            appBar.apply {
                btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_back_left))
                btnLeft.setOnClickListener {
                    if (!isDoubleClick)
                        if (isFromNotification) {
                            appNavigation.openNewsToTopScreen()
                        } else {
                            appNavigation.navigateUp()
                        }
                }
                tvTitle.text = resources.getString(R.string.news)
                lineBottom.visibility = View.INVISIBLE
                viewStatusBar.visibility = View.GONE
            }
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPagger) { tab, position ->
            tab.text = title.get(position)
        }.attach()
    }
}
