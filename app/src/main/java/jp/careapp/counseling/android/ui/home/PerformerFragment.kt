package jp.careapp.counseling.android.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.ConsultantAdapter
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.favourite.FavoriteViewModel
import jp.careapp.counseling.android.ui.main.MainViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.GenresUtil
import jp.careapp.counseling.databinding.FragmentPerformerBinding
import javax.inject.Inject

@AndroidEntryPoint
class PerformerFragment : BaseFragment<FragmentPerformerBinding,HomeViewModel>(){

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences
    override val layoutId: Int = R.layout.fragment_performer

    private val viewModel: HomeViewModel by viewModels()

    private val favoriteViewModel: FavoriteViewModel by activityViewModels()


    override fun getVM(): HomeViewModel = viewModel

    private val mainViewModels: MainViewModel by activityViewModels()

    private val shareViewModel: ShareViewModel by activityViewModels()

    private var typeOnlineListScreen: Int? = 0

    private val mConsultantAdapter: ConsultantAdapter by lazy {
        ConsultantAdapter(
            requireContext(),
            GenresUtil.getListGenres(),
            listener = { position, listData ->
                if (!isDoubleClick) {
                    onClickDetailConsultant(position, listData)
                }
            }
        )
    }

    override fun initView() {
        super.initView()
        arguments?.let {
            typeOnlineListScreen = it.getInt(BUNDLE_KEY.TYPE_ONLINE_LIST_SCREEN)
        }

        binding.rvConsultant.layoutManager = GridLayoutManager(context, 2)
        binding.rvConsultant.adapter = mConsultantAdapter

        binding.rvConsultant.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val listDataSize = viewModel.listConsultantResult.value?.size ?: 0
                if (listDataSize > 0) {
                    val layoutManager = binding.rvConsultant.layoutManager as LinearLayoutManager
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

    override fun bindingStateView() {
        super.bindingStateView()
        if(typeOnlineListScreen==BUNDLE_KEY.TYPE_ALL_PERFORMER)
        viewModel.listConsultantResult.observe(
            viewLifecycleOwner
        ) {
            if (!it.isNullOrEmpty()) {
                shareViewModel.saveListPerformerSearch(it)
            }
            mConsultantAdapter.submitList(it)
        }else{
            favoriteViewModel.uiDataResult.observe(
                viewLifecycleOwner,
                Observer {
//                    it ?: return@Observer
//                    favorites.clear()
//                    favorites.addAll(it)
//                    adapter.submitList(it.toMutableList())
                }
            )
        }
    }

    fun loadData(isShowLoading: Boolean) {
//        activity?.let { act -> viewModel.getListTypeRanking(typeRanking!!, act, isShowLoading) }
    }

    private fun onClickDetailConsultant(
        position: Int,
        listData: List<ConsultantResponse>
    ) {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, position)
        shareViewModel.setListPerformer(listData)
        appNavigation.openTopToUserProfileScreen(bundle)
    }

    companion object {
        @JvmStatic
        fun newInstance(typeOnlineListScreen: Int) =
            PerformerFragment().apply {
                arguments = Bundle().apply {
                    putInt(BUNDLE_KEY.TYPE_ONLINE_LIST_SCREEN, typeOnlineListScreen)
                }
            }
    }


}