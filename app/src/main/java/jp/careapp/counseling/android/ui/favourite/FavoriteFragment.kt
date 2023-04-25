package jp.careapp.counseling.android.ui.favourite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.FavoriteResponse
import jp.careapp.counseling.databinding.FragmentFavouriteBinding
import jp.careapp.counseling.android.utils.event.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
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

    private lateinit var adapterFavoriteHome: FavoriteHomeAdapter

    private var typeFavoriteScreen: Int? = 0

    private var favorites: MutableList<FavoriteResponse> = mutableListOf()


    @Inject
    lateinit var appNavigation: AppNavigation

    override fun initView() {
        super.initView()

        arguments?.let {
            typeFavoriteScreen = it.getInt(BUNDLE_KEY.TYPE_ONLINE_LIST_SCREEN)
        }

        adapterFavorite = FavoriteAdapter(context = requireContext(), onItemClick = { item ->
            val bundle = Bundle()
            bundle.putInt(BUNDLE_KEY.POSITION_SELECT, 0)
            val listConsultant = ArrayList(
                listOf(
                    ConsultantResponse(
                        code = item.code,
                        existsImage = item.existsImage,
                        imageUrl = item.imageUrl,
                        name = item.name,
                        presenceStatus = item.presenceStatus,
                        stage = item.status,
                        thumbnailImageUrl = item.thumbnailImageUrl
                    )
                )
            )
            bundle.putSerializable(
                BUNDLE_KEY.LIST_USER_PROFILE,
                listConsultant
            )
            appNavigation.openRankingToUserProfileScreen(bundle)
        })
        adapterHistory = HistoryAdapter(
            context = requireContext(),
            onItemClick = { item ->

                val bundle = Bundle()
                bundle.putInt(BUNDLE_KEY.POSITION_SELECT, 0)

                val listConsultant = ArrayList(
                    listOf(
                        ConsultantResponse(
                            code = item.code,
                            existsImage = item.existsImage,
                            imageUrl = item.imageUrl,
                            name = item.name,
                            stage = item.status,
                            thumbnailImageUrl = item.thumbnailImageUrl
                        )
                    )
                )

                bundle.putSerializable(
                    BUNDLE_KEY.LIST_USER_PROFILE,
                    listConsultant
                )
                appNavigation.openRankingToUserProfileScreen(bundle)
            })
        adapterFavoriteHome = FavoriteHomeAdapter(onItemClick = { item ->
            val bundle = Bundle()
            bundle.putInt(BUNDLE_KEY.POSITION_SELECT, 0)
            val listConsultant = ArrayList(
                listOf(
                    ConsultantResponse(
                        code = item.code,
                        existsImage = item.existsImage,
                        imageUrl = item.imageUrl,
                        name = item.name,
                        presenceStatus = item.presenceStatus,
                        stage = item.status,
                        thumbnailImageUrl = item.thumbnailImageUrl
                    )
                )
            )
            bundle.putSerializable(
                BUNDLE_KEY.LIST_USER_PROFILE,
                listConsultant
            )
            appNavigation.openRankingToUserProfileScreen(bundle)
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
                }
                BUNDLE_KEY.TYPE_HISTORY -> {
                    rvFavorite.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    rvFavorite.adapter = adapterHistory
                }
                else -> {
                    rvFavorite.layoutManager = GridLayoutManager(requireContext(), 2)
                    rvFavorite.adapter = adapterFavoriteHome
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
                }
                adapterFavoriteHome.submitList(it.toMutableList())
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
