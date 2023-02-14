package jp.careapp.counseling.android.ui.profile.update_trouble_sheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.ConverterUtils
import jp.careapp.core.utils.DateUtil
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_3
import jp.careapp.core.utils.DateUtil.Companion.DATE_FORMAT_9
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.onTextChange
import jp.careapp.core.utils.showDatePicker
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.event.NotifiEvent
import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.network.TroubleSheetResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.DesiredResponseId
import jp.careapp.counseling.android.utils.GenderId
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.android.utils.extensions.updateTextStyleChecked
import jp.careapp.counseling.databinding.FragmentTroubleSheetUpdateBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TroubleSheetUpdateFragment :
    BaseFragment<FragmentTroubleSheetUpdateBinding, TroubleSheetUpdateViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId = R.layout.fragment_trouble_sheet_update
    private val viewModel: TroubleSheetUpdateViewModel by viewModels()
    override fun getVM(): TroubleSheetUpdateViewModel = viewModel
    private val shareViewModel: ShareViewModel by activityViewModels()

    private var firstChat: Int = 0
    private var performerCode = ""
    private var performerName = ""

    // save status is seted content,when back other screen don't setting again.
    private var isLoadedTroubleSheet = false
    private var oldGenrePos: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listenEvent()
    }

    private fun listenEvent() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun initView() {
        super.initView()
        arguments?.let {
            if (it.containsKey(BUNDLE_KEY.FIRST_CHAT)) {
                firstChat = it.getInt(BUNDLE_KEY.FIRST_CHAT)
            }
            if (it.containsKey(BUNDLE_KEY.PERFORMER_CODE)) {
                performerCode = it.getString(BUNDLE_KEY.PERFORMER_CODE, "")
            }
            if (it.containsKey(BUNDLE_KEY.PERFORMER_NAME)) {
                performerName = it.getString(BUNDLE_KEY.PERFORMER_NAME, "")
            }
        }
        shareViewModel.isShowBuyPoint.value = false

        setViewInfo()

        if (!isLoadedTroubleSheet) {
            activity?.let { viewModel.getMemberInfo(it) }
        }

        // get trouble sheet in local
        val troubleSheetResponse = rxPreferences.getTroubleInfo()
        troubleSheetResponse?.let { setContentTrouble(it) }

        setListener()
    }

    private fun updateGenres(genres: List<CategoryResponse>) {
        val genreSelected = rxPreferences.getGenre()
        for (i in genres.indices) {
            val chip = layoutInflater.inflate(
                R.layout.single_chip_layout, binding.cgGenres, false
            ) as Chip
            chip.isChecked = genres[i].id == genreSelected
            if (genres[i].id == genreSelected) {
                oldGenrePos = i
            }
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setGenre(i)
                }
                if (oldGenrePos != -1 && i != oldGenrePos) {
                    val oldChip: Chip = binding.cgGenres.getChildAt(oldGenrePos) as Chip
                    oldChip.isChecked = false
                }
                oldGenrePos = i
                binding.cgGenres.updateTextStyleChecked()
            }
            binding.cgGenres.addView(chip.apply { text = genres[i].name }, i)
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

    private fun setViewInfo() {
        binding.layoutYourInfo.apply {
            tvLabelName.text = getString(R.string.label_your_name)
            edtName.hint = getString(R.string.hint_your_name)
            tvLabelGender.text = getString(R.string.label_your_gender)
            tvLabelBirthday.text = getString(R.string.label_your_birthday)
            edtName.onTextChange {
                it?.let {
                    tvCountName.text = (Define.NAME_MAX_LENGTH - it.length).toString()
                    viewModel.changeMemberInfo(
                        it.toString(), getMyGenderId(), getBirthday(edtBirthday)
                    )
                }
            }
            edtBirthday.setOnClickListener {
                if (!isDoubleClick) {
                    context?.let {
                        it.showDatePicker(getDefaultCalendar(edtBirthday), { _, year, month, day ->
                            Calendar.getInstance().apply { set(year, month, day) }.let { birthday ->
                                edtBirthday.setText(
                                    DateUtil.formatDateToString(DATE_FORMAT_9, birthday.time)
                                )
                                viewModel.changeMemberInfo(
                                    edtName.text.toString(),
                                    getMyGenderId(),
                                    getBirthday(edtBirthday)
                                )
                            }
                        }, true)
                    }
                }
            }
            rgGender.setOnCheckedChangeListener { _, _ ->
                viewModel.changeMemberInfo(
                    edtName.text.toString(), getMyGenderId(), getBirthday(edtBirthday)
                )
            }
        }
        binding.layoutPartnerInfo.apply {
            tvLabelName.text = getString(R.string.label_partner_name)
            edtName.hint = getString(R.string.hint_your_name)
            tvLabelGender.text = getString(R.string.partner_gender)
            tvLabelBirthday.text = getString(R.string.partner_birthday)
            edtName.onTextChange {
                it?.let {
                    tvCountName.text = (Define.NAME_MAX_LENGTH - it.length).toString()
                    viewModel.changePartnerInfo(
                        it.toString(), getPartnerGenderId(), getBirthday(edtBirthday)
                    )
                }
            }
            edtBirthday.setOnClickListener {
                if (!isDoubleClick) {
                    context?.let {
                        it.showDatePicker(getDefaultCalendar(edtBirthday), { _, year, month, day ->
                            Calendar.getInstance().apply { set(year, month, day) }.let { birthday ->
                                edtBirthday.setText(
                                    DateUtil.formatDateToString(DATE_FORMAT_9, birthday.time)
                                )
                                viewModel.changePartnerInfo(
                                    edtName.text.toString(),
                                    getPartnerGenderId(),
                                    getBirthday(edtBirthday)
                                )
                            }
                        }, true)
                    }
                }
            }
            rgGender.setOnCheckedChangeListener { _, _ ->
                viewModel.changePartnerInfo(
                    edtName.text.toString(), getPartnerGenderId(), getBirthday(edtBirthday)
                )
            }
        }
    }

    private fun getBirthday(edtBirthday: AppCompatEditText): String {
        return if (edtBirthday.text.isNullOrEmpty() || edtBirthday.text.toString() == getString(R.string.not_set)) {
            ""
        } else {
            DateUtil.convertStringToDateString(
                edtBirthday.text.toString(), DATE_FORMAT_9, DATE_FORMAT_3
            )
        }
    }

    private fun getMyGenderId(): Int {
        return when (binding.layoutYourInfo.rgGender.checkedRadioButtonId) {
            R.id.rbMale -> GenderId.MALE_ID
            R.id.rbFemale -> GenderId.FEMALE_ID
            R.id.rbOther -> GenderId.OTHER_ID
            else -> GenderId.DEFAULT_ID
        }
    }

    private fun getPartnerGenderId(): Int {
        return when (binding.layoutPartnerInfo.rgGender.checkedRadioButtonId) {
            R.id.rbMale -> GenderId.MALE_ID
            R.id.rbFemale -> GenderId.FEMALE_ID
            R.id.rbOther -> GenderId.OTHER_ID
            else -> GenderId.DEFAULT_ID
        }
    }

    private fun getValueResponse(): Int {
        return when (binding.chooseQuestionChipGroup.checkedChipId) {
            binding.answerAdviceChip.id -> DesiredResponseId.ADVICE_ID
            binding.answerFeelingChip.id -> DesiredResponseId.FEELING_ID
            binding.answerSortFeelingChip.id -> DesiredResponseId.SORT_FEELING_ID
            binding.answerOther.id -> DesiredResponseId.OTHER_ID
            else -> DesiredResponseId.OTHER_ID
        }
    }

    private fun setDataInfo(member: MemberResponse) {
        binding.layoutYourInfo.apply {
            edtName.setText(member.name)
            when (member.sex) {
                GenderId.MALE_ID -> rbMale.isChecked = true
                GenderId.FEMALE_ID -> rbFemale.isChecked = true
                GenderId.OTHER_ID -> rbOther.isChecked = true
            }
            val birthday = DateUtil.convertStringToDateString(
                member.birth, DATE_FORMAT_3, DATE_FORMAT_9
            )
            edtBirthday.setText(birthday.ifEmpty { getString(R.string.not_set) })
        }
        binding.layoutPartnerInfo.apply {
            member.troubleSheetResponse.apply {
                edtName.setText(partnerName)
                when (partnerSex) {
                    GenderId.MALE_ID -> rbMale.isChecked = true
                    GenderId.FEMALE_ID -> rbFemale.isChecked = true
                    GenderId.OTHER_ID -> rbOther.isChecked = true
                }
                val birthday = DateUtil.convertStringToDateString(
                    partnerBirth, DATE_FORMAT_3, DATE_FORMAT_9
                )
                edtBirthday.setText(birthday.ifEmpty { getString(R.string.not_set) })
            }
        }
    }

    private fun setListener() {
        binding.chooseQuestionChipGroup.setOnCheckedChangeListener { _, _ ->
            binding.chooseQuestionChipGroup.updateTextStyleChecked()
            viewModel.changeTroubleSheet(
                binding.contentTroubleSheetEdt.text.toString(), getValueResponse()
            )
        }
    }

    private fun setContentTrouble(troubleSheetResponse: TroubleSheetResponse) {
        if (!isLoadedTroubleSheet) {
            binding.contentTroubleSheetEdt.setText(troubleSheetResponse.content)
            setChipGroupChecked(troubleSheetResponse.response)
        }
    }

    private fun setChipGroupChecked(value: Int) {
        when (value) {
            DesiredResponseId.ADVICE_ID -> binding.answerAdviceChip.isChecked = true
            DesiredResponseId.FEELING_ID -> binding.answerFeelingChip.isChecked = true
            DesiredResponseId.SORT_FEELING_ID -> binding.answerSortFeelingChip.isChecked = true
            DesiredResponseId.OTHER_ID -> binding.answerOther.isChecked = true
            else -> {
                binding.answerAdviceChip.isChecked = false
                binding.answerFeelingChip.isChecked = false
                binding.answerSortFeelingChip.isChecked = false
                binding.answerOther.isChecked = false
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnClick() {
        super.setOnClick()
        binding.toolBar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                handleSaveContent()
            }
        })
        binding.contentTroubleSheetEdt.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener false
            }
        }
        binding.contentTroubleSheetEdt.addTextChangedListener {
            val isHasEmoiji = ConverterUtils.isContainEmoiji(it.toString())
            if (isHasEmoiji) {
                binding.errorImojiTv.visibility = View.VISIBLE
                enableButtonUpdate(false)
            } else {
                binding.errorImojiTv.visibility = View.GONE
                viewModel.changeTroubleSheet(it.toString(), getValueResponse())
            }
            binding.countTextTv.text = (128 - it.toString().length).toString()
        }

        binding.tvSave.setOnClickListener {
            if (!isDoubleClick) {
                activity?.let { viewModel.updateInfo(it) }
            }
        }

        handleBackPress()
    }

    private fun enableButtonUpdate(isEnable: Boolean) {
        binding.tvSave.isEnabled = isEnable
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.genresResult.observe(viewLifecycleOwner) {
            it?.let { updateGenres(it) }
        }
        viewModel.statusSendTrouble.observe(viewLifecycleOwner, handleResultTrouble)
        viewModel.memberInfoResult.observe(viewLifecycleOwner, handleResultMember)
        viewModel.isEnableButtonUpdate.observe(viewLifecycleOwner) {
            it?.let {
                if (firstChat == FIRST_CHAT) {
                    enableButtonUpdate(binding.contentTroubleSheetEdt.text.length <= 128)
                } else {
                    enableButtonUpdate(it)
                }
            }
        }
    }

    private var handleResultTrouble: Observer<Boolean> = Observer {
        if (it) {
            if (firstChat != 0) {
                if ((rxPreferences.getPoint() == 0)) {
                    shareViewModel.isShowBuyPoint.value = true
                    appNavigation.navigateUp()
                } else {
                    val bundle = Bundle()
                    bundle.putString(BUNDLE_KEY.PERFORMER_CODE, performerCode)
                    bundle.putString(BUNDLE_KEY.PERFORMER_NAME, performerName)
                    bundle.putBoolean(
                        BUNDLE_KEY.IS_SHOW_FREE_MESS,
                        arguments?.getBoolean(BUNDLE_KEY.IS_SHOW_FREE_MESS) ?: false
                    )
                    bundle.putInt(
                        BUNDLE_KEY.CALL_RESTRICTION,
                        arguments?.getInt(BUNDLE_KEY.CALL_RESTRICTION) ?: 0
                    )
                    appNavigation.openTroubleSheetUpdateToChatMessage(bundle)
                }
            } else {
                CommonAlertDialog.getInstanceCommonAlertdialog(requireContext()).showDialog()
                    .setDialogTitle(R.string.save_trouble_sheet_suscess)
                    .setTextPositiveButton(R.string.text_OK).setOnPositivePressed { dialog ->
                        dialog.dismiss()
                        appNavigation.navigateUp()
                    }
            }
        }
    }

    private var handleResultMember: Observer<MemberResponse?> = Observer {
        it?.let { member ->
            member.troubleSheetResponse.let { troubleSheet ->
                if (!isLoadedTroubleSheet) {
                    setDataInfo(member)
                    setContentTrouble(troubleSheet)
                    isLoadedTroubleSheet = true
                }
            }
        }
    }

    private fun handleBackPress() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    handleSaveContent()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun handleSaveContent() {
        if (!isChangeContent()) {
            appNavigation.navigateUp()
        } else {
            CommonAlertDialog.getInstanceCommonAlertdialog(requireContext()).showDialog()
                .setDialogTitle(R.string.confirm_save_trouble_sheet)
                .setTextPositiveButton(R.string.confirm_block_alert)
                .setTextNegativeButton(R.string.no_block_alert).setOnPositivePressed {
                    it.dismiss()
                    appNavigation.navigateUp()
                }.setOnNegativePressed {
                    it.dismiss()
                }
        }
    }

    private fun isChangeContent(): Boolean {
        return viewModel.isEnableButtonUpdate.value ?: false
    }

    override fun onDestroy() {
        activity?.let {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onMessageEvent(event: NotifiEvent) {
        handleSaveContent()
    }

    companion object {
        const val FIRST_CHAT = 1
    }
}
