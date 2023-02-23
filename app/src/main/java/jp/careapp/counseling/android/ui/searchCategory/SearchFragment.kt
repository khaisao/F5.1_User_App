package jp.careapp.counseling.android.ui.searchCategory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.ConsultantAdapter
import jp.careapp.counseling.android.data.network.CategoryResponse
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.model.user_profile.PerformerSearch
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define.SearchCondition
import jp.careapp.counseling.android.utils.GenresUtil
import jp.careapp.counseling.android.utils.customView.ToolbarSearch
import jp.careapp.counseling.android.utils.extensions.updateTextStyleChecked
import jp.careapp.counseling.databinding.FragmentSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
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
    private val searchResultViewModel: SearchResultViewModel by viewModels()
    override fun getVM(): SearchViewModel = viewModel
    private val shareViewModel: ShareViewModel by activityViewModels()

    private var mConsultantAdapter: ConsultantAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appNavigation.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<IntArray>(
            BUNDLE_KEY.GENRES_SELECTED
        )?.observe(viewLifecycleOwner, handleBackStackEntry)
    }

    private val handleBackStackEntry: Observer<IntArray> = Observer { data ->
//        for (i in 0 until binding.genresChipGroup.childCount) {
//            (binding.genresChipGroup.getChildAt(i) as Chip).isChecked = false
//            if (i in data) {
//                (binding.genresChipGroup.getChildAt(i) as Chip).isChecked = true
//            }
//        }
    }

    override fun initView() {
        super.initView()
        shareViewModel.getListPerformerSearch()?.let {
            viewModel.setListConsultantResult(it)
        }
//        listCategory?.let {
//            for (i in listCategory!!.indices) {
//                val chip = layoutInflater.inflate(
//                    R.layout.single_chip_layout,
//                    binding.genresChipGroup,
//                    false
//                ) as Chip
//                binding.genresChipGroup.addView(chip!!.apply {
//                    text = listCategory!![i].name
//                    isChecked = false
//                }, i)
//            }
//        }
        DeviceUtil.showKeyboardWithFocus(binding.edtInputName,requireActivity())
        initRecyclerView()

        binding.swipeRefreshLayout.setOnRefreshListener {
            showHideLoading(true)

            if (!binding.progressBar.isVisible) {
                searchResultViewModel.getListBlockedConsultant()
                searchResultViewModel.isShowHideLoading.value = true
//                    viewModel.getListBlockedConsultant()
                }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.rvConsultantSearch.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val listDataSize = searchResultViewModel.listConsultantTemp.value?.size ?: 0
                if (listDataSize > 0) {
                    val layoutManager = binding.rvConsultantSearch.layoutManager as LinearLayoutManager
                    if (!searchResultViewModel.isLoadMoreData) {
                        if (layoutManager != null && (layoutManager.findLastCompletelyVisibleItemPosition() == listDataSize - 1) && searchResultViewModel.isCanLoadMoreData()) {
                            searchResultViewModel.isLoadMoreData = true
                            searchResultViewModel.loadMoreData()
                        }
                    }
                }
            }
        })
    }

    override fun bindingStateView() {
        super.bindingStateView()
        searchResultViewModel.dataPerformer.observe(viewLifecycleOwner) {
                searchResultViewModel.getListBlockedConsultant()
        }

        searchResultViewModel.isShowHideLoading.observe(viewLifecycleOwner){
            showHideLoading(it)
        }

        searchResultViewModel.listConsultantTemp.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.llNoResult.visibility = View.GONE
                binding.rvConsultantSearch.visibility=View.VISIBLE
                mConsultantAdapter!!.submitList(it)
            } else {
                binding.llNoResult.visibility = View.VISIBLE
                binding.rvConsultantSearch.visibility=View.GONE
            }
        }

        searchResultViewModel.isLoading.observe(viewLifecycleOwner, isLoadingObserver)

    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private var isLoadingObserver: Observer<Boolean> = Observer {
        showHideLoading(it)
    }

    override fun setOnClick() {
        super.setOnClick()

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

            var timer = Timer()
            val DELAY: Long = 500 // Milliseconds
            override fun afterTextChanged(s: Editable) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            lifecycleScope.launch(Dispatchers.Main) {
                                viewModel.setNameConsultant(s.trim().toString())
                                val dataPerformer = PerformerSearch(
                                    viewModel.getStatusConsultant(),
                                    viewModel.getNameConsultant(),
                                    viewModel.getHaveNumberReview(),
                                    viewModel.getGenderChecked(),
                                    viewModel.getListGenresSelected(),
                                    viewModel.getListRankingSelected(),
                                    viewModel.getReviewAverageChecked()
                                )
                                searchResultViewModel.setDataPerformer(dataPerformer)
                            }
                        }
                    },
                    DELAY
                )
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
//        title = appendConditionChecked(title)
        if (title.trim().toString().isNotEmpty()) {
            return title.trim().toString().trim().substring(0, title.trim().toString().length - 1)
        }
        return ""
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(context,2)
        mConsultantAdapter = context?.let {
            ConsultantAdapter(
                it,
                GenresUtil.getListGenres(),
                listener = { position, listData ->
                    if (!isDoubleClick) {
                        onClickDetailConsultant(position, listData)
                    }
                }
            )
        }!!

        binding.rvConsultantSearch.layoutManager = layoutManager
        binding.rvConsultantSearch.adapter = mConsultantAdapter
    }

    private fun onClickDetailConsultant(
        position: Int,
        listData: List<ConsultantResponse>
    ) {
        var bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, position)
        shareViewModel.setListPerformer(listData)
        appNavigation.openSearchResultToProfileScreen(bundle)
    }

//    private fun appendConditionChecked(title: StringBuilder): StringBuilder {
//        for (i in 0 until binding.genresChipGroup.childCount) {
//            val chip: Chip = binding.genresChipGroup.getChildAt(i) as Chip
//            if (chip.isChecked) {
//                title.append(chip.text).append("、 ")
//            }
//        }
//        for (i in 0 until binding.genderChipGroup.childCount) {
//            val chip: Chip = binding.genderChipGroup.getChildAt(i) as Chip
//            if (chip.isChecked) {
//                title.append(chip.text).append("、 ")
//            }
//        }
//        for (i in 0 until binding.rankingChipGroup.childCount) {
//            val chip: Chip = binding.rankingChipGroup.getChildAt(i) as Chip
//            if (chip.isChecked) {
//                title.append(chip.text).append("、 ")
//            }
//        }
//        for (i in 0 until binding.reviewChipGroup.childCount) {
//            val chip: Chip = binding.reviewChipGroup.getChildAt(i) as Chip
//            if (chip.isChecked) {
//                title.append(chip.text).append("、 ")
//            }
//        }
//        for (i in 0 until binding.statusChipGroup.childCount) {
//            val chip: Chip = binding.statusChipGroup.getChildAt(i) as Chip
//            if (chip.isChecked) {
//                title.append(chip.text).append("、 ")
//            }
//        }
//        return title
//    }

    override fun onDestroyView() {
        binding.rvConsultantSearch.clearOnScrollListeners()
        super.onDestroyView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}