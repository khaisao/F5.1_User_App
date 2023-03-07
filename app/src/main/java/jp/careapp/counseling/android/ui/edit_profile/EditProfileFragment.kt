package jp.careapp.counseling.android.ui.edit_profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.android.utils.event.EventObserver
import jp.careapp.counseling.databinding.FragmentEditProfileBinding
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, EditProfileViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId = R.layout.fragment_edit_profile

    private val mViewModel: EditProfileViewModel by activityViewModels()
    override fun getVM() = mViewModel


//    @Inject
//    lateinit var rxPreferences: RxPreferences
//    private var bundle: MemberResponse? = null
//    private var codeScreen = 0

    override fun initView() {
        super.initView()
//        bundle = arguments?.getParcelable("member")
//        arguments?.let {
//            if (it.containsKey(BUNDLE_KEY.CODE_SCREEN)) {
//                codeScreen = it.getInt(BUNDLE_KEY.CODE_SCREEN)
//            }
//        }

        binding.viewModel = mViewModel

        mViewModel.getData()

        setUpToolBar()

        binding.llMemberName.setOnClickListener { if (!isDoubleClick) appNavigation.openEditProfileToEditNickName() }

        binding.llMemberBirth.setOnClickListener { if (!isDoubleClick) showDatePickerDialog() }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun bindingStateView() {
        super.bindingStateView()

        mViewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is EditProfileActionState.UpdateMemberInfoSuccess -> showDialogUpdateMemberInfoSuccess()
            }
        }

//        binding.executeAfter {
//            myPageViewModels.uiMember.observe(
//                viewLifecycleOwner,
//                Observer { rs ->
//                    mCalendar = DateUtil.convertStringToCalendar(rs.birth, DateUtil.DATE_FORMAT_3)
//                    mViewModel.setCurrentName(
//                        rs.name,
//                        ParamsUpdateMember(
//                            rs.name,
//                            rs.sex,
//                            rs.birth
//                        )
//                    )
//                    tvName.apply {
//                        tvStart.text = getString(R.string.nick_name)
//                        tvEnd.text = rs.name
//                        item.setOnClickListener {
//                            if (!isDoubleClick)
//                                mViewModel.destinationEdit(
//                                    EditProfile(
//                                        rs.name,
//                                        TypeMember.EDIT,
//                                        DestinationEdit.NAME
//                                    )
//                                )
//                        }
//                    }
//
//                    tvAge.apply {
//                        tvStart.text = getString(R.string.age)
//                        myPageViewModels.uiMember.observe(
//                            viewLifecycleOwner,
//                            Observer {
//                                tvEnd.text = "${it.age}歳"
//                                if (it.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL) {
//                                    binding.apply {
//                                        tvMail.tvEnd.text = getString(R.string.unregistered)
//                                    }
//                                } else {
//                                    binding.apply {
//                                        tvMail.tvEnd.text = email
//                                    }
//                                }
//                                rxPreferences.setSignedUpStatus(
//                                    it.signupStatus ?: SignedUpStatus.UNKNOWN
//                                )
//                            }
//                        )
//                        divider.visibility = View.GONE
//                        item.setOnClickListener {
//                            var check = false
//                            val date =
//                                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
//                                    if (mCalendar.get(Calendar.YEAR) == year &&
//                                        mCalendar.get(Calendar.MONTH) == monthOfYear &&
//                                        mCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth
//                                    )
//                                        check = false
//                                    else {
//                                        check = true
//                                        mCalendar.set(Calendar.YEAR, year)
//                                        mCalendar.set(Calendar.MONTH, monthOfYear)
//                                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                                        mViewModel.setParamProfile(
//                                            ParamsUpdateMember(
//                                                rs.name,
//                                                rs.sex,
//                                                DateUtil.getDateTimeDisplayByFormat(
//                                                    DateUtil.DATE_FORMAT_3,
//                                                    mCalendar
//                                                )
//                                            )
//                                        )
//                                    }
//                                }
//                            DatePickerDialog(
//                                requireContext(),
//                                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
//                                date,
//                                mCalendar.get(Calendar.YEAR),
//                                mCalendar.get(Calendar.MONTH),
//                                mCalendar.get(Calendar.DAY_OF_MONTH)
//                            ).apply {
//                                datePicker.maxDate = System.currentTimeMillis() - 568111536000L
//                                show()
//                            }
//                        }
//                    }
//                    tvMail.apply {
//                        tvStart.text = getString(R.string.email_address)
//                        tvEnd.text =
//                            if (rs.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL) getString(R.string.unregistered)
//                            else rs.mail
//                        email = rs.mail
//                        divider.visibility = View.GONE
//                        item.setOnClickListener {
//                            if (!isDoubleClick) {
//                                val bundle = Bundle().apply {
//                                    putInt(BUNDLE_KEY.CODE_SCREEN, SCREEN_EDIT_EMAIL)
//                                    putString(BUNDLE_KEY.EMAIL, rs.mail)
//                                }
//                                appNavigation.openEditProfileToEditEmailScreen(bundle)
//                            }
//                        }
//                    }
//
//                    if (rs.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL) {
//                        binding.apply {
//                            tvMail.tvEnd.text = getString(R.string.unregistered)
//                            tvCaution.visibility = View.VISIBLE
//                        }
//                    } else {
//                        binding.apply {
//                            tvMail.tvEnd.text = email
//                            tvCaution.visibility = View.GONE
//                        }
//                    }
//                    rxPreferences.setSignedUpStatus(rs.signupStatus ?: SignedUpStatus.UNKNOWN)
//                }
//            )
//        }


//        myPageViewModels.memberLoading.observe(
//            viewLifecycleOwner,
//            Observer {
//                showHideLoading(it)
//            }
//        )
    }

//    override fun onResume() {
//        super.onResume()
//        myPageViewModels.forceRefresh()
//    }
//
//    override fun onBackPressed() {
//        if (codeScreen == SCREEN_EDIT_EMAIL) {
//            shareViewModel.setTabSelected(ShareViewModel.TAB_MY_PAGE_SELECTED)
//            appNavigation.openOtherScreenToTopScreen()
//        }
//    }

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

//        dialog
//            .showDialog()
//            .setDialogTitle(R.string.updated_my_profile)
//            .setTextPositiveButton(R.string.text_OK)
//            .setOnPositivePressed {
//                it.dismiss()
//                mViewModel.updateProfileLoading.observe(
//                    viewLifecycleOwner,
//                    Observer {
//                        showHideLoading(it)
//                    }
//                )
//                val dateTime = mCalendar.toInstant().atZone(
//                    ZoneId.systemDefault()
//                ).toLocalDate()
//                binding.tvAge.tvEnd.text = "${
//                    dateTime.until(
//                        LocalDate.now(),
//                        ChronoUnit.YEARS
//                    )
//                }歳"
//                check = false
//            }
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
//                    mViewModel.setParamProfile(
//                        ParamsUpdateMember(
//                            rs.name,
//                            rs.sex,
//                            DateUtil.getDateTimeDisplayByFormat(
//                                DateUtil.DATE_FORMAT_3,
//                                mCalendar
//                            )
//                        )
//                    )
                Log.e(
                    "haidang",
                    "${DateUtil.getDateTimeDisplayByFormat(DateUtil.DATE_FORMAT_3, mCalendar)}"
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
