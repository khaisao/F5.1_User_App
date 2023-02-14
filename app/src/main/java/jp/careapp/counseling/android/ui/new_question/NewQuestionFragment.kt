package jp.careapp.counseling.android.ui.new_question

import android.os.Bundle
import android.text.TextUtils
import android.text.method.TextKeyListener
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.ConverterUtils
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.NewQuestionRequest
import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentNewQuestionBinding
import kotlinx.coroutines.flow.collect
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class NewQuestionFragment : BaseFragment<FragmentNewQuestionBinding, NewQuestionViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId = R.layout.fragment_new_question
    private val viewModel: NewQuestionViewModel by viewModels()
    override fun getVM(): NewQuestionViewModel = viewModel

    private val formatCharacter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale(","))
        val df = DecimalFormat()
        df.decimalFormatSymbols = symbols
        df.groupingSize = 3
        df
    }

    private var postQuestionEnable = false
    private var valueOfCategory = -1
    private var countContentCharacter = 0
    private var listCategory: ArrayList<CategoryResponse> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(false)
        }
        activity?.let {
            DeviceUtil.setupUI(
                view,
                mutableListOf(binding.titleNewQuestionEdt, binding.contentNewQuestionEdt),
                it
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(true)
        }
    }

    override fun onStop() {
        super.onStop()
        activity?.let { DeviceUtil.hideSoftKeyboard(it) }
    }

    override fun initView() {
        super.initView()
        rxPreferences.getListCategory()?.let {
            for (i in it) {
                if (i.registEnable) listCategory.add(i)
            }
        }
        handleBackStack()

        //set keyboard popup view
        ViewCompat.setOnApplyWindowInsetsListener(binding.chooseCategoryRl) { _, insets ->
            binding.chooseCategoryRl.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(
                    bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                )
            }
            insets
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        setToolBar()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.newQuestionEvent.collect { event ->
                when (event) {
                    is NewQuestionViewModel.NewQuestionEvent.SendNewQuestionResult -> {
                        event.data.let {
                            appNavigation.navController?.previousBackStackEntry?.savedStateHandle?.set(
                                BUNDLE_KEY.NEW_QUESTION_SUCCESS, true
                            )
                            appNavigation.navigateUp()
                            binding.contentNewQuestionEdt.setText("")
                            binding.titleNewQuestionEdt.setText("")
                            rxPreferences.setPoint(it.point)
                            binding.toolbar.setColorBtnRight(
                                R.color.color_6D5D9A,
                                requireActivity()
                            )
                        }
                    }
                }
            }
        }

        viewModel.countContentCharacter.observe(viewLifecycleOwner) {
            it?.let {
                countContentCharacter = it
                formatCharacter()
                checkEnableButton()
                showOrHideLimitContentTv()
            }
        }
    }

    private fun setToolBar() {
        binding.toolbar.apply {
            setColorTvTitle(R.color.color_text_E3DFEF, requireActivity())
            setColorBtnRight(R.color.color_6D5D9A, requireActivity())
        }
    }

    override fun setOnClick() {
        super.setOnClick()

        binding.rootLayout.setOnClickListener {
            DeviceUtil.hideKeyBoard(activity)
        }

        binding.titleNewQuestionEdt.addTextChangedListener {
            checkEmoji(it.toString(), binding.contentNewQuestionEdt.text.toString())
        }

        binding.contentNewQuestionEdt.addTextChangedListener {
            viewModel.countContentCharacter.value = it.toString().trim().replace("\\n+".toRegex(), "").length
            checkEmoji(binding.titleNewQuestionEdt.text.toString(), it.toString())
        }

        binding.chooseCategoryRl.setOnClickListener {
            if (!isDoubleClick) {
                val bundle = Bundle()
                bundle.putString(BUNDLE_KEY.TITLE, getString(R.string.category))
                bundle.putInt(BUNDLE_KEY.TYPE_SEARCH, Define.RESPONSE)
                bundle.putInt(BUNDLE_KEY.ITEM_SELECT, valueOfCategory)
                appNavigation.openNewQuestionToChooseTypeCategoryScreen(bundle)
            }
        }

        binding.toolbar.setOnToolBarClickListener(object : ToolBarCommon.OnToolBarClickListener() {
            override fun onClickLeft() {
                super.onClickLeft()
                if (!isDoubleClick)
                    findNavController().navigateUp()
            }

            override fun onClickRight() {
                super.onClickRight()
                if (!isDoubleClick && postQuestionEnable) {
                    showDialogConfirm()
                }
            }
        })

        binding.titleNewQuestionEdt.setOnKeyListener(null)
        binding.titleNewQuestionEdt.ellipsize = TextUtils.TruncateAt.END
        binding.titleNewQuestionEdt.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.titleNewQuestionEdt.ellipsize = null
                binding.titleNewQuestionEdt.keyListener = TextKeyListener(TextKeyListener.Capitalize.NONE, false)
            } else {
                binding.titleNewQuestionEdt.keyListener = null
                binding.titleNewQuestionEdt.ellipsize = TextUtils.TruncateAt.END
            }
        }
    }

    private fun showDialogConfirm() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.title_dialog_confirm_new_question)
            .setTextPositiveButton(R.string.positive_button_dialog_confirm_new_question)
            .setTextNegativeButton(R.string.negative_button_dialog_confirm_new_question)
            .setOnPositivePressed {
                val counseleeName: String = binding.titleNewQuestionEdt.text.toString().trim()
                val genre: Int = listCategory[valueOfCategory - 1].id
                val body: String = binding.contentNewQuestionEdt.text.toString().trim()
                val newQuestionRequest = NewQuestionRequest(counseleeName, genre, body)
                viewModel.sendNewQuestion(newQuestionRequest)
                it.dismiss()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private var handleBackStack: Observer<Int> = Observer { result ->
        if (result == -1) {
            binding.typeCategoryTv.text = getString(R.string.please_select)
            return@Observer
        }
        valueOfCategory = result
        binding.typeCategoryTv.text = listCategory[result - 1].name
        checkEnableButton()

        appNavigation.navController?.currentBackStackEntry?.savedStateHandle?.remove<Int>(
            BUNDLE_KEY.POSITION_TYPE_TROUBLE
        )
    }

    private fun handleBackStack() {
        appNavigation.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(
            BUNDLE_KEY.POSITION_TYPE_TROUBLE
        )?.observe(viewLifecycleOwner, handleBackStack)
    }

    private fun formatCharacter() {
        binding.countLimitCharTv.text = String.format(
            getString(R.string.format_limit_character_of_content),
            formatCharacter.format(Define.LIMIT_CHAR_CONTENT - countContentCharacter)
        )
        binding.countCharTv.text = String.format(
            getString(R.string.format_character_of_content),
            formatCharacter.format(Define.MAX_LENGTH_CONTENT_NEW_QUESTION - countContentCharacter)
        )
    }

    private fun checkEnableButton() {
        if (TextUtils.isEmpty(binding.contentNewQuestionEdt.text.toString().trim()) ||
            TextUtils.isEmpty(binding.titleNewQuestionEdt.text.toString().trim()) ||
            countContentCharacter < Define.LIMIT_CHAR_CONTENT ||
            valueOfCategory == Define.INVALID_VALUE_OF_CATEGORY ||
            countContentCharacter > Define.MAX_LENGTH_CONTENT_NEW_QUESTION
        ) {
            activeBtnPost(false)
        } else {
            binding.toolbar.setColorBtnRight(R.color.color_978DFF, requireActivity())
            activeBtnPost(true)
        }
    }

    private fun activeBtnPost(enable: Boolean) {
        postQuestionEnable = enable
        if (enable) {
            binding.toolbar.setColorBtnRight(R.color.color_978DFF, requireActivity())
        } else {
            binding.toolbar.setColorBtnRight(R.color.color_6D5D9A, requireActivity())
        }
    }

    private fun showOrHideLimitContentTv() {
        if (countContentCharacter < Define.LIMIT_CHAR_CONTENT) {
            binding.countLimitCharTv.visibility = View.VISIBLE
        } else {
            binding.countLimitCharTv.visibility = View.INVISIBLE
        }
    }

    private fun checkEmoji(title: String, content: String) {
        val isHasEmojiTitle =
            ConverterUtils.isContainEmoiji(title)
        val isHasEmojiContent =
            ConverterUtils.isContainEmoiji(content)
        if (isHasEmojiTitle || isHasEmojiContent) {
            binding.errorImojiTv.visibility = View.VISIBLE
            activeBtnPost(false)
        } else {
            binding.errorImojiTv.visibility = View.INVISIBLE
            checkEnableButton()
        }
    }
}