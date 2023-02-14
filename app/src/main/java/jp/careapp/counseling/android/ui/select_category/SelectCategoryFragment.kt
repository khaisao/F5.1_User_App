package jp.careapp.counseling.android.ui.select_category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.SearchCategoryItem
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.android.utils.extensions.updateTextStyleChecked
import jp.careapp.counseling.databinding.FragmentSelectCategoryBinding
import javax.inject.Inject

@AndroidEntryPoint
class SelectCategoryFragment :
    BaseFragment<FragmentSelectCategoryBinding, SelectCategoryViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId = R.layout.fragment_select_category

    private val viewModel: SelectCategoryViewModel by viewModels()

    override fun getVM(): SelectCategoryViewModel = viewModel

    private var valueOfResponse = 0
    private var genreIdSelected: Int = -1
    private var oldGenreId: Int = -1

    private val listCategory: MutableList<SearchCategoryItem> by lazy {
        val listCategoryResponse = rxPreferences.getListCategory()
        val temp = mutableListOf<SearchCategoryItem>()
        listCategoryResponse?.let {
            for (i in it) {
                if (i.registEnable) temp.add(SearchCategoryItem(i.name, false, i.id))
            }
        }
        temp
    }
    private var chooseItem = false

    private val handleBackStackEntry: Observer<Int> = Observer { data ->
        if (data != -1) {
            for (i in 0 until binding.genresChipGroup.childCount) {
                if (listCategory[i].id == data) {
                    val oldChip: Chip = binding.genresChipGroup.getChildAt(oldGenreId) as Chip
                    oldChip.isChecked = true
                    genreIdSelected = data
                    oldGenreId = i
                    checkEnableButton()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appNavigation.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(
            BUNDLE_KEY.ITEM_SELECT
        )?.observe(viewLifecycleOwner, handleBackStackEntry)
    }

    override fun initView() {
        super.initView()

        listCategory?.let {
            for (i in listCategory!!.indices) {
                val chip = layoutInflater.inflate(
                    R.layout.single_chip_layout,
                    binding.genresChipGroup,
                    false
                ) as Chip
                binding.genresChipGroup.addView(chip!!.apply {
                    text = listCategory!![i].name
                    isChecked = false
                }, i)
            }
        }

        handleBackStackEntry()
        setListener()
    }

    private val handleBackStack: Observer<Int> = Observer { result ->
        activeBtnNext(chooseItem)
    }

    private fun handleBackStackEntry() {
        appNavigation.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(
            BUNDLE_KEY.ITEM_SELECT
        )?.observe(viewLifecycleOwner, handleBackStack)
    }

    private fun checkEnableButton() {
        if (valueOfResponse == 0 || genreIdSelected == -1) {
            activeBtnNext(false)
            chooseItem = false
        } else {
            activeBtnNext(true)
            chooseItem = true
        }
    }

    private fun activeBtnNext(enable: Boolean) {
        if (enable) {
            binding.nextBtn.setBackgroundResource(R.drawable.bg_corner_26dp_978dff_alpha)
            binding.nextTv.setTextColor(resources.getColor(R.color.white, requireActivity().theme))
        } else {
            binding.nextBtn.setBackgroundResource(R.drawable.bg_disable_button)
            binding.nextTv.setTextColor(
                resources.getColor(
                    R.color.color_6D5D9A,
                    requireActivity().theme
                )
            )
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.nextBtn.setOnClickListener {
            if (!isDoubleClick && chooseItem) {
                val bundle = Bundle()
                bundle.putString(
                    BUNDLE_KEY.TITLE_TROUBLE_SHEET,
                    getString(R.string.trouble_title_1)
                )
                bundle.putInt(BUNDLE_KEY.ITEM_SELECT, genreIdSelected)
                bundle.putInt(BUNDLE_KEY.DESIRED_RESPONSE, valueOfResponse)
                bundle.putInt(BUNDLE_KEY.PARAM_GENRE, genreIdSelected)
                arguments?.let {
                    bundle.putBoolean(
                        BUNDLE_KEY.PARAM_LOGIN_WITH_EMAIL,
                        it.getBoolean(BUNDLE_KEY.PARAM_LOGIN_WITH_EMAIL)
                    )
                    bundle.putSerializable(
                        BUNDLE_KEY.PARAM_REGISTRATION,
                        it.getSerializable(BUNDLE_KEY.PARAM_REGISTRATION)
                    )
                }
                appNavigation.openSelectCategoryToTroubleSheet(bundle)
            }
        }

        binding.toolbar.setOnToolBarClickListener(
            object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    super.onClickLeft()
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

        // ChipGroup choice listener
        for (i in 0 until binding.genresChipGroup.childCount) {
            val chip: Chip = binding.genresChipGroup.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) genreIdSelected = listCategory[i].id
                if (oldGenreId != -1 && i != oldGenreId) {
                    val oldChip: Chip = binding.genresChipGroup.getChildAt(oldGenreId) as Chip
                    oldChip.isChecked = false
                }
                if (oldGenreId != -1 && i == oldGenreId && listCategory[i].id == genreIdSelected) {
                    genreIdSelected = -1
                    oldGenreId = -1
                } else oldGenreId = i

                binding.genresChipGroup.updateTextStyleChecked()
                checkEnableButton()
            }
        }

    }

    private fun setListener() {
        binding.chooseAnswerChipGroup.setOnCheckedChangeListener { _, checkedId ->
            valueOfResponse = when (checkedId) {
                binding.answerAdviceChip.id -> 2
                binding.answerFeelingChip.id -> 3
                binding.answerSortFeelingChip.id -> 4
                binding.answerOther.id -> 5
                else -> 0
            }
            checkEnableButton()
            binding.chooseAnswerChipGroup.updateTextStyleChecked()
        }
    }

    private fun isOpenBackPress(): Boolean {
        return !(valueOfResponse != 0 || genreIdSelected != -1)
    }
}