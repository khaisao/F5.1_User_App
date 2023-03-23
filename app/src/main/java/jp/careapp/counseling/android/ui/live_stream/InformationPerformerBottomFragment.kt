package jp.careapp.counseling.android.ui.live_stream

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.utils.loadImage
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.profile.detail_user.DetailUserProfileViewModel
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.extensions.getBustSize
import jp.careapp.counseling.databinding.FragmentInfoPerformerBottomBinding
import javax.inject.Inject

@AndroidEntryPoint
class InformationPerformerBottomFragment : BottomSheetDialogFragment() {

    companion object {
        var isShow: Boolean = false
        fun showInfoPerformerBottomSheet(
            fragmentManager: FragmentManager,
            itemView: ClickItemView,
            consultantResponse: ConsultantResponse
        ) {
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY.USER_PROFILE_BOTTOM_SHEET, consultantResponse)
            val fragment = InformationPerformerBottomFragment()
            fragment.setClickItemView(itemView)
            fragment.arguments = bundle
            fragment.show(fragmentManager, "")
        }
    }

    @Inject
    lateinit var appNavigation: AppNavigation

    private var consultantResponseLocal: ConsultantResponse? = null

    lateinit var binding: FragmentInfoPerformerBottomBinding

    private val viewModel: DetailUserProfileViewModel by viewModels()

    override fun onStart() {
        super.onStart()

        val view = view
        requireView().post {
            val parent = view?.parent as View
            val params =
                parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            bottomSheetBehavior?.peekHeight = view.measuredHeight
            try {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBehavior?.addBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                                isShow = true
                            }
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                    }
                )
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        bundle: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_info_performer_bottom,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            consultantResponseLocal =
                bundle.getSerializable(BUNDLE_KEY.USER_PROFILE_BOTTOM_SHEET) as? ConsultantResponse
            binding.apply {
                ivAvatar.loadImage(consultantResponseLocal?.imageUrl, true)
                tvUserName.text = consultantResponseLocal?.name ?: ""
                tvAge.text =
                    consultantResponseLocal?.age.toString() + resources.getString(R.string.age_raw)
                tvMessageNotice.text = consultantResponseLocal?.messageNotice ?: ""
                consultantResponseLocal?.let { changeStatusIsFavorite(it.isFavorite) }
                val bustSize =
                    consultantResponseLocal?.let { requireContext().getBustSize(it.bust) }

                if (bustSize == "") {
                    tvSize.visibility = View.GONE
                } else {
                    tvSize.visibility = View.VISIBLE
                    tvSize.text = bustSize
                }
            }
        }
        setOnClickView()
        viewModel.statusFavorite.observe(viewLifecycleOwner, handleResultStatusFavorite)
        viewModel.statusRemoveFavorite.observe(viewLifecycleOwner, handleResultStatusUnFavorite)
        viewModel.isLoading.observe(viewLifecycleOwner, handleLoading)

    }

    private var handleResultStatusFavorite: Observer<Boolean> = Observer {
        if (it) {
            viewModel.statusFavorite.value = false
            changeStatusIsFavorite(true)
        }
    }

    private var handleResultStatusUnFavorite: Observer<Boolean> = Observer {
        if (it) {
            changeStatusIsFavorite(false)
        }
    }

    private var handleLoading: Observer<Boolean> = Observer {
        if (it) {
            (activity as BaseActivity<*, *>?)?.showLoading()
        } else {
            (activity as BaseActivity<*, *>?)?.hiddenLoading()
        }
    }

    private fun changeStatusIsFavorite(isFavorite: Boolean) {
        binding.apply {
            removeFavoriteTv.visibility = if (isFavorite) View.VISIBLE else View.GONE
            addFavoriteTv.visibility = if (isFavorite) View.GONE else View.VISIBLE
        }
    }


    private fun setOnClickView() {
        binding.addFavoriteTv.setOnClickListener {
            if ((activity as BaseActivity<*, *>?)?.isDoubleClick == false) {
                consultantResponseLocal?.let {
                    viewModel.addUserToFavorite(
                        it.code ?: ""
                    )
                }
            }
            clickItemView.onAddFollowClick()
        }

        binding.removeFavoriteTv.setOnClickListener {
            if ((activity as BaseActivity<*, *>?)?.isDoubleClick == false) {
                consultantResponseLocal?.let {
                    viewModel.removeUserToFavorite(
                        it.code ?: ""
                    )
                }
            }
            clickItemView.onRemoveFollowClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isShow = false
    }

    private lateinit var clickItemView: ClickItemView
    fun setClickItemView(clickItemView: ClickItemView) {
        this.clickItemView = clickItemView
    }

    interface ClickItemView {
        fun onAddFollowClick()
        fun onRemoveFollowClick()
    }
}