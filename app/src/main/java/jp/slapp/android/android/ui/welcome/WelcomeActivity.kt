package jp.slapp.android.android.ui.welcome

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import jp.careapp.core.base.BaseActivity
import jp.slapp.android.R
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.databinding.ActivityWelcomeBinding
import jp.slapp.android.android.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding, WelcomeViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences
    private val viewModel: WelcomeViewModel by viewModels()

    override val layoutId = R.layout.activity_welcome

    override fun getVM() = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_welcome) as NavHostFragment
        appNavigation.bind(navHostFragment.navController)

        handleLaunchAppWithDeepLink()
    }

    private fun handleLaunchAppWithDeepLink() {
        var data: Uri? = this.intent.data
        if (data != null && data.isHierarchical) {
            val uri = this.intent.dataString
            if (uri != null) {
                rxPreferences.saveDeepLink(uri)
            }
        }
    }
}
