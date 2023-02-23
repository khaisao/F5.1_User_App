package jp.careapp.counseling.android.ui.searchCategory

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.ConsultantAdapter
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.model.user_profile.PerformerSearch
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.main.MainActivity
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.GenresUtil
import jp.careapp.counseling.databinding.FragmentSearchResultBinding
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SearchResultFragment : BaseFragment<FragmentSearchResultBinding, SearchResultViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId = R.layout.fragment_search_result
    private val viewModel: SearchResultViewModel by viewModels()
    override fun getVM(): SearchResultViewModel = viewModel
    private var mConsultantAdapter: ConsultantAdapter? = null

    private val shareViewModel: ShareViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(BUNDLE_KEY.DATA_PERFORMER)) {
                val dataPerformer: PerformerSearch = it.getSerializable(BUNDLE_KEY.DATA_PERFORMER) as PerformerSearch
                viewModel.setDataPerformer(dataPerformer)
            }
        }
    }

    override fun initView() {
        super.initView()

        initRecyclerView()
        arguments?.let {
            var title = it.getString(BUNDLE_KEY.TITLE)
            binding.tvToolbar.text = title
            appNavigation.navController?.previousBackStackEntry?.savedStateHandle?.set(
                BUNDLE_KEY.GENRES_SELECTED,
                it.getIntArray(BUNDLE_KEY.GENRES_SELECTED)
            )
        }
        viewModel.newMessage.observe(viewLifecycleOwner) {
            it?.let {
                (activity as MainActivity).checkList(0 - Define.HEIGHT)
            }
        }

        binding.rvConsultantSearch.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val listDataSize = viewModel.listConsultantTemp.value?.size ?: 0
                if (listDataSize > 0) {
                    val layoutManager = binding.rvConsultantSearch.layoutManager as LinearLayoutManager
                    if (!viewModel.isLoadMoreData) {
                        if (layoutManager != null && (layoutManager.findLastCompletelyVisibleItemPosition() == listDataSize - 1) && viewModel.isCanLoadMoreData()) {
                            viewModel.isLoadMoreData = true
                            viewModel.loadMoreData()
                        }
                    }
                }
            }
        })

    }

    override fun onDestroyView() {
        binding.rvConsultantSearch.clearOnScrollListeners()
        super.onDestroyView()
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModel.dataPerformer.observe(viewLifecycleOwner) {
            if (viewModel.isFirstInit) {
                viewModel.getListBlockedConsultant()
                viewModel.isFirstInit = false
            }
        }

        viewModel.listConsultantTemp.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.tvNoResult.visibility = View.GONE
                mConsultantAdapter!!.submitList(it)
            } else {
                binding.tvNoResult.visibility = View.VISIBLE
            }
        }

        shareViewModel.isBlockConsultant.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getListBlockedConsultant()
                shareViewModel.isBlockConsultant.value = false
            }
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
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

    override fun setOnClick() {
        super.setOnClick()
        binding.btnLeft.setOnClickListener {
            appNavigation.navigateUp()
        }
    }
}
