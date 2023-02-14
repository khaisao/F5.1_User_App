package jp.careapp.counseling.android.ui.badUser

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.databinding.FragmentBadUserBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BadUserFragment : BaseFragment<FragmentBadUserBinding, BadUserViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId = R.layout.fragment_bad_user
    private val viewModel: BadUserViewModel by viewModels()

    override fun getVM(): BadUserViewModel = viewModel

    override fun initView() {
        super.initView()

        val spanContactUs = SpannableString(getString(R.string.please_contact_with_have_question))
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val bundle = Bundle().apply {
                    putString("type_contact", this@BadUserFragment::class.java.simpleName)
                }
                appNavigation.openMyPageToContact(bundle)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = resources.getColor(R.color.color_978DFF)
                ds.isUnderlineText = true
            }
        }
        spanContactUs.setSpan(clickableSpan1, 12, 22, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        binding.tvContactUs.setText(spanContactUs, TextView.BufferType.SPANNABLE)
        binding.tvContactUs.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.toolBar.setOnToolBarClickListener(
            object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    appNavigation.navigateUp()
                }
            }
        )
    }
}
