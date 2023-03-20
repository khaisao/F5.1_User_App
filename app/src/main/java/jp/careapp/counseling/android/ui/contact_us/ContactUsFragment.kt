package jp.careapp.counseling.android.ui.contact_us

import android.view.View
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.contact_us.confirm.CONTACT_US_CATEGORY
import jp.careapp.counseling.android.ui.contact_us.confirm.CONTACT_US_CONTENT
import jp.careapp.counseling.android.ui.contact_us.confirm.CONTACT_US_REPLY
import jp.careapp.counseling.android.ui.review_mode.settingContact.SpinnerAdapter
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentContactUsBinding
import javax.inject.Inject

@AndroidEntryPoint
class ContactUsFragment : BaseFragment<FragmentContactUsBinding, ContactUsViewModel>(),
    AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_contact_us

    private val mViewModel: ContactUsViewModel by viewModels()
    override fun getVM() = mViewModel

    private val adapterSpinner by lazy {
        SpinnerAdapter(
            requireContext(), R.layout.list_popup_window_item, arrayListOf(
                getString(R.string.contact_category_please_select),
                getString(R.string.case_payment),
                getString(R.string.case_performer),
                getString(R.string.case_account),
                getString(R.string.case_feature),
                getString(R.string.case_other)
            )
        )
    }

    override fun initView() {
        super.initView()

        binding.spinner.adapter = adapterSpinner
        binding.spinner.onItemSelectedListener = this

        setUpToolBar()
        setUpEditText()
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
                            CONTACT_US_REPLY to it.reply
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mViewModel.setCategory(parent?.getItemAtPosition(position) as String)
    }

    private fun setUpEditText() {
        binding.edtContent.addTextChangedListener {
            mViewModel.setContent(it.toString().trim())
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}