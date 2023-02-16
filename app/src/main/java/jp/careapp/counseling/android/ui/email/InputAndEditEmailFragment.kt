package jp.careapp.counseling.android.ui.email

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.StringUtils.isValidEmail
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.email.InputAndEditMailViewModel.Companion.SCREEN_EDIT_EMAIL
import jp.careapp.counseling.android.ui.email.InputAndEditMailViewModel.Companion.SCREEN_LOGIN_WITH_EMAIL
import jp.careapp.counseling.android.ui.main.MainViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.SignedUpStatus
import jp.careapp.counseling.databinding.FragmentInputAndEditEmailBinding
import javax.inject.Inject

@AndroidEntryPoint
class InputAndEditEmailFragment :
    BaseFragment<FragmentInputAndEditEmailBinding, InputAndEditMailViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId = R.layout.fragment_input_and_edit_email

    private val viewModel: InputAndEditMailViewModel by viewModels()

    override fun getVM(): InputAndEditMailViewModel = viewModel
    private val shareViewModel: ShareViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var codeScreen = SCREEN_LOGIN_WITH_EMAIL
    private var email: String = ""
    private var isFocusEditEmail = false
    private var isEnableEditName = false

    override fun initView() {
        super.initView()
        try {
            if (arguments != null) {
                codeScreen = arguments?.getInt(BUNDLE_KEY.CODE_SCREEN) ?: SCREEN_LOGIN_WITH_EMAIL
                email = if (rxPreferences.getSignedUpStatus() == SignedUpStatus.LOGIN_WITHOUT_EMAIL) {
                    ""
                } else {
                    arguments?.getString(BUNDLE_KEY.EMAIL) ?: ""
                }
                binding.etInputEmail.setText(email)
            }
        } catch (e: Exception) {
        }
        changeStatusButton()
        handleInputName()
        openWebPrivacy()
        displayView()
    }

    private fun displayView() {
        binding.toolBar.viewStatusBar.visibility = GONE
        if (codeScreen == SCREEN_EDIT_EMAIL) {
            binding.tvTitle.visibility = GONE
            binding.toolBar.btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_back_left))
            binding.toolBar.tvTitle.text = getString(R.string.sub_title_edt_reregister)
            binding.tvErrorEmail.text = getString(R.string.please_enter_valid_email)
            binding.etInputEmail.hint = getString(R.string.hint_edit_email)
            binding.btnEmail.text = getString(R.string.edit_email)
        } else {
            binding.tvTitle.visibility = VISIBLE
            binding.toolBar.btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_cancel))
            binding.tvErrorEmail.text = getString(R.string.alert_error_email)
            binding.etInputEmail.hint = getString(R.string.hint_input_email)
            binding.btnEmail.text = getString(R.string.input_email)
        }
    }

    private fun handleInputName() {
        binding.etInputEmail.addTextChangedListener(
            object : TextWatcher {
                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    validateEmailPattern(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {
                }
            }
        )
    }

    private fun changeStatusButton() {
        if (codeScreen == SCREEN_LOGIN_WITH_EMAIL) {
            if (TextUtils.isEmpty(getEmail())) {
                disableButton()
            } else {
                enableButton()
            }
        } else {
            disableButton()
        }
    }

    private fun enableButton() {
        binding.btnEmail.isEnabled = true
        isEnableEditName = true
        handleShowOrHideKeyBoard(true)
    }

    private fun disableButton() {
        binding.btnEmail.isEnabled = false
        isEnableEditName = false
        handleShowOrHideKeyBoard(false)
    }

    private fun validateEmailPattern(content: String) {
        if (content.isValidEmail()) {
            binding.tvErrorEmail.visibility = GONE
            if (codeScreen == SCREEN_EDIT_EMAIL) {
                if (content == email) {
                    disableButton()
                } else {
                    enableButton()
                }
            } else {
                enableButton()
            }
        } else {
            binding.tvErrorEmail.visibility =
                if (!TextUtils.isEmpty(content)) VISIBLE else GONE
            disableButton()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.rlInputEmail.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
    }
    override fun onResume() {
        super.onResume()
        viewModel.isComeBackFromBackGround.observe(viewLifecycleOwner, isFocusObserver)

        binding.rlInputEmail.setOnTouchListener { v, event ->
            if (event.action === MotionEvent.ACTION_DOWN) {
                isFocusEditEmail = false
                binding.etInputEmail.clearFocus()
                DeviceUtil.hideSoftKeyboard(activity)
            }
            false
        }

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.toolBar.btnLeft.setOnClickListener {
            appNavigation.navigateUp()
        }

        binding.btnEmail.setOnClickListener {
            if (!isDoubleClick) {
                if (codeScreen == SCREEN_EDIT_EMAIL) {
                    viewModel.editEmail(getEmail())
                } else {
                    viewModel.sendEmail(getEmail())
                    mainViewModel.setMail(getEmail())
                }
            }
        }
    }

    private fun getEmail(): String {
        return binding.etInputEmail.text.toString().trim()
    }

    private fun openWebPrivacy() {
        binding.linkPrivacy.setOnClickListener {
            if (!isDoubleClick) {
                val bundle = Bundle().apply {
                    putString(Define.TITLE_WEB_VIEW, getString(R.string.PrivacyPolicy))
                    putString(Define.URL_WEB_VIEW, Define.URL_PRIVACY)
                }
                appNavigation.openScreenToWebview(bundle)
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.isLoading.observeForever(isLoadingObserver)
        viewModel.isSuccess.observeForever(isSuccessObserver)
        viewModel.isComeBackFromBackGround.observe(viewLifecycleOwner, isFocusObserver)
    }

    private var isLoadingObserver: Observer<Boolean> = Observer {
        showHideLoading(it)
    }

    private var isSuccessObserver: Observer<Boolean> = Observer {
        if (it) {
            val bundle = Bundle().apply {
                putInt(BUNDLE_KEY.CODE_SCREEN, codeScreen)
                putString(BUNDLE_KEY.EMAIL, getEmail())
                putBoolean(BUNDLE_KEY.FOCUS_EDITTEXT_EMAIL, isFocusEditEmail)
            }
            appNavigation.openInputEmailToVerifyCode(bundle)
            shareViewModel.setFocusVerifyCode(false)
        }
    }

    private var isFocusObserver: Observer<Boolean> = Observer {
        if (it) {
            if (binding.etInputEmail.isFocused) {
                isFocusEditEmail = true
                binding.etInputEmail.requestFocus()
                activity?.let { DeviceUtil.showKeyboardWithFocus(binding.etInputEmail, it) }
                handleShowOrHideKeyBoard(true)
            } else {
                activity?.let { DeviceUtil.hideKeyBoard(it) }
            }
        }
        binding.etInputEmail.setOnFocusChangeListener { view, b ->
            if (view.isFocused && binding.etInputEmail.text.toString()
                .isNotEmpty()
            ) {
                binding.etInputEmail.requestFocus()
                isFocusEditEmail = true
                activity?.let { DeviceUtil.showKeyboardWithFocus(binding.etInputEmail, it) }
                handleShowOrHideKeyBoard(isEnableEditName)
            } else {
                isFocusEditEmail = false
            }
        }
    }

    private val keyboardLayoutListener = OnGlobalLayoutListener {
        val r = Rect()
        binding.rlInputEmail.getWindowVisibleDisplayFrame(r)
        val screenHeight = binding.rlInputEmail.rootView.height
        val keypadHeight = screenHeight - r.bottom
        if (keypadHeight > screenHeight * 0.15) {
            binding.etInputEmail.requestFocus()
        } else {
            binding.etInputEmail.clearFocus()
        }
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
                        binding.btnEmail,
                        it
                    )
                }
            }

            binding.rlInputEmail.setOnTouchListener { v, event ->
                if (event.action === MotionEvent.ACTION_DOWN) {
                    isFocusEditEmail = false
                    binding.etInputEmail.clearFocus()
                    DeviceUtil.hideSoftKeyboard(activity)
                    viewModel.setComebackFromBackGround(false)
                }
                false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.setComebackFromBackGround(true)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun onDestroyView() {
        activity?.let {
            DeviceUtil.hideKeyBoard(it)
        }
        viewModel.setComebackFromBackGround(false)
        super.onDestroyView()
        viewModel.isSuccess.removeObservers(viewLifecycleOwner)
        viewModel.isLoading.removeObservers(viewLifecycleOwner)
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(true)
        }
    }

    override fun onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            binding.rlInputEmail.viewTreeObserver
                .removeOnGlobalLayoutListener(keyboardLayoutListener)
        } else {
            binding.rlInputEmail.viewTreeObserver
                .removeGlobalOnLayoutListener(keyboardLayoutListener)
        }
        super.onStop()
        activity?.let {
            DeviceUtil.hideSoftKeyboard(it)
        }
    }
}