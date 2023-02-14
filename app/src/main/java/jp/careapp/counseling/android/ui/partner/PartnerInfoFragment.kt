package jp.careapp.counseling.android.ui.partner

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_3
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_9
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.onTextChange
import jp.careapp.core.utils.showDatePicker
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.Define.Companion.NAME_MAX_LENGTH
import jp.careapp.counseling.databinding.FragmentPartnerInfoBinding
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PartnerInfoFragment : BaseFragment<FragmentPartnerInfoBinding, PartnerInfoViewModel>() {

    @Inject
    lateinit var navigation: AppNavigation

    private val viewModel: PartnerInfoViewModel by viewModels()
    override val layoutId = R.layout.fragment_partner_info
    override fun getVM(): PartnerInfoViewModel = viewModel

    override fun initView() {
        super.initView()

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            })

        binding.apply {
            layoutPartnerInfo.apply {
                tvSkipName.isVisible = true
                tvSkipGender.isVisible = true
                tvSkipBirthday.isVisible = true
                edtName.onTextChange {
                    it?.let {
                        tvCountName.text = (NAME_MAX_LENGTH - it.length).toString()
                        viewModel.setPartnerName(it.toString())
                    }
                }

                rgGender.setOnCheckedChangeListener { _, id ->
                    when (id) {
                        R.id.rbMale -> viewModel.setGender(1)
                        R.id.rbFemale -> viewModel.setGender(2)
                        R.id.rbOther -> viewModel.setGender(3)
                    }
                }

                edtBirthday.setOnClickListener {
                    if (!isDoubleClick) {
                        DeviceUtil.hideSoftKeyboard(activity)
                        showDatePickerDialog()
                    }
                }
            }

            tvStart.setOnClickListener {
                if (!isDoubleClick) {
                    viewModel.updatePartnerInfo()
                }
            }
        }
    }

    override fun bindingAction() {
        super.bindingAction()

        viewModel.actionState.observe(viewLifecycleOwner) {
            when (it) {
                ActionState.OpenTutorialScreen -> navigation.openPartnerInfoToTutorialScreen()
                ActionState.OpenTopScreen -> navigation.openPartnerInfoToTopScreen()
            }
        }
    }

    private fun getDefaultCalendar(editText: AppCompatEditText): Calendar {
        return if (editText.text.isNullOrEmpty() || editText.text.toString() == getString(R.string.not_set)) {
            Calendar.getInstance().apply { set(Calendar.YEAR, get(Calendar.YEAR) - 18) }
        } else {
            DateUtil.convertStringToCalendar(editText.text.toString(), DATE_FORMAT_9)
                ?: Calendar.getInstance().apply { set(Calendar.YEAR, get(Calendar.YEAR) - 18) }
        }
    }

    private fun showDatePickerDialog() {
        context?.let {
            it.showDatePicker(
                getDefaultCalendar(binding.layoutPartnerInfo.edtBirthday),
                { _, year, month, day ->
                    Calendar.getInstance().apply { set(year, month, day) }.let { birthday ->
                        binding.layoutPartnerInfo.edtBirthday.setText(
                            DateUtil.formatDateToString(DATE_FORMAT_9, birthday.time)
                        )
                        viewModel.setBirthday(
                            DateUtil.formatDateToString(DATE_FORMAT_3, birthday.time)
                        )
                    }
                },
                true
            )
        }
    }
}