package jp.careapp.counseling.android.ui.live_stream

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.adapter.ConsultantAdapter
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.handle.HandleBuyPoint
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.profile.detail_user.DetailUserProfileViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.SCREEN_DETAIL
import jp.careapp.counseling.databinding.FragmentExitLivestreamBinding
import javax.inject.Inject


@AndroidEntryPoint
class ExitLivestreamFragment :
    BaseFragment<FragmentExitLivestreamBinding, ExitLiveStreamViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    override val layoutId: Int = R.layout.fragment_exit_livestream

    private val viewModel: ExitLiveStreamViewModel by viewModels()

    private val detailViewModel: DetailUserProfileViewModel by viewModels()

    override fun getVM(): ExitLiveStreamViewModel = viewModel

    private var consultantResponseLocal: ConsultantResponse? = null

    private val shareViewModel: ShareViewModel by activityViewModels()

    private var isFirstChat: Boolean? = null

    private var previousScreen = ""

    private var isShowFromUserDisable: Boolean = false

    private var rootScreen: Int = 0

    private lateinit var behavior: AppBarLayout.Behavior


    @Inject
    lateinit var handleBuyPoint: HandleBuyPoint

    private val adapter: ConsultantAdapter by lazy {
        ConsultantAdapter(
            requireContext(),
            listener = { position, listData ->
                if (!isDoubleClick) {
                    onClickDetailConsultant(position, listData)
                }
            },
            isExitLiveStreamScreen = true
        )
    }

    override fun initView() {
        super.initView()
        val bundle = arguments

        if (bundle != null) {
            consultantResponseLocal =
                bundle.getSerializable(BUNDLE_KEY.USER_PROFILE) as? ConsultantResponse
            previousScreen = bundle.getString(BUNDLE_KEY.SCREEN_TYPE, "")
            rootScreen = bundle.getInt(BUNDLE_KEY.ROOT_SCREEN)
        }

        binding.rvConsultant.layoutManager = GridLayoutManager(context, 2)
        binding.rvConsultant.adapter = adapter

        binding.rvConsultant.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val listDataSize = viewModel.listConsultantResult.value?.size ?: 0
                if (listDataSize > 0) {
                    val layoutManager = binding.rvConsultant.layoutManager as LinearLayoutManager
                    if (!viewModel.isLoadMoreData) {
                        if ((layoutManager.findLastCompletelyVisibleItemPosition() == listDataSize - 1) && viewModel.isCanLoadMoreData()) {
                            viewModel.isLoadMoreData = true
                            viewModel.loadMoreData()
                        }
                    }
                }
            }
        })

        consultantResponseLocal?.let { changeStatusIsFavorite(it.isFavorite) }

        binding.tvName.text = consultantResponseLocal?.name ?: ""

        binding.ivAvatar.loadImage(consultantResponseLocal?.thumbnailImageUrl)

        val params = binding.alRv.layoutParams as CoordinatorLayout.LayoutParams
        behavior = params.behavior as AppBarLayout.Behavior
    }

    override fun setOnClick() {
        super.setOnClick()
        binding.ivClose.setOnClickListener {
            if (rootScreen == SCREEN_DETAIL) {
                openDetailScreen()
            } else {
                openChatScreen()
            }
        }

        binding.ivMessage.setOnClickListener {
            if (!isDoubleClick) {
                openChatScreen()
            }
        }

        binding.ivAddFollow.setOnClickListener {
            if (!isDoubleClick) {
                consultantResponseLocal?.let {
                    isShowFromUserDisable = false
                    detailViewModel.addUserToFavorite(
                        it.code ?: ""
                    )
                }
            }
        }
        binding.ivRemoveFollow.setOnClickListener {
            if (!isDoubleClick) {
                consultantResponseLocal?.let {
                    detailViewModel.removeUserToFavorite(
                        it.code ?: ""
                    )
                }
            }
        }
    }

    private fun openChatScreen(isShowFreeMess: Boolean = false) {
        val bundle = Bundle()
        bundle.putString(
            BUNDLE_KEY.PERFORMER_CODE,
            consultantResponseLocal?.code ?: ""
        )
        bundle.putString(
            BUNDLE_KEY.PERFORMER_NAME,
            consultantResponseLocal?.name ?: ""
        )
        bundle.putBoolean(BUNDLE_KEY.PROFILE_SCREEN, false)
        bundle.putBoolean(BUNDLE_KEY.IS_SHOW_FREE_MESS, isShowFreeMess)
        bundle.putInt(BUNDLE_KEY.CALL_RESTRICTION, consultantResponseLocal?.callRestriction ?: 0)
        appNavigation.openExitLiveStreamToMessage(bundle)
    }

    private fun openDetailScreen() {
        val bundle = Bundle()
        bundle.putSerializable(BUNDLE_KEY.USER_PROFILE, consultantResponseLocal)
        appNavigation.openExitLiveStreamToUserDetailFragment(bundle)
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.listConsultantResult.observe(
            viewLifecycleOwner
        ) {
            if (!it.isNullOrEmpty()) {
                binding.rvConsultant.visibility = VISIBLE
                binding.llNoResult.visibility = GONE
                behavior.setDragCallback(object : DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return true
                    }
                })
                adapter.submitList(it)
            } else {
                binding.rvConsultant.visibility = GONE
                binding.llNoResult.visibility = VISIBLE
                behavior.setDragCallback(object : DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return false
                    }
                })
            }
        }

        detailViewModel.statusFavorite.observe(viewLifecycleOwner, handleResultStatusFavorite)
        detailViewModel.statusRemoveFavorite.observe(viewLifecycleOwner, handleResultStatusUnFavorite)
        detailViewModel.isFirstChat.observe(viewLifecycleOwner, handleFirstChat)
        detailViewModel.isLoading.observe(viewLifecycleOwner, isLoadingObserver)
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

    private fun changeStatusIsFavorite(isFavorite: Boolean) {
        if (isFavorite) {
            binding.apply {
                ivRemoveFollow.visibility = VISIBLE
                ivAddFollow.visibility = GONE
            }

        } else {
            binding.apply {
                ivRemoveFollow.visibility = GONE
                ivAddFollow.visibility = VISIBLE
            }
        }
    }
    private var handleResultStatusFavorite: Observer<Boolean> = Observer {
        if (it) {
            // when user is first chat and disable
            if (isShowFromUserDisable) {
                detailViewModel.statusFavorite.value = false
            }
            changeStatusIsFavorite(true)
        }
    }

    private var handleResultStatusUnFavorite: Observer<Boolean> = Observer {
        if (it) {
            changeStatusIsFavorite(false)
        }
    }

    private var handleFirstChat: Observer<Boolean> = Observer {
        this.isFirstChat = it
    }

    private var isLoadingObserver: Observer<Boolean> = Observer {
        showHideLoading(it)
    }

}

