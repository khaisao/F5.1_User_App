package jp.careapp.counseling.android.ui.live_stream

import android.os.Bundle
import android.view.View
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
import jp.careapp.counseling.android.ui.buy_point.BuyPointBottomFragment
import jp.careapp.counseling.android.ui.profile.detail_user.DetailUserProfileViewModel
import jp.careapp.counseling.android.ui.profile.update_trouble_sheet.TroubleSheetUpdateFragment
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
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

    private lateinit var behavior:  AppBarLayout.Behavior


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
        }

        consultantResponseLocal?.code?.let { detailViewModel.loadMailInfo(it) }
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
            appNavigation.navigateUp()
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

        binding.tvBuyPoint.setOnClickListener {
            if (!isDoubleClick) {
                doBuyPoint()
            }
        }
    }

    private fun openChatScreen(isShowFreeMess: Boolean = false) {
        if (isFirstChat == null) {
            consultantResponseLocal?.code?.let { it1 ->
                detailViewModel.loadMailInfo(
                    it1
                )
            }
        } else {
            isFirstChat?.let {
                if (it) {
                    // transition to trouble sheet screen
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
                    bundle.putInt(
                        BUNDLE_KEY.FIRST_CHAT,
                        TroubleSheetUpdateFragment.FIRST_CHAT
                    )
                    bundle.putBoolean(BUNDLE_KEY.IS_SHOW_FREE_MESS, isShowFreeMess)
                    bundle.putInt(BUNDLE_KEY.CALL_RESTRICTION, consultantResponseLocal?.callRestriction ?: 0)
                    appNavigation.openDetailUserToChatMessage(bundle)
                } else {
                    if ((rxPreferences.getPoint() == 0)) {
                        doBuyPoint()
                    } else {
                        handleOpenChatScreen(isShowFreeMess)
                    }
                }
            }
        }
    }

    private fun doBuyPoint() {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.TYPE_BUY_POINT, Define.BUY_POINT_FIRST)
        handleBuyPoint.buyPoint(
            childFragmentManager,
            bundle,
            object : BuyPointBottomFragment.HandleBuyPoint {
                override fun buyPointSucess() {
                    activity?.let { it1 ->
                        handleOpenChatScreen()
                    }
                }
            }
        )
    }

    private fun handleOpenChatScreen(isShowFreeMess: Boolean = false) {
        val bundle = Bundle()
        bundle.putString(
            BUNDLE_KEY.PERFORMER_CODE,
            consultantResponseLocal?.code ?: ""
        )
        bundle.putString(
            BUNDLE_KEY.PERFORMER_NAME,
            consultantResponseLocal?.name ?: ""
        )
        bundle.putBoolean(BUNDLE_KEY.IS_SHOW_FREE_MESS, isShowFreeMess)
        bundle.putInt(BUNDLE_KEY.CALL_RESTRICTION, consultantResponseLocal?.callRestriction ?: 0)
        if (BUNDLE_KEY.CHAT_MESSAGE == previousScreen) {
            appNavigation.navigateUp()
        } else {
            appNavigation.openExitLiveStreamToMessage(bundle)
        }
    }


    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.listConsultantResult.observe(
            viewLifecycleOwner
        ) {
            if (!it.isNullOrEmpty()) {
                binding.rvConsultant.visibility = View.VISIBLE
                binding.llNoResult.visibility = View.GONE
                behavior.setDragCallback(object : DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return true
                    }
                })

            } else {
                binding.rvConsultant.visibility = View.GONE
                binding.llNoResult.visibility = View.VISIBLE
                behavior.setDragCallback(object : DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return false
                    }
                })
            }
            adapter.submitList(it)
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
                ivRemoveFollow.visibility = View.VISIBLE
                ivAddFollow.visibility = View.GONE
            }

        } else {
            binding.apply {
                ivRemoveFollow.visibility = View.GONE
                ivAddFollow.visibility = View.VISIBLE
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

