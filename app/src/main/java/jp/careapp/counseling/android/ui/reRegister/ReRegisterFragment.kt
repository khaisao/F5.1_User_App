package jp.careapp.counseling.android.ui.reRegister

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.StringUtils.isValidEmail
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentReRegisterBinding
import javax.inject.Inject

@AndroidEntryPoint
class ReRegisterFragment : BaseFragment<FragmentReRegisterBinding, ReRegisterViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId = R.layout.fragment_re_register

    private val viewModel: ReRegisterViewModel by viewModels()

    override fun getVM(): ReRegisterViewModel = viewModel

    override fun initView() {
        super.initView()
        arguments?.let {
            if (it.containsKey(BUNDLE_KEY.EMAIL)) {
                val email = it.getString(BUNDLE_KEY.EMAIL)!!
                binding.etInputEmail.setText(email)
            }
        }
        changeStatusButton()
        handleInputEmail()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun handleInputEmail() {
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

    private fun validateEmailPattern(content: String) {
        if (content.isValidEmail()) {
            binding.tvErrorEmail.visibility = View.GONE
            binding.btReRegister.isEnabled = true
        } else {
            binding.tvErrorEmail.visibility =
                if (!TextUtils.isEmpty(content)) View.VISIBLE else View.GONE
            binding.btReRegister.isEnabled = false
        }
    }

    private fun changeStatusButton() {
        binding.btReRegister.isEnabled =
            !TextUtils.isEmpty(binding.etInputEmail.text.toString().trim())
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
        binding.btReRegister.setOnClickListener {
            viewModel.reRegister()
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.isLoading.observeForever(isLoadingObserver)
        viewModel.isSuccess.observeForever(isSuccessObserver)
    }

    private var isLoadingObserver: Observer<Boolean> = Observer {
        showHideLoading(it)
    }

    private var isSuccessObserver: Observer<Boolean> = Observer {
        if (it) {
            appNavigation.navController?.let { it1 ->
                it1.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.isSuccess.removeObservers(viewLifecycleOwner)
        viewModel.isLoading.removeObservers(viewLifecycleOwner)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}
