package jp.slapp.android.android.ui.greet

import android.os.Bundle
import android.text.Html
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.email.InputAndEditMailViewModel
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.databinding.FragmentGreetBinding
import javax.inject.Inject

@AndroidEntryPoint
class GreetFragment : BaseFragment<FragmentGreetBinding, GreetViewModel>() {
    override val layoutId: Int = R.layout.fragment_greet

    private val viewModel: GreetViewModel by viewModels()

    @Inject
    lateinit var appNavigation: AppNavigation

    override fun getVM(): GreetViewModel = viewModel

    override fun initView() {
        super.initView()
        binding.tvAlreadyHaveAcc.text =
            Html.fromHtml(
                resources.getString(R.string.already_have_acc),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        binding.linkPrivacy.text = Html.fromHtml(
            resources.getString(R.string.link_privacy),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.linkPrivacy.setOnClickListener {
            if (!isDoubleClick) appNavigation.openTermsOfService()
        }
        binding.tvAlreadyHaveAcc.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(BUNDLE_KEY.CODE_SCREEN, InputAndEditMailViewModel.SCREEN_LOGIN_WITH_EMAIL)
            }
            appNavigation.openGreetToInputEmail(bundle)
        }

        binding.btnEmail.setOnClickListener {
            if(!isDoubleClick){
                appNavigation.openGreetToRegistration()
            }
        }
    }

}