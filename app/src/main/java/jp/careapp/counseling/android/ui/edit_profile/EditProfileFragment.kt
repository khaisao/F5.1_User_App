package jp.careapp.counseling.android.ui.edit_profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.executeAfter
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.ParamsUpdateMember
import jp.careapp.counseling.android.data.model.TypeMember
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.email.InputAndEditMailViewModel.Companion.SCREEN_EDIT_EMAIL
import jp.careapp.counseling.android.ui.main.OnBackPressedListener
import jp.careapp.counseling.android.ui.mypage.MyPageViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.SignedUpStatus
import jp.careapp.counseling.android.utils.customView.GenderSelectView
import jp.careapp.counseling.android.utils.event.EventObserver
import jp.careapp.counseling.databinding.FragmentEditProfileBinding
import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, EditProfileViewModel>(),
    OnBackPressedListener {

    @Inject
    lateinit var rxPreferences: RxPreferences

    @Inject
    lateinit var appNavigation: AppNavigation
    private val viewModels: EditProfileViewModel by activityViewModels()
    private val myPageViewModels: MyPageViewModel by activityViewModels()
    override val layoutId = R.layout.fragment_edit_profile
    override fun getVM(): EditProfileViewModel = viewModels
    private var bundle: MemberResponse? = null
    private var codeScreen = 0
    private val shareViewModel: ShareViewModel by activityViewModels()
    private var email = ""

    var mCalendar = Calendar.getInstance()

    override fun initView() {
        super.initView()
        bundle = arguments?.getParcelable("member")
        arguments?.let {
            if (it.containsKey(BUNDLE_KEY.CODE_SCREEN)) {
                codeScreen = it.getInt(BUNDLE_KEY.CODE_SCREEN)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun bindingStateView() {
        super.bindingStateView()
        val dialog = CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
        val singleItem = arrayOf(
            resources.getString(R.string.label_male),
            resources.getString(R.string.label_female),
            resources.getString(R.string.label_other)
        )

        binding.executeAfter {
            appBar.btnLeft.apply {
                setOnClickListener {
                    if (!isDoubleClick) {
                        appNavigation.navigateUp()
                    }
                }
                setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_left))
            }
            appBar.tvTitle.text = getString(R.string.title_edit_profile)
            appBar.viewStatusBar.visibility = View.GONE
            myPageViewModels.uiMember.observe(
                viewLifecycleOwner,
                Observer { rs ->
                    mCalendar = DateUtil.convertStringToCalendar(rs.birth, DateUtil.DATE_FORMAT_3)
                    viewModels.setCurrentName(
                        rs.name,
                        ParamsUpdateMember(
                            rs.name,
                            rs.sex,
                            rs.birth
                        )
                    )
                    tvName.apply {
                        tvStart.text = getString(R.string.nick_name)
                        tvEnd.text = rs.name
                        item.setOnClickListener {
                            if (!isDoubleClick)
                                viewModels.destinationEdit(
                                    EditProfile(
                                        rs.name,
                                        TypeMember.EDIT,
                                        DestinationEdit.NAME
                                    )
                                )
                        }
                    }
                    tvGender.apply {
                        tvStart.text = getString(R.string.gender)
                        tvEnd.text = checkGender(rs.sex)
                        item.setOnClickListener {
                            binding.genderSelectView.visibility = View.VISIBLE
                            binding.genderSelectView.setDefaultSelected(checkGenderInt(binding.tvGender.tvEnd.text.toString()))
                            binding.genderSelectView.setOnChooseGender(
                                object :
                                    GenderSelectView.ChooseGender {
                                    override fun choose(pos: Int, title: String?) {
                                        binding.genderSelectView.visibility = View.GONE
                                        if (pos != checkGenderInt(binding.tvGender.tvEnd.text.toString())) {
                                            viewModels.setParamProfile(
                                                ParamsUpdateMember(
                                                    rs.name,
                                                    pos,
                                                    rs.birth
                                                )
                                            )
                                            binding.tvGender.tvEnd.text = checkGender(pos)
                                            viewModels.updateProfileSuccess.observe(
                                                viewLifecycleOwner,
                                                Observer {
                                                    dialog
                                                        .showDialog()
                                                        .setDialogTitle(R.string.updated_my_profile)
                                                        .setTextPositiveButton(R.string.text_OK)
                                                        .setOnPositivePressed {
                                                            it.dismiss()
                                                        }
                                                }
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }

                    tvAge.apply {
                        tvStart.text = getString(R.string.age)
                        myPageViewModels.uiMember.observe(
                            viewLifecycleOwner,
                            Observer {
                                tvEnd.text = "${it.age}歳"
                                if (it.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL) {
                                    binding.apply {
                                        tvMail.tvEnd.text = getString(R.string.unregistered)
                                        tvCaution.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding.apply {
                                        tvMail.tvEnd.text = email
                                        tvCaution.visibility = View.GONE
                                    }
                                }
                                rxPreferences.setSignedUpStatus(
                                    it.signupStatus ?: SignedUpStatus.UNKNOWN
                                )
                            }
                        )
                        divider.visibility = View.GONE
                        item.setOnClickListener {
                            var check = false
                            val date =
                                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                                    if (mCalendar.get(Calendar.YEAR) == year &&
                                        mCalendar.get(Calendar.MONTH) == monthOfYear &&
                                        mCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth
                                    )
                                        check = false
                                    else {
                                        check = true
                                        mCalendar.set(Calendar.YEAR, year)
                                        mCalendar.set(Calendar.MONTH, monthOfYear)
                                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                        viewModels.setParamProfile(
                                            ParamsUpdateMember(
                                                rs.name,
                                                rs.sex,
                                                DateUtil.getDateTimeDisplayByFormat(
                                                    DateUtil.DATE_FORMAT_3,
                                                    mCalendar
                                                )
                                            )
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
                                setOnDismissListener {
                                    if (check)
                                        viewModels.updateProfileSuccess.observe(
                                            viewLifecycleOwner,
                                            Observer {
                                                dialog
                                                    .showDialog()
                                                    .setDialogTitle(R.string.updated_my_profile)
                                                    .setTextPositiveButton(R.string.text_OK)
                                                    .setOnPositivePressed {
                                                        it.dismiss()
                                                        viewModels.updateProfileLoading.observe(
                                                            viewLifecycleOwner,
                                                            Observer {
                                                                showHideLoading(it)
                                                            }
                                                        )
                                                        val dateTime = mCalendar.toInstant().atZone(
                                                            ZoneId.systemDefault()
                                                        ).toLocalDate()
                                                        binding.tvAge.tvEnd.text = "${
                                                            dateTime.until(
                                                                LocalDate.now(),
                                                                ChronoUnit.YEARS
                                                            )
                                                        }歳"
                                                        check = false
                                                    }
                                            }
                                        )
                                }
                                show()
                            }
                        }
                    }
                    tvCode.apply {
                        tvStart.text = getString(R.string.member_code)
                        tvEnd.text = rs.code
                        ivEnd.visibility = View.GONE
                    }
                    tvPoint.apply {
                        val point = NumberFormat.getInstance(Locale.JAPAN).format(rs.point)
                        tvStart.text = getString(R.string.retention_point)
                        tvEnd.text = "${point}pts"
                        ivEnd.visibility = View.GONE
                    }
                    tvMail.apply {
                        tvStart.text = getString(R.string.email_address)
                        tvEnd.text =
                            if (rs.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL) getString(R.string.unregistered)
                            else rs.mail
                        email = rs.mail
                        divider.visibility = View.GONE
                        item.setOnClickListener {
                            if (!isDoubleClick) {
                                val bundle = Bundle().apply {
                                    putInt(BUNDLE_KEY.CODE_SCREEN, SCREEN_EDIT_EMAIL)
                                    putString(BUNDLE_KEY.EMAIL, rs.mail)
                                }
                                appNavigation.openEditProfileToEditEmailScreen(bundle)
                            }
                        }
                    }

                    if (rs.signupStatus == SignedUpStatus.LOGIN_WITHOUT_EMAIL) {
                        binding.apply {
                            tvMail.tvEnd.text = getString(R.string.unregistered)
                            tvCaution.visibility = View.VISIBLE
                        }
                    } else {
                        binding.apply {
                            tvMail.tvEnd.text = email
                            tvCaution.visibility = View.GONE
                        }
                    }
                    rxPreferences.setSignedUpStatus(rs.signupStatus ?: SignedUpStatus.UNKNOWN)
                }
            )
        }

        viewModels.updateSuccess.observe(
            viewLifecycleOwner,
            Observer {
                binding.tvName.tvEnd.text = it
            }
        )
        viewModels.error.observe(
            viewLifecycleOwner,
            Observer {
            }
        )
        myPageViewModels.memberLoading.observe(
            viewLifecycleOwner,
            Observer {
                showHideLoading(it)
            }
        )
        viewModels.navigateToEditProfileFragmentAction.observe(
            viewLifecycleOwner,
            EventObserver {
                val data = Bundle().apply {
                    putString("data", it.data)
                    putString("action", it.action.toString())
                }
                when (it.destination) {
                    DestinationEdit.NAME -> {
                        findNavController().navigate(
                            R.id.action_fragmentEditProfile_to_registerNameFragment2,
                            data
                        )
                    }
                    DestinationEdit.GENDER -> {
                        // TODO
                    }
                    DestinationEdit.AGE -> {
                        // TODO
                    }
                    DestinationEdit.MAIL -> {
                        // TODO
                    }
                }
            }
        )
    }

    fun checkGender(sex: Int): String {
        if (sex == 1) return getString(R.string.label_male) else if (sex == 2) return getString(R.string.label_female) else return getString(
            R.string.label_other
        )
    }

    fun checkGenderInt(sex: String): Int {
        if (sex == getString(R.string.label_male)) return 1 else if (sex == getString(R.string.label_female)) return 2 else return 3
    }

    override fun onResume() {
        super.onResume()
        myPageViewModels.forceRefresh()
    }

    override fun onBackPressed() {
        if (codeScreen == SCREEN_EDIT_EMAIL) {
            shareViewModel.setTabSelected(ShareViewModel.TAB_MY_PAGE_SELECTED)
            appNavigation.openOtherScreenToTopScreen()
        }
    }
}
