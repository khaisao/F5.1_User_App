package jp.careapp.counseling.android.ui.maintenance

import android.os.Bundle
import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentMaintanceBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.Define
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MaintenanceFragment : BaseFragment<FragmentMaintanceBinding, MaintenanceViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_maintance

    private val mViewModel: MaintenanceViewModel by viewModels()
    override fun getVM() = mViewModel

    override fun setOnClick() {
        super.setOnClick()

        binding.btnTwitter.setOnClickListener {
            if (!isDoubleClick) {
                val bundle = Bundle().apply {
                    putString(Define.TITLE_WEB_VIEW, getString(R.string.PrivacyPolicy))
                    putString(Define.URL_WEB_VIEW, Define.URL_TWITTER)
                }
                appNavigation.openMaintenanceToWebview(bundle)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        System.exit(0)
    }
}
