package jp.careapp.counseling.android.ui.home

import android.os.Bundle
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.setDrawableCompat
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.databinding.FragmentBannerItemBinding
import javax.inject.Inject

@AndroidEntryPoint
class BannerFragment : BaseFragment<FragmentBannerItemBinding, BannerItemViewModel>() {
    override val layoutId: Int = R.layout.fragment_banner_item

    @Inject
    lateinit var appNavigation: AppNavigation

    private val viewModel: BannerItemViewModel by viewModels()
    private var bannerType: Int? = 0

    override fun getVM(): BannerItemViewModel = viewModel

    override fun initView() {
        super.initView()
        arguments?.let {
            bannerType = it.getInt(BUNDLE_KEY.BANNER_TYPE)
        }
        if (bannerType == null) return
        setUpContentWithType()
    }

    private fun setUpContentWithType() {
        binding.ivBanner.setOnClickListener {
            if (isDoubleClick) {
                return@setOnClickListener
            }
            when (bannerType) {
                BANNER_TYPE_REUNION -> openHelpPage()
                BANNER_TYPE_PURCHASE -> openListCreditItemPage()
                else -> {
                }
            }
        }
    }

    private fun openHelpPage() {
        val webViewBundle = Bundle().apply {
            putString(
                Define.TITLE_WEB_VIEW,
                getString(R.string.title_home)
            )
            putString(
                Define.URL_WEB_VIEW,
                Define.URL_OPEN_FROM_HOME
            )
        }
        appNavigation.openHomeToBannerFirstTimeUseScreen(webViewBundle)
    }

    private fun openListCreditItemPage() {
        val webViewBundle = Bundle().apply {
            putString(
                Define.TITLE_WEB_VIEW,
                getString(R.string.stripe_buy_point)
            )
            putString(
                Define.URL_WEB_VIEW,
                Define.URL_STRIPE_BUY_POINT
            )
        }
        appNavigation.openHomeToBannerFirstTimeUseScreen(webViewBundle)
    }

    companion object {
        fun newInstance(bannerType: Int) = BannerFragment().apply {
            arguments = Bundle().apply {
                putInt(BUNDLE_KEY.BANNER_TYPE, bannerType)
            }
        }

        const val BANNER_TYPE_REUNION = 0
        const val BANNER_TYPE_PURCHASE = 1
    }
}
