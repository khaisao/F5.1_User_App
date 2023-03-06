package jp.careapp.counseling.android.ui.favourite

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun bindingStateView() {
        super.bindingStateView()
        viewModels.forceRefresh()
        adapterFavorite = FavoriteAdapter(lifecycleOwner = viewLifecycleOwner, events = viewModels)
        adapterHistory = HistoryAdapter(lifecycleOwner = viewLifecycleOwner, events = historyViewModel)
        adapterFavoriteHome = FavoriteHomeAdapter(lifecycleOwner = viewLifecycleOwner, events = viewModels)
        binding.apply {
            if (typeFavoriteScreen == BUNDLE_KEY.TYPE_ALL_PERFORMER_FOLLOW_FAVORITE) {
                rvFavorite.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rvFavorite.adapter = adapterFavorite
            } else if (typeFavoriteScreen == BUNDLE_KEY.TYPE_HISTORY) {
                rvFavorite.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rvFavorite.adapter = adapterHistory
            } else {
                rvFavorite.layoutManager = GridLayoutManager(requireContext(), 2)
                rvFavorite.adapter = adapterFavoriteHome
            }

            appBar.apply {
                btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_left))
                btnLeft.setOnClickListener {
                    if (!isDoubleClick)
                        findNavController().navigateUp()
                }
                tvTitle.text = getString(R.string.favorite_counselor)
                viewStatusBar.visibility = View.GONE
                lineBottom.isVisible = false
                arguments?.apply {
                    binding.appBar.root.isVisible = getBoolean(BUNDLE_KEY.IS_SHOW_TOOLBAR, false)
                }
            }
        }
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

        viewModels.isShowNoData.observe(viewLifecycleOwner, isShowNoDataObserver)
    }

    private var isShowNoDataObserver: Observer<Boolean> = Observer {
        showNoData(it)
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
