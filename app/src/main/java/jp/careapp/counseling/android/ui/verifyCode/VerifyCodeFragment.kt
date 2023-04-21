package jp.careapp.counseling.android.ui.verifyCode

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.databinding.FragmentVerifyCodeBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.email.InputAndEditMailViewModel
import jp.careapp.counseling.android.ui.main.OnBackPressedListener
import jp.careapp.counseling.android.ui.verifyCode.VerifyCodeViewModel.Companion.SCREEN_CODE_REGISTER
import jp.careapp.counseling.android.ui.verifyCode.VerifyCodeViewModel.Companion.SCREEN_CODE_TOP
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.utils.SignedUpStatus
import javax.inject.Inject

@AndroidEntryPoint
class VerifyCodeFragment :
    BaseFragment<FragmentVerifyCodeBinding, VerifyCodeViewModel>(),
    OnBackPressedListener {
    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId = R.layout.fragment_verify_code

    private val viewModel: VerifyCodeViewModel by viewModels()
    override fun getVM() = viewModel
    private val shareViewModel: ShareViewModel by activityViewModels()

    @Inject
    lateinit var rxPreferences: RxPreferences

    private var codeScreen = InputAndEditMailViewModel.SCREEN_LOGIN_WITH_EMAIL
    private var email = ""
    private var isFocusEditTextEmail = false
    private var isSaveStateVerifyCode = false

    override fun initView() {
        super.initView()
        if (requireArguments() != null) {
            if (requireArguments().containsKey(BUNDLE_KEY.CODE_SCREEN)) {
                codeScreen = requireArguments()?.getInt(BUNDLE_KEY.CODE_SCREEN)!!
                email = requireArguments()?.getString(BUNDLE_KEY.EMAIL)!!
                isFocusEditTextEmail =
                    requireArguments()?.getBoolean(BUNDLE_KEY.FOCUS_EDITTEXT_EMAIL)!!
            }
        }
        if (viewModel.getCountNumber() in 1..4) {
            showLabelError(!isSaveStateVerifyCode)
        }
        clearAllValueCode()
        setUpEditText()
        binding.tvNotReceive.text = HtmlCompat.fromHtml(
            getString(R.string.click_to_not_receive_email),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun handleShowOrHideKeyBoard(isEnable: Boolean) {
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(!isEnable)
        }
        if (isEnable) {
            activity?.let {
                view?.let { it1 ->
                    DeviceUtil.hideKeyBoardWhenClickOutSide(
                        it1,
                        arrayListOf(binding.tvNotReceive),
                        it
                    )
                }
            }
        }

        binding.clVerifyCode.setOnTouchListener { v, event ->
            if (event.action === MotionEvent.ACTION_DOWN) {
                binding.etFirstCode.clearFocus()
                DeviceUtil.hideSoftKeyboard(activity)
            }
            false
        }
    }

    private fun setUpInputCode() {
        changeStatusEditText(binding.etSecondCode, false)
        changeStatusEditText(binding.etThirdCode, false)
        changeStatusEditText(binding.etFourCode, false)
    }

    private fun changeStatusEditText(editText: EditText, isEnable: Boolean) {
        editText.isFocusable = isEnable
        editText.isEnabled = isEnable
        editText.isFocusableInTouchMode = isEnable
        editText.isCursorVisible = isEnable
    }

    private fun setUpEditText() {
        binding.etFirstCode.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    when (binding.etFirstCode.text.toString().trim().length) {
                        1 -> {
                            changeStatusEditText(binding.etSecondCode, true)
                            binding.etSecondCode.requestFocus()
                            changeStatusEditText(binding.etFirstCode, false)
                            showLabelError(false)
                        }
                        0 -> {
                            changeStatusEditText(binding.etFirstCode, true)
                            binding.etFirstCode.requestFocus()
                            changeStatusEditText(binding.etSecondCode, false)
                        }
                    }

                    changeStatusEditText(binding.etThirdCode, false)
                    changeStatusEditText(binding.etFourCode, false)
                }

                override fun afterTextChanged(s: Editable) {
                }
            }
        )
        binding.etSecondCode.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    when (binding.etSecondCode.text.toString().trim().length) {
                        1 -> {
                            changeStatusEditText(binding.etThirdCode, true)
                            binding.etThirdCode.requestFocus()
                            changeStatusEditText(binding.etSecondCode, false)
                        }
                        0 -> {
                            changeStatusEditText(binding.etSecondCode, true)
                            binding.etSecondCode.requestFocus()
                            changeStatusEditText(binding.etThirdCode, false)
                            changeStatusEditText(binding.etFourCode, false)
                        }
                    }
                }

                override fun afterTextChanged(s: Editable) {
                }
            }
        )
        binding.etThirdCode.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    when (binding.etThirdCode.text.toString().trim().length) {
                        1 -> {
                            changeStatusEditText(binding.etFourCode, true)
                            binding.etFourCode.requestFocus()
                            changeStatusEditText(binding.etThirdCode, false)
                        }
                        0 -> {
                            changeStatusEditText(binding.etThirdCode, true)
                            binding.etThirdCode.requestFocus()
                            changeStatusEditText(binding.etFourCode, false)
                        }
                    }
                }

                override fun afterTextChanged(s: Editable) {
                }
            }
        )
        binding.etFourCode.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    when (binding.etFourCode.text.toString().trim().length) {
                        1 -> {
                            binding.etFourCode.clearFocus()
                            DeviceUtil.hideSoftKeyboard(activity)
                        }
                        0 -> {
                            changeStatusEditText(binding.etThirdCode, true)
                            binding.etThirdCode.requestFocus()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    sendAuthCode()
                }
            }
        )

        binding.etFirstCode.setOnKeyListener(
            View.OnKeyListener { v: View?, keyCode: Int, event: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (binding.etFirstCode.text.toString().trim().isEmpty()) {
// TODO
                    }
                }
                false
            }
        )
        binding.etSecondCode.setOnKeyListener(
            View.OnKeyListener { v: View?, keyCode: Int, event: KeyEvent? ->
                if (event != null) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                        if (binding.etSecondCode.text.toString().trim().isEmpty()) {
                            binding.etFirstCode.setText("")
                        }
                    }
                }
                false
            }
        )
        binding.etThirdCode.setOnKeyListener(
            View.OnKeyListener { v: View?, keyCode: Int, event: KeyEvent? ->
                if (event != null) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                        if (binding.etThirdCode.text.toString().trim().isEmpty()) {
                            binding.etSecondCode.setText("")
                        }
                    }
                }
                false
            }
        )

        binding.etFourCode.setOnKeyListener(
            View.OnKeyListener { v: View?, keyCode: Int, event: KeyEvent? ->
                if (event != null) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                        if (binding.etFourCode.text.toString().trim().isEmpty()) {
                            binding.etThirdCode.setText("")
                        }
                    }
                }
                false
            }
        )

        binding.etFirstCode.setOnFocusChangeListener { view, b ->
            handleShowOrHideKeyBoard(view.isFocused)
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.ivCloseDialog.setOnClickListener {
            shareViewModel.setFocusEditText(isFocusEditTextEmail)
            appNavigation.navigateUp()
        }

        with(binding.tvNotReceive) {
            setOnClickListener {
                binding.tvNotReceive.setTextColor(resources.getColor(R.color.color_6D5D9A))
                val bundle = Bundle().apply {
                    putInt(BUNDLE_KEY.CODE_SCREEN, codeScreen)
                    putString(BUNDLE_KEY.EMAIL, email)
                    putBoolean(BUNDLE_KEY.FOCUS_EDITTEXT_EMAIL, isFocusEditTextEmail)
                    putBoolean(BUNDLE_KEY.FOCUS_EDITTEXT_VERIFY, binding.etFirstCode.isFocused)
                }
                appNavigation.openVerifyCodeToVerifyCodeHelpScreen(bundle)
            }
        }
    }

    fun sendAuthCode() {
        if (!TextUtils.isEmpty(binding.etFourCode.text.toString().trim())) {
            val authCode: String = binding.etFirstCode.text.toString().trim() +
                binding.etSecondCode.text.toString().trim() +
                binding.etThirdCode.text.toString().trim() +
                binding.etFourCode.text.toString().trim()

            if (codeScreen == InputAndEditMailViewModel.SCREEN_LOGIN_WITH_EMAIL || codeScreen == InputAndEditMailViewModel.SCREEN_REGISTER_WITH_EMAIL) {
                viewModel.sendVerifyCode(email, authCode)
            } else {
                viewModel.sendVerifyCodeWhenEditEmail(email, authCode)
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.codeScreenAfterVerify.observeForever(verifyCodeScreenObserver)
        viewModel.numberError.observeForever(numberErrorObserver)
        shareViewModel.isFocusVerifyCode.observe(viewLifecycleOwner, isFocusObserver)

        viewModel.mActionState.observe(viewLifecycleOwner) {
            when (it) {
                is VerifyCodeActionState.EditEmailSuccess -> showDialogEditEmailSuccess()
            }
        }
    }

    private var verifyCodeScreenObserver: Observer<Int> = Observer {
        when (it) {
            SCREEN_CODE_REGISTER -> {
                rxPreferences.setSignedUpStatus(SignedUpStatus.UNKNOWN)
                appNavigation.openVerifyCodeToRegistrationScreen()
            }
            SCREEN_CODE_TOP -> {
                appNavigation.openVerifyCodeToTopScreen()
                shareViewModel.setHaveToken(true)
            }
        }
    }

    private var numberErrorObserver: Observer<Int> = Observer {
        if (it > 0) {
            showLabelError(true)
            if (it < 5) {
                clearAllValueCode()
            } else {
                activity?.let { it1 ->
                    CommonAlertDialog.getInstanceCommonAlertdialog(it1)
                        .showDialog()
                        .setDialogTitleWithString(it1.getString(R.string.content_error_over_five_times))
                        .setTextOkButton(R.string.text_OK)
                        .setOnOkButtonPressed {
                            it.dismiss()
                            shareViewModel.setFocusEditText(isFocusEditTextEmail)
                            appNavigation.navigateUp()
                        }
                }
            }
        } else {
            clearAllValueCode()
        }
    }

    private fun showDialogEditEmailSuccess() {
        activity?.let { it1 ->
            CommonAlertDialog.getInstanceCommonAlertdialog(it1)
                .showDialog()
                .setDialogTitleWithString(it1.getString(R.string.updated_profile))
                .setTextOkButton(R.string.text_OK)
                .setOnOkButtonPressed {
                    it.dismiss()
                    appNavigation.navigateUp()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isSaveStateVerifyCode) {
            clearAllValueCode()
            isSaveStateVerifyCode = false
        }
        if (binding.etFirstCode.isFocused) {
            activity?.let { DeviceUtil.showKeyboardWithFocus(binding.etFirstCode, it) }
            setUpInputCode()
        }

        binding.etSecondCode.setOnFocusChangeListener { view, b ->
            if (view.isFocused) {
                activity?.let { DeviceUtil.showKeyboardWithFocus(binding.etSecondCode, it) }
            }
        }

        binding.etThirdCode.setOnFocusChangeListener { view, b ->
            if (view.isFocused) {
                activity?.let { DeviceUtil.showKeyboardWithFocus(binding.etThirdCode, it) }
            }
        }

        binding.etFourCode.setOnFocusChangeListener { view, b ->
            if (view.isFocused) {
                activity?.let { DeviceUtil.showKeyboardWithFocus(binding.etFourCode, it) }
            }
        }
    }

    private fun showLabelError(isShow: Boolean) {
        if (isShow) {
            binding.llErrorCode.visibility = View.VISIBLE
            binding.apply {
                etFirstCode.setBackgroundResource(R.drawable.bg_input_code_error)
                etSecondCode.setBackgroundResource(R.drawable.bg_input_code_error)
                etThirdCode.setBackgroundResource(R.drawable.bg_input_code_error)
                etFourCode.setBackgroundResource(R.drawable.bg_input_code_error)
            }
        } else {
            binding.llErrorCode.visibility = View.GONE
            binding.apply {
                etFirstCode.setBackgroundResource(R.drawable.bg_input_code)
                etSecondCode.setBackgroundResource(R.drawable.bg_input_code)
                etThirdCode.setBackgroundResource(R.drawable.bg_input_code)
                etFourCode.setBackgroundResource(R.drawable.bg_input_code)
            }
        }
    }

    private fun clearAllValueCode() {
        binding.etFirstCode.text.clear()
        binding.etSecondCode.text.clear()
        binding.etThirdCode.text.clear()
        binding.etFourCode.text.clear()
        binding.etFirstCode.clearFocus()
        binding.etSecondCode.clearFocus()
        binding.etThirdCode.clearFocus()
        binding.etFourCode.clearFocus()
        setUpInputCode()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.numberError.removeObservers(viewLifecycleOwner)
        viewModel.isLoading.removeObservers(viewLifecycleOwner)
        viewModel.codeScreenAfterVerify.removeObservers(viewLifecycleOwner)

        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(true)
        }
        activity?.let {
            DeviceUtil.hideKeyBoard(it)
        }
    }

    override fun onStop() {
        super.onStop()
        activity?.let {
            DeviceUtil.hideSoftKeyboard(it)
        }
    }

    private var isFocusObserver: Observer<Boolean> = Observer {
        if (it) {
            activity?.let { DeviceUtil.showKeyboardWithFocus(binding.etFirstCode, it) }
        } else {
            clearAllValueCode()
        }
    }

    override fun onBackPressed() {
        shareViewModel.setFocusEditText(isFocusEditTextEmail)
    }
}
