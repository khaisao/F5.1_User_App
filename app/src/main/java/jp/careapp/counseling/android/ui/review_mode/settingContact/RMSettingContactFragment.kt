package jp.careapp.counseling.android.ui.review_mode.settingContact

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentRmSettingContactBinding
import javax.inject.Inject

@AndroidEntryPoint
class RMSettingContactFragment :
    BaseFragment<FragmentRmSettingContactBinding, RMSettingContactViewModel>(),
    AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId: Int = R.layout.fragment_rm_setting_contact

    private val mViewModel: RMSettingContactViewModel by viewModels()
    override fun getVM(): RMSettingContactViewModel = mViewModel

    private val adapterSpinner by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.rm_list_popup_window_item,
            arrayListOf(getString(R.string.contact_category_about_account_deletion))
        )
    }

    override fun initView() {
        super.initView()

        binding.spinner.adapter = adapterSpinner
        binding.spinner.onItemSelectedListener = this

        setUpToolBar()
        setUpEditText()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.btnSend.setOnClickListener { if (!isDoubleClick) mViewModel.sendContact() }

        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            mViewModel.setReply(
                if (id == binding.radioBtnNecessary.id) REPLY_REQUIRED
                else REPLY_NOT_REQUIRED
            )
        }
    }

    private fun setUpEditText() {
        binding.edtContactContent.addTextChangedListener {
            mViewModel.setContent(it.toString().trim())
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mViewModel.setCategory(parent?.getItemAtPosition(position) as String)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    private fun setUpToolBar() {
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick) appNavigation.navigateUp()
            }
        })
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.isEnableBtnSend.observe(viewLifecycleOwner) {
            binding.btnSend.isEnabled = it
        }

        mViewModel.actionState.observe(viewLifecycleOwner) {
            if (it is ActionState.SendContactSuccess) {
                if (it.isSuccess) {
                    appNavigation.openRMSettingContactToRMSettingContactFinish()
                }
            }
        }
    }

    companion object {
        const val REPLY_NOT_REQUIRED = 0
        const val REPLY_REQUIRED = 1
    }
}