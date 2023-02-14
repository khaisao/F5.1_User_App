package jp.careapp.counseling.android.ui.registerName

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.TypeMember
import jp.careapp.counseling.databinding.FragmentRegisterNameBinding
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.edit_profile.EditProfileViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.MAX_LENGTH_NAME
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterNameFragment : BaseFragment<FragmentRegisterNameBinding, RegisterNameViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    override val layoutId = R.layout.fragment_register_name

    private val viewModel: RegisterNameViewModel by viewModels()
    private val editProfileViewModel: EditProfileViewModel by activityViewModels()

    override fun getVM(): RegisterNameViewModel = viewModel
    private var data: String? = ""
    private var action: String? = ""

    private var numberCharacterRemain: Int = 0
    private var receiveName: String = ""
    private var isFocusInputName = true
    private var isEnableEditName = false

    companion object {
        var fragmentCallBacks: FragmentCallBacks? = null

        fun setOnFragmentCallBack(fragmentCallBacks: FragmentCallBacks) {
            Companion.fragmentCallBacks = fragmentCallBacks
        }
    }

    override fun initView() {
        super.initView()
        requireArguments().let {
            if (it.containsKey(BUNDLE_KEY.NAME)) {
                receiveName = it.getString(BUNDLE_KEY.NAME).toString()
            }
        }
        binding.edtNameAccount.setText(receiveName)
        handleInputName()
        data = arguments?.getString("data")
        action = arguments?.getString("action")
        data?.let {
            receiveName = data.toString()
            binding.edtNameAccount.setText(it)
        }
        changeStatusButton()
    }

    private fun handleInputName() {
        binding.edtNameAccount.addTextChangedListener(
            object : TextWatcher {
                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    numberCharacterRemain = MAX_LENGTH_NAME - getEditTextName().length
                    binding.tvCount.text = numberCharacterRemain.toString()
                    changeStatusButton()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editProfileViewModel.error.observe(
            viewLifecycleOwner,
            Observer {
                // TODO
            }
        )
    }

    private fun changeStatusButton() {
        if (getEditTextName().isEmpty()) {
            binding.tvCount.text =
                context?.resources?.getString(R.string.limit_number_character).toString()
            if (receiveName.isNotEmpty()) {
                binding.tvPleaseInputName.visibility = View.VISIBLE
            }
            disableButton()
        } else {
            if (getEditTextName().toString() == receiveName) {
                disableButton()
            } else {
                enableButton()
            }
            numberCharacterRemain = MAX_LENGTH_NAME - getEditTextName().length
            binding.tvCount.text = numberCharacterRemain.toString()
            binding.tvPleaseInputName.visibility = View.GONE
        }
    }

    private fun getEditTextName(): Editable {
        return binding.edtNameAccount.text
    }

    private fun disableButton() {
        binding.btnRegister.isEnabled = false
        isEnableEditName = false
        handleShowOrHideKeyBoard(false)
    }

    private fun enableButton() {
        binding.btnRegister.isEnabled = true
        isEnableEditName = true
        handleShowOrHideKeyBoard(true)
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.toolBar.setOnToolBarClickListener(
            object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
                    appNavigation.navigateUp()
                }
            }
        )
        binding.btnRegister.setOnClickListener {
            if (isDoubleClick) {
                return@setOnClickListener
            }

            if (getEditTextName().trim().isNotEmpty()) {
                if (action == TypeMember.EDIT.toString()) {
                    data?.let { it1 ->
                        editProfileViewModel.setParams(
                            name = binding.edtNameAccount.text.toString()
                        )
                        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                            .showDialog()
                            .setDialogTitle(R.string.updated_profile)
                            .setTextPositiveButton(R.string.text_OK)
                            .setOnPositivePressed {
                                it.dismiss()
                                findNavController().navigateUp()
                            }
                    }
                } else {
                    fragmentCallBacks.let { it!!.onCallBack(binding.edtNameAccount.text.toString()) }
                    appNavigation.navigateUp()
                }
            } else {
                if (binding.edtNameAccount.isFocused) {
                    isFocusInputName = true
                }
                binding.edtNameAccount.clearFocus()
                DeviceUtil.hideSoftKeyboard(activity)
                CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                    .showDialog()
                    .setDialogTitle(R.string.your_name_not_enter)
                    .setTextPositiveButton(R.string.ok)
                    .setOnPositivePressed {
                        it.dismiss()
                        if (isFocusInputName) {
                            binding.edtNameAccount.requestFocus()
                            binding.edtNameAccount.setSelection(10 - numberCharacterRemain)
                            DeviceUtil.showSoftKeyboard(activity)
                        }
                    }
            }
        }
    }

    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val r = Rect()
        binding.rlRegisterName.getWindowVisibleDisplayFrame(r)
        val screenHeight = binding.rlRegisterName.rootView.height
        val keypadHeight = screenHeight - r.bottom
        if (keypadHeight > screenHeight * 0.15) {
            binding.edtNameAccount.requestFocus()
        } else {
            binding.edtNameAccount.clearFocus()
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
                        binding.btnRegister,
                        it
                    )
                }
            }
            binding.rlRegisterName.setOnTouchListener { v, event ->
                if (event.action === MotionEvent.ACTION_DOWN) {
                    isFocusInputName = false
                    binding.edtNameAccount.clearFocus()
                    DeviceUtil.hideSoftKeyboard(activity)
                    viewModel.setComebackFromBackGround(false)
                }
                false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.rlRegisterName.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
    }

    override fun onResume() {
        super.onResume()
        viewModel.isComeBackFromBackGround.observe(viewLifecycleOwner, isFocusObserver)
    }

    private var isFocusObserver: Observer<Boolean> = Observer {
        if (it) {
            if (binding.edtNameAccount.isFocused) {
                isFocusInputName = true
                binding.edtNameAccount.requestFocus()
                activity?.let { DeviceUtil.showKeyboardWithFocus(binding.edtNameAccount, it) }
                handleShowOrHideKeyBoard(isEnableEditName)
            } else {
                activity?.let { DeviceUtil.hideKeyBoard(it) }
            }

            binding.edtNameAccount.setOnFocusChangeListener { view, b ->
                if (view.isFocused && binding.edtNameAccount.text.toString()
                    .isNotEmpty()
                ) {
                    binding.edtNameAccount.requestFocus()
                    isFocusInputName = true
                    handleShowOrHideKeyBoard(isEnableEditName)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.setComebackFromBackGround(true)
    }

    override fun onDestroyView() {
        viewModel.setComebackFromBackGround(false)
        super.onDestroyView()
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(true)
        }
    }

    override fun onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            binding.rlRegisterName.viewTreeObserver
                .removeOnGlobalLayoutListener(keyboardLayoutListener)
        } else {
            binding.rlRegisterName.viewTreeObserver
                .removeGlobalOnLayoutListener(keyboardLayoutListener)
        }
        super.onStop()
        activity?.let {
            DeviceUtil.hideSoftKeyboard(it)
        }
    }

    interface FragmentCallBacks {
        fun onCallBack(data: String?)
    }
}
