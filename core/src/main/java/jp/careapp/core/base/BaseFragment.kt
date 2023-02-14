package jp.careapp.core.base

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import jp.careapp.core.R
import jp.careapp.core.utils.dialog.CommonAlertDialog

abstract class BaseFragment<BD : ViewDataBinding, VM : BaseViewModel> : Fragment() {
    private var _binding: BD? = null
    protected val binding: BD
        get() = _binding
            ?: throw IllegalStateException("Cannot access view after view destroyed or before view creation")

    private lateinit var viewModel: VM

    @get: LayoutRes
    abstract val layoutId: Int
    abstract fun getVM(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getVM()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        _binding?.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setActivityViewModel(requireActivity())
        initView()

        setOnClick()

        bindingStateView()

        bindingAction()
    }

    open fun setOnClick() {
    }

    open fun initView() {
        with(viewModel) {

            messageError.observe(viewLifecycleOwner) {
                var message = ""
                if (it is String) {
                    message = it
                } else {
                    if (it is Int) {
                        try {
                            message = getString(it)
                        } catch (e: Exception) {
                            // do nothing
                        }
                    }
                }
                if (!TextUtils.isEmpty(message)) {
                    showAlertDialog(message)
                }
            }

            isLoading.observe(viewLifecycleOwner) {
                showHideLoading(it)
            }
        }
    }

    open fun bindingStateView() {
    }

    open fun bindingAction() {
    }

    protected val isDoubleClick: Boolean
        get() {
            if (activity == null) {
                return false
            }
            return if (activity is BaseActivity<*, *>) {
                (activity as BaseActivity<*, *>?)!!.isDoubleClick
            } else false
        }

    override fun onDestroyView() {
        _binding?.unbind()
        _binding = null
        super.onDestroyView()
    }

    fun showHideLoading(isShow: Boolean) {
        if (activity != null && activity is BaseActivity<*, *>) {
            if (isShow) {
                (activity as BaseActivity<*, *>?)!!.showLoading()
            } else {
                (activity as BaseActivity<*, *>?)!!.hiddenLoading()
            }
        }
    }

    fun showAlertDialog(message: String, onPositivePressed: ((CommonAlertDialog) -> Unit)? = null) {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setContent(message)
            .setTextPositiveButton(R.string.ok)
            .setOnPositivePressed {
                onPositivePressed?.invoke(it)
                it.dismiss()
            }
    }

    fun showAlertDialog(@StringRes message: Int) {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setContent(message)
            .setTextPositiveButton(R.string.ok)
    }

    fun showAlertDialog(@StringRes message: Int, @StringRes textConfirm: Int) {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(message)
            .setTextPositiveButton(textConfirm)
    }

    fun showConfirmDialog(
        @StringRes message: Int,
        onPositive: ((CommonAlertDialog) -> Unit)? = null,
        onNegative: ((CommonAlertDialog) -> Unit)? = null,
    ) {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setContent(message)
            .setTextPositiveButton(R.string.ok)
            .setTextNegativeButton(R.string.cancel)
            .setOnPositivePressed {
                onPositive?.invoke(it)
                it.dismiss()
            }
            .setOnNegativePressed {
                onNegative?.invoke(it)
                it.dismiss()
            }
    }

    fun bindingIsNull(): Boolean {
        return _binding == null
    }
}
