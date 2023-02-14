package jp.careapp.counseling.android.ui.profile.trouble_sheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.ConverterUtils
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.InfoRegistrationWithoutEmailRequest
import jp.careapp.counseling.android.data.model.InforRegistrationRequest
import jp.careapp.counseling.android.data.model.user_profile.TroubleSheetRequest
import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.network.TroubleSheetResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.verifyCode.VerifyCodeViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentTroubleSheetBinding
import javax.inject.Inject

@AndroidEntryPoint
class TroubleSheetFragment :
    BaseFragment<FragmentTroubleSheetBinding, TroubleSheetViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences
    override val layoutId = R.layout.fragment_trouble_sheet

    private val viewModel: TroubleSheetViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()

    override fun getVM(): TroubleSheetViewModel = viewModel

    private var valueOfResponse = 0
    private var valueOfGenre = -1
    private var defaultReply = 1
    private var isLoginWithEmail: Boolean = false
    private var registrationWithoutEmailRequest: InfoRegistrationWithoutEmailRequest? = null
    private var registrationRequest: InforRegistrationRequest? = null
    private var isLoadedTroubleSheet = false
    private var contentTrouble: TroubleSheetResponse = TroubleSheetResponse("", 0, 0)
    private var enableBtn = false
    private var itemSelected: Int = -1
    private val listCategory: List<CategoryResponse>? by lazy {
        rxPreferences.getListCategory()
    }
    private var isEnterLater: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shareViewModel.isShowBuyPoint.value = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        super.initView()
        arguments?.let { it ->
            if (it.containsKey(BUNDLE_KEY.TITLE_TROUBLE_SHEET)) {
                it.getString(BUNDLE_KEY.TITLE_TROUBLE_SHEET)?.let { it ->
                    binding.toolBar.setTvTitle(it)
                }
            }
            if (it.containsKey(BUNDLE_KEY.DESIRED_RESPONSE)) {
                valueOfResponse = it.getInt(BUNDLE_KEY.DESIRED_RESPONSE, 0)
            }
            if (it.containsKey(BUNDLE_KEY.PARAM_GENRE)) {
                valueOfGenre = it.getInt(BUNDLE_KEY.PARAM_GENRE, -1)
            }
            if (it.containsKey(BUNDLE_KEY.PARAM_LOGIN_WITH_EMAIL)) {
                isLoginWithEmail = it.getBoolean(BUNDLE_KEY.PARAM_LOGIN_WITH_EMAIL)
            }
            if (it.containsKey(BUNDLE_KEY.PARAM_REGISTRATION)) {
                if (isLoginWithEmail) {
                    registrationRequest =
                        it.getSerializable(BUNDLE_KEY.PARAM_REGISTRATION) as InforRegistrationRequest
                } else {
                    registrationWithoutEmailRequest =
                        it.getSerializable(BUNDLE_KEY.PARAM_REGISTRATION) as InfoRegistrationWithoutEmailRequest
                }
            }
        }

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
        if (!isLoadedTroubleSheet && registrationWithoutEmailRequest == null && registrationRequest == null) {
            activity?.let { viewModel.getMemberInfo(it) }
        }

        // get trouble sheet in local
        val troubleSheetResponse = rxPreferences.getTroubleInfo()
        troubleSheetResponse?.let { setContentTrouble(it) }

        setContentTroubleSheet()
    }

    private fun setContentTrouble(troubleSheetResponse: TroubleSheetResponse) {
        if (!isLoadedTroubleSheet) {
            contentTrouble = troubleSheetResponse
            binding.contentTroubleSheetEdt.setText(troubleSheetResponse.content)
        }

        setContentTroubleSheet()
    }

    private fun setContentTroubleSheet() {
        val bundle = arguments
        if (bundle != null) {
            bundle.getInt(BUNDLE_KEY.ITEM_SELECT, -1)?.let {
                itemSelected = it
                listCategory?.let {
                    if (itemSelected != -1) {
                        val selectedCategory: List<CategoryResponse> =
                            listCategory!!.filter { it.id == itemSelected }
                        binding.contentTroubleSheetEdt.hint = "例）${
                            selectedCategory[0].defaultContent.replace(
                                "\\n",
                                System.getProperty("line.separator")
                            )
                        }"
                    }
                }
            }

            appNavigation.navController?.previousBackStackEntry?.savedStateHandle?.set(
                BUNDLE_KEY.ITEM_SELECT,
                itemSelected
            )
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.toolBar.setOnToolBarClickListener(
            object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    val bundle = arguments
                    if (bundle != null) {
                        if (bundle.containsKey(BUNDLE_KEY.TITLE_TROUBLE_SHEET)) {
                            appNavigation.navigateUp()
                            return
                        }
                    }

                    if (isOpenBackPress()) {
                        appNavigation.navigateUp()
                    } else {
                        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                            .showDialog()
                            .setDialogTitle(R.string.confirm_save_trouble_sheet)
                            .setTextPositiveButton(R.string.confirm_block_alert)
                            .setTextNegativeButton(R.string.no_block_alert)
                            .setOnPositivePressed {
                                it.dismiss()
                                appNavigation.navigateUp()
                            }.setOnNegativePressed {
                                it.dismiss()
                            }
                    }
                }
            }
        )
        binding.contentTroubleSheetEdt.addTextChangedListener {
            val isHasEmoiji =
                ConverterUtils.isContainEmoiji(it.toString())
            if (isHasEmoiji) {
                binding.errorImojiTv.visibility = View.VISIBLE
                activeBtnSendMessage(false)
            } else {
                binding.errorImojiTv.visibility = View.GONE
                checkEnableButton()
            }
            binding.countTextTv.setText((128 - it.toString().length).toString())
        }

        binding.sendMessageBtn.setOnClickListener {
            if (!isDoubleClick && enableBtn) {
                if (isLoginWithEmail) {
                    registrationRequest?.let { viewModel.register(it) }
                } else {
                    if (registrationWithoutEmailRequest == null)
                        sendTroubleSheet(rxPreferences.getDesiredResponse())
                    else
                        viewModel.registerWithoutEmail(registrationWithoutEmailRequest!!)
                }
            }
        }

        binding.tvEnterLater.setOnClickListener {
            isEnterLater = true
            if (!isDoubleClick) {
                if (isLoginWithEmail) {
                    registrationRequest?.let { viewModel.register(it) }
                } else {
                    if (registrationWithoutEmailRequest == null)
                        sendTroubleSheet(rxPreferences.getDesiredResponse())
                    else
                        viewModel.registerWithoutEmail(registrationWithoutEmailRequest!!)
                }
            }
        }

        handleBackPress()
    }

    private fun isOpenBackPress(): Boolean {
        return contentTrouble.content.equals(binding.contentTroubleSheetEdt.text.toString())
    }

    private fun checkEnableButton() {
        if (TextUtils.isEmpty(
                binding.contentTroubleSheetEdt.text.toString().trim()
            ) || binding.contentTroubleSheetEdt.text.toString().length > 128
        ) {
            activeBtnSendMessage(false)
        } else {
            activeBtnSendMessage(true)
        }
    }

    private fun activeBtnSendMessage(enable: Boolean) {
        this.enableBtn = enable
        if (enable) {
            binding.sendMessageBtn.setBackgroundResource(R.drawable.bg_corner_26dp_978dff_alpha)
            binding.sendMessTv.setTextColor(resources.getColor(R.color.white))
        } else {
            binding.sendMessageBtn.setBackgroundResource(R.drawable.bg_disable_button)
            binding.sendMessTv.setTextColor(resources.getColor(R.color.color_6D5D9A))
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.statusSendTrouble.observe(viewLifecycleOwner, handleResultTrouble)
        viewModel.memberInFoResult.observe(viewLifecycleOwner, handleResultMember)
        viewModel.codeScreenAfterRegistration.observeForever(openScreenObserver)
        viewModel.statusUpdateGenre.observeForever(handleResultGenre)
    }

    private var openScreenObserver: Observer<Int> = Observer {
        when (it) {
            VerifyCodeViewModel.SCREEN_CODE_SELECT_CATEGORY -> {
                shareViewModel.setHaveToken(true)
                viewModel.updateGenre(valueOfGenre)
            }
        }
    }

    private var handleResultGenre: Observer<Int> = Observer {
        when (it) {
            VerifyCodeViewModel.SCREEN_CODE_TUTORIAL -> {
                sendTroubleSheet()
            }
        }
    }

    private fun sendTroubleSheet(response: Int = 0) {
        valueOfResponse = if (response == 0) valueOfResponse else response
        val troubleSheetRequest = TroubleSheetRequest(
            if (isEnterLater) "" else binding.contentTroubleSheetEdt.text.toString().trim(),
            valueOfResponse,
            defaultReply
        )
        isEnterLater = false
        activity?.let { instance ->
            viewModel.sendTroubleSheet(
                troubleSheetRequest,
                instance
            )
        }
    }

    private var handleResultTrouble: Observer<Boolean> = Observer {
        if (it) {
            viewModel.trackEventToken()
            rxPreferences.setDesiredResponse(valueOfResponse)
            val bundle = arguments
            if (bundle != null) {
                if (bundle.containsKey(BUNDLE_KEY.TITLE_TROUBLE_SHEET)) {
                    appNavigation.openTroubleSheetToPartnerInfoScreen()
                    return@Observer
                }
            }

            if ((rxPreferences.getPoint() == 0)) {
                shareViewModel.isShowBuyPoint.value = true
                appNavigation.navigateUp()
            } else {
                appNavigation.openTroubleSheetToChatMessage(arguments)
            }
        }
    }

    private var handleResultMember: Observer<MemberResponse?> = Observer {
        it?.let { member ->
            member.troubleSheetResponse.let {
                if (!isLoadedTroubleSheet) {
                    setContentTrouble(it)
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
        if (isOpenBackPress()) {
            appNavigation.navigateUp()
        } else {
            CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                .showDialog()
                .setDialogTitle(R.string.confirm_save_trouble_sheet)
                .setTextPositiveButton(R.string.confirm_block_alert)
                .setTextNegativeButton(R.string.no_block_alert)
                .setOnPositivePressed {
                    it.dismiss()
                    findNavController().navigateUp()
                }.setOnNegativePressed {
                    it.dismiss()
                }
        }
    }
}
