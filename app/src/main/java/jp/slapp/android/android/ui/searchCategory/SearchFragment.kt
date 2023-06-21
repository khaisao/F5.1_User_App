package jp.slapp.android.android.ui.searchCategory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.slapp.android.R
import jp.slapp.android.android.adapter.ConsultantAdapter
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.data.shareData.ShareViewModel
import jp.slapp.android.android.model.user_profile.PerformerSearch
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.databinding.FragmentSearchBinding
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

    }

    override fun initView() {
        super.initView()
        shareViewModel.getListPerformerSearch()?.let {
            viewModel.setListConsultantResult(it)
        }

        DeviceUtil.showKeyboardWithFocus(binding.edtInputName,requireActivity())
        initRecyclerView()

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
            if (binding.edtInputName.text.toString().isNotBlank()) {
                if (!it.isNullOrEmpty()) {
                    binding.llNoResult.visibility = View.GONE
                    binding.rvConsultantSearch.visibility = View.VISIBLE
                    mConsultantAdapter!!.submitList(it)
                } else {
                    binding.llNoResult.visibility = View.VISIBLE
                    binding.rvConsultantSearch.visibility = View.GONE
                }
            } else {
                binding.llNoResult.visibility = View.GONE
                binding.rvConsultantSearch.visibility = View.GONE
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
                                if (s.trim().toString() == "") {
                                    binding.rvConsultantSearch.visibility = View.GONE
                                    binding.llNoResult.visibility = View.GONE
                                    mConsultantAdapter?.submitList(emptyList())
                                } else {
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
                        }
                    },
                    DELAY
                )
            }
        })

        binding.ivClear.setOnClickListener {
            mConsultantAdapter?.submitList(emptyList())
            binding.rvConsultantSearch.visibility = View.GONE
            binding.llNoResult.visibility = View.GONE
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
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, position)
        shareViewModel.setListPerformer(listData)
        appNavigation.openSearchResultToProfileScreen(bundle)
    }

    override fun onDestroyView() {
        binding.rvConsultantSearch.clearOnScrollListeners()
        super.onDestroyView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}