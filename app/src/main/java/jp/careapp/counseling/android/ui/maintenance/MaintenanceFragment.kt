package jp.careapp.counseling.android.ui.maintenance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentMaintanceBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.event.NetworkEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MaintenanceFragment : BaseFragment<FragmentMaintanceBinding, MaintenanceViewModel>() {

    var type: String = ""
    @Inject
    lateinit var networkEvent: NetworkEvent
    @Inject
    lateinit var appNavigation: AppNavigation
    private val viewModels: MaintenanceViewModel by viewModels()
    override val layoutId: Int = R.layout.fragment_maintance
    override fun getVM(): MaintenanceViewModel = viewModels
    override fun bindingStateView() {
        super.bindingStateView()
        type = arguments?.getString("type").toString()
        binding.btnTwitter.setOnClickListener {
            if (!isDoubleClick) {
                val bundle = Bundle().apply {
                    putString(Define.TITLE_WEB_VIEW, getString(R.string.PrivacyPolicy))
                    putString(Define.URL_WEB_VIEW, Define.URL_TWITTER)
                }
                appNavigation.openMaintenanceToWebview(bundle)
            }
        }
        binding.apply {
            when (type) {
                Define.TYPE_MAINTENANCE -> {
                    image.setImageResource(R.drawable.maintenance)
                    content.text = getString(R.string.careia_maintenance)
                }
                Define.TYPE_ERROR -> {
                    image.setImageResource(R.drawable.error)
                    title.visibility = View.GONE
                    content.text = getString(R.string.careia_error)
                    content.textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        System.exit(0)
    }
}
