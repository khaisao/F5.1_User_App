package jp.careapp.counseling.android.ui.searchCategory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.model.user_profile.PerformerSearch
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define.SearchCondition
import jp.careapp.counseling.android.utils.customView.ToolbarSearch
import jp.careapp.counseling.android.utils.extensions.updateTextStyleChecked
import jp.careapp.counseling.databinding.FragmentSearchBinding
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {
    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    private val listCategory: List<CategoryResponse>? by lazy {
        val listCategoryResponse = rxPreferences.getListCategory()
        val temp = mutableListOf<CategoryResponse>()
        listCategoryResponse?.let {
            for (i in it) {
                if (i.registEnable) temp.add(i)
            }
        }
        temp
    }

    override val layoutId = R.layout.fragment_search
    private val viewModel: SearchViewModel by viewModels()
    override fun getVM(): SearchViewModel = viewModel
    private val shareViewModel: ShareViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appNavigation.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<IntArray>(
            BUNDLE_KEY.GENRES_SELECTED
        )?.observe(viewLifecycleOwner, handleBackStackEntry)
    }

    private val handleBackStackEntry: Observer<IntArray> = Observer { data ->
        for (i in 0 until binding.genresChipGroup.childCount) {
            (binding.genresChipGroup.getChildAt(i) as Chip).isChecked = false
            if (i in data) {
                (binding.genresChipGroup.getChildAt(i) as Chip).isChecked = true
            }
        }
    }

    override fun initView() {
        super.initView()
        shareViewModel.getListPerformerSearch()?.let {
            viewModel.setListConsultantResult(it)
        }
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
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun setOnClick() {
        super.setOnClick()

        // ChipGroup multiple choice listener
        for (i in 0 until binding.genresChipGroup.childCount) {
            val chip: Chip = binding.genresChipGroup.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateGenresChecked(i, isChecked)
                binding.genresChipGroup.updateTextStyleChecked()
            }
        }

        binding.genderChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.genderMaleChip.id -> viewModel.setGenderChecked(SearchCondition.MALE)
                binding.genderFemaleChip.id -> viewModel.setGenderChecked(SearchCondition.FEMALE)
                else -> viewModel.setGenderChecked(SearchCondition.DEFAULT)
            }
            binding.genderChipGroup.updateTextStyleChecked()
        }

        // ChipGroup multiple choice listener
        for (i in 0 until binding.rankingChipGroup.childCount) {
            val chip: Chip = binding.rankingChipGroup.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updateRankingChecked(i, isChecked)
                binding.rankingChipGroup.updateTextStyleChecked()
            }
        }

        binding.reviewChipGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.hasReviewChip.id) viewModel.setHaveNumberReview(SearchCondition.HAVE_REVIEW)
            else viewModel.setHaveNumberReview(SearchCondition.DEFAULT)
            if (checkedId == binding.threeOrMoreChip.id || checkedId == binding.fourOrMoreChip.id ) {
                if (checkedId == binding.threeOrMoreChip.id) viewModel.setReviewAverageChecked(SearchCondition.THREE_OR_MORE)
                else viewModel.setReviewAverageChecked(SearchCondition.FOUR_OR_MORE)
            } else viewModel.setReviewAverageChecked(SearchCondition.DEFAULT)
            binding.reviewChipGroup.updateTextStyleChecked()
        }

        binding.statusChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.statusChip.id -> viewModel.setStatusConsultant(SearchCondition.ONLY_DURING_RECEPTION)
                else -> viewModel.setStatusConsultant(SearchCondition.DEFAULT)
            }
            binding.statusChipGroup.updateTextStyleChecked()
        }

        binding.btnSearch.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY.TITLE, getTitle())
            bundle.putIntArray(
                BUNDLE_KEY.GENRES_SELECTED,
                viewModel.listGenresSelectedPosition.toIntArray()
            )
            val dataPerformer = PerformerSearch(viewModel.getStatusConsultant(), viewModel.getNameConsultant(), viewModel.getHaveNumberReview(),
                viewModel.getGenderChecked(), viewModel.getListGenresSelected(), viewModel.getListRankingSelected(), viewModel.getReviewAverageChecked())
            bundle.putSerializable(BUNDLE_KEY.DATA_PERFORMER, dataPerformer)
            appNavigation.openSearchToSearchResultScreen(bundle)
        }

        binding.toolBar.setToolBarSearchListener(object : ToolbarSearch.ToolBarSearchListener() {
            override fun clickLeftBtn() {
                super.clickLeftBtn()
                appNavigation.navigateUp()
            }

            override fun clickRightBtn() {
                super.clickRightBtn()
                binding.toolBar.etSearch.setText("")
                binding.toolBar.etSearch.clearFocus()
            }
        })

        binding.toolBar.etSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.setNameConsultant(s.trim().toString())
            }
        })

        binding.edtInputName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.setNameConsultant(s.trim().toString())
            }
        })

        binding.ivClear.setOnClickListener {
            binding.edtInputName.text?.clear()
        }

        binding.tvBack.setOnClickListener {
            appNavigation.navigateUp()
        }
    }

    private fun getTitle(): String {
        var title: StringBuilder = StringBuilder()
        if (binding.edtInputName.text.toString().isNotEmpty()) {
            title.append(binding.edtInputName.text.toString())
                .append("、 ")
        }
//        if (binding.toolBar.etSearch.text.toString().isNotEmpty()) {
//            title.append(binding.toolBar.etSearch.text.toString())
//                .append("、 ")
//        }
        title = appendConditionChecked(title)
        if (title.trim().toString().isNotEmpty()) {
            return title.trim().toString().trim().substring(0, title.trim().toString().length - 1)
        }
        return ""
    }

    private fun appendConditionChecked(title: StringBuilder): StringBuilder {
        for (i in 0 until binding.genresChipGroup.childCount) {
            val chip: Chip = binding.genresChipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                title.append(chip.text).append("、 ")
            }
        }
        for (i in 0 until binding.genderChipGroup.childCount) {
            val chip: Chip = binding.genderChipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                title.append(chip.text).append("、 ")
            }
        }
        for (i in 0 until binding.rankingChipGroup.childCount) {
            val chip: Chip = binding.rankingChipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                title.append(chip.text).append("、 ")
            }
        }
        for (i in 0 until binding.reviewChipGroup.childCount) {
            val chip: Chip = binding.reviewChipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                title.append(chip.text).append("、 ")
            }
        }
        for (i in 0 until binding.statusChipGroup.childCount) {
            val chip: Chip = binding.statusChipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                title.append(chip.text).append("、 ")
            }
        }
        return title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}