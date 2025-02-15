package jp.slapp.android.android.ui.favourite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.slapp.android.R
import jp.slapp.android.android.data.network.FavoriteResponse
import jp.slapp.android.databinding.FragmentFavouriteBinding
import jp.slapp.android.android.utils.event.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.network.HistoryResponse
import jp.slapp.android.android.data.shareData.ShareViewModel
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.utils.BUNDLE_KEY
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentFavouriteBinding, FavoriteViewModel>() {

    private val viewModels: FavoriteViewModel by viewModels()

    private val historyViewModel: HistoryViewModel by activityViewModels()

    private val shareViewModel: ShareViewModel by activityViewModels()

    override val layoutId: Int = R.layout.fragment_favourite

    override fun getVM(): FavoriteViewModel = viewModels

    private lateinit var adapterFavorite: FavoriteAdapter

    private lateinit var adapterHistory: HistoryAdapter

    private var typeFavoriteScreen: Int? = 0

    private var favorites: MutableList<FavoriteResponse> = mutableListOf()


    @Inject
    lateinit var appNavigation: AppNavigation

    override fun initView() {
        super.initView()

        arguments?.let {
            typeFavoriteScreen = it.getInt(BUNDLE_KEY.TYPE_ONLINE_LIST_SCREEN)
        }

        adapterFavorite = FavoriteAdapter(
            context = requireContext(),
            listener = { position, listData ->
                if (!isDoubleClick) {
                    onClickFavouriteItem(position, listData)
                }
            })

        adapterHistory = HistoryAdapter(
            context = requireContext(),
            listener = { position, listData ->
                if (!isDoubleClick) {
                    onClickHistoryItem(position, listData)
                }
            })

        setUpAdapter()

        if (typeFavoriteScreen == BUNDLE_KEY.TYPE_HISTORY) {
            binding.rvFavorite.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val listDataSize = historyViewModel.listHistoryConsultantResult.value?.size ?: 0
                    if (listDataSize > 0) {
                        val layoutManager = binding.rvFavorite.layoutManager as LinearLayoutManager
                        if (!historyViewModel.isLoadMoreData) {
                            if ((layoutManager.findLastCompletelyVisibleItemPosition() == listDataSize - 1) && historyViewModel.isCanLoadMoreData()) {
                                historyViewModel.isLoadMoreData = true
                                historyViewModel.loadMoreData()
                            }
                        }
                    }
                }
            })
        }
    }

    private fun setUpAdapter() {
        binding.apply {
            when (typeFavoriteScreen) {
                BUNDLE_KEY.TYPE_ALL_PERFORMER_FOLLOW_FAVORITE -> {
                    rvFavorite.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    rvFavorite.adapter = adapterFavorite
                    tvNoResult.text = requireContext().resources.getString(R.string.dont_follow_any_girl_yet)
                }
                BUNDLE_KEY.TYPE_HISTORY -> {
                    rvFavorite.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    rvFavorite.adapter = adapterHistory
                    tvNoResult.text = requireContext().resources.getString(R.string.no_viewing_history_yet)
                }
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModels.forceRefresh()

        viewModels.favoriteLoading.observe(
            viewLifecycleOwner
        ) {
            showHideLoading(it)
        }

        historyViewModel.isLoading.observe(viewLifecycleOwner) {
            showHideLoading(it)
            if (it) {
                binding.rvFavorite.visibility = View.INVISIBLE

            } else {
                binding.rvFavorite.visibility = View.VISIBLE
            }
        }

        shareViewModel.detectRefreshDataFollowerHome.observe(
            viewLifecycleOwner
        ) {
            viewModels.forceRefresh()
        }

        shareViewModel.detectRefreshDataHistory.observe(
            viewLifecycleOwner
        ) {
            historyViewModel.clearData()
            historyViewModel.getListBlockedConsultant()

        }

        shareViewModel.detectRefreshDataFavorite.observe(
            viewLifecycleOwner
        ) {
            viewModels.forceRefresh()
        }

        viewModels.error.observe(
            viewLifecycleOwner,
            Observer {
            }
        )
        viewModels.deleteFavoriteLoading.observe(
            viewLifecycleOwner,
            Observer {
                showHideLoading(it)
            }
        )
        viewModels.uiDataResult.observe(
            viewLifecycleOwner,
            Observer {
                if (typeFavoriteScreen == BUNDLE_KEY.TYPE_ALL_PERFORMER_FOLLOW_FAVORITE) {
                    adapterFavorite.submitList(it.toMutableList())
                    showNoData(it.isEmpty())
                }
            }
        )

        historyViewModel.listHistoryConsultantResult.observe(
            viewLifecycleOwner
        ) {
            if (typeFavoriteScreen == BUNDLE_KEY.TYPE_HISTORY) {
                if (it.isEmpty()) {
                    showNoData(true)
                } else {
                    showNoData(false)
                    adapterHistory.submitList(it.toMutableList())
                }
            }
        }

        viewModels.showDialogAction.observe(
            viewLifecycleOwner,
            EventObserver { data ->
                CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                    .showDialog()
                    .setDialogTitleWithString("${data.name} " + resources.getString(R.string.remove_favorite_ques))
                    .setTextNegativeButton(R.string.no_block_alert)
                    .setTextPositiveButton(R.string.confirm_block_alert)
                    .setOnNegativePressed {
                        it.dismiss()
                    }
                    .setOnPositivePressed {
                        viewModels.setCodeFavarite(data.code)
                        if (favorites.removeIf {
                            it.code == data.code
                        }
                        )
                            adapterFavorite.submitList(favorites)
                        if (favorites.isEmpty())
                            viewModels.isShowNoData.postValue(true)
                        it.dismiss()
                    }
            }
        )
    }

    private fun showNoData(isShow: Boolean) {
        if (isShow) {
            binding.llNoResult.visibility = View.VISIBLE
            binding.rvFavorite.visibility = View.GONE
        } else {
            binding.llNoResult.visibility = View.GONE
            binding.rvFavorite.visibility = View.VISIBLE
        }
    }

    private fun onClickFavouriteItem(
        position: Int,
        listData: List<FavoriteResponse>
    ) {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, position)
        val listConsultantResponse = arrayListOf<ConsultantResponse>()
        for (item in listData) {
            val consultantItem = ConsultantResponse(
                code = item.code
            )
            listConsultantResponse.add(consultantItem)
        }
        shareViewModel.setListPerformer(listConsultantResponse)
        appNavigation.openTopToUserProfileScreen(bundle)
    }

    private fun onClickHistoryItem(
        position: Int,
        listData: List<HistoryResponse>
    ) {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.POSITION_SELECT, position)
        val listConsultantResponse = arrayListOf<ConsultantResponse>()
        for (item in listData) {
            val consultantItem = ConsultantResponse(
                code = item.code
            )
            listConsultantResponse.add(consultantItem)
        }
        shareViewModel.setListPerformer(listConsultantResponse)
        appNavigation.openTopToUserProfileScreen(bundle)
    }

    companion object {
        @JvmStatic
        fun newInstance(typeOnlineListScreen: Int) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putInt(BUNDLE_KEY.TYPE_ONLINE_LIST_SCREEN, typeOnlineListScreen)
                }
            }
    }
}
