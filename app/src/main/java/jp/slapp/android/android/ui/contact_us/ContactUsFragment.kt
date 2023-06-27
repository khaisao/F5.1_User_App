package jp.slapp.android.android.ui.contact_us

import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.contact_us.confirm.CONTACT_US_CATEGORY
import jp.slapp.android.android.ui.contact_us.confirm.CONTACT_US_CONTENT
import jp.slapp.android.android.ui.contact_us.confirm.CONTACT_US_REPLY
import jp.slapp.android.android.utils.CONTACT_US_MAIL
import jp.slapp.android.android.utils.CONTACT_US_MODE
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentContactUsBinding
import javax.inject.Inject

@AndroidEntryPoint
class ContactUsFragment : BaseFragment<FragmentContactUsBinding, ContactUsViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_contact_us

    private val mViewModel: ContactUsViewModel by viewModels()
    override fun getVM() = mViewModel

    private val adapterSpinner by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.list_popup_window_item,
            resources.getStringArray(R.array.nm_contact_us_category)
        )
    }

    override fun initView() {
        super.initView()

        binding.autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            mViewModel.setCategory(parent?.getItemAtPosition(position) as String)
        }

        setUpToolBar()
        setUpEditText()
    }

    override fun onResume() {
        super.onResume()

        binding.autoCompleteTextView.setAdapter(adapterSpinner)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.statusBtnConfirm.observe(viewLifecycleOwner) {
            binding.btnConfirmContent.isEnabled = it
        }

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is ContactUsActionState.NavigateToContactUsConfirm -> {
                    appNavigation.openContactUsToContactUsConfirm(
                        bundleOf(
                            CONTACT_US_CATEGORY to it.category,
                            CONTACT_US_CONTENT to it.content,
                            CONTACT_US_REPLY to it.reply,
                            CONTACT_US_MAIL to it.mail,
                            CONTACT_US_MODE to it.contactUsMode
                        )
                    )
                }
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            mViewModel.setReply(
                if (id == binding.radioBtnNecessary.id) REPLY_REQUIRED
                else REPLY_NOT_REQUIRED
            )
        }

        binding.btnConfirmContent.setOnClickListener {
            mViewModel.handleConfirmContent()
        }
    }

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }
        })
    }

    private fun setUpEditText() {
        binding.edtContent.addTextChangedListener {
            mViewModel.setContent(it.toString().trim())
        }
    }
}