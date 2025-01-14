package jp.slapp.android.android.ui.edit_profile

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.slapp.android.R
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.SignupStatusMode
import jp.slapp.android.android.utils.customView.ToolBarCommon
import jp.slapp.android.databinding.FragmentEditProfileBinding
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, EditProfileViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId = R.layout.fragment_edit_profile

    private val mViewModel: EditProfileViewModel by viewModels()
    override fun getVM() = mViewModel

    @Inject
    lateinit var rxPreferences: RxPreferences

    override fun initView() {
        super.initView()

        binding.viewModel = mViewModel

        mViewModel.getData()

        setUpToolBar()
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.llMemberName.setOnClickListener { if (!isDoubleClick) appNavigation.openEditProfileToEditNickName() }

        binding.llMemberBirth.setOnClickListener { if (!isDoubleClick) showDatePickerDialog() }

        binding.llMemberMail.setOnClickListener { if (!isDoubleClick) appNavigation.openEditMail() }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is EditProfileActionState.UpdateMemberBirthSuccess -> showDialogUpdateMemberInfoSuccess()
            }
        }

        mViewModel.memberMail.observe(viewLifecycleOwner) {
            if (rxPreferences.getSignupStatus() == SignupStatusMode.WITHOUT_VERIFY_EMAIL) {
                binding.tvEmail.text = getString(R.string.not_set)
                binding.tvEmail.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_alert_error
                    )
                )
            } else {
                binding.tvEmail.text = it
                binding.tvEmail.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_text_F0F0F0
                    )
                )
            }
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

    private fun showDialogUpdateMemberInfoSuccess() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.updated_my_profile)
            .setTextOkButton(R.string.text_OK)
            .setOnOkButtonPressed { it.dismiss() }
    }

    private fun showDatePickerDialog() {
        val mCalendar = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            if (mCalendar.get(Calendar.YEAR) != year
                || mCalendar.get(Calendar.MONTH) != monthOfYear
                || mCalendar.get(Calendar.DAY_OF_MONTH) != dayOfMonth
            ) {
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, monthOfYear)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                mViewModel.editMemberBirth(
                    DateUtil.getDateTimeDisplayByFormat(
                        DateUtil.DATE_FORMAT_3,
                        mCalendar
                    ), year, monthOfYear, dayOfMonth
                )
            }
        }
        DatePickerDialog(
            requireContext(),
            AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
            date,
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis() - 568111536000L
            show()
        }
    }
}
