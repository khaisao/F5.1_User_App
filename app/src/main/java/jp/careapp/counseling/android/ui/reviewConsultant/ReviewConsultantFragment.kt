package jp.careapp.counseling.android.ui.reviewConsultant

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.customView.ToolBarCommon
import jp.careapp.counseling.databinding.FragmentReviewConsultantBinding
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class ReviewConsultantFragment :
    BaseFragment<FragmentReviewConsultantBinding, ReviewConsultantViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation
    override val layoutId = R.layout.fragment_review_consultant

    private val viewModel: ReviewConsultantViewModel by viewModels()

    override fun getVM(): ReviewConsultantViewModel = viewModel

    private var reviewManager: ReviewManager? = null

    override fun initView() {
        super.initView()
        reviewManager = activity?.let { ReviewManagerFactory.create(it) }

        viewModel.consultantCode = arguments?.getString(BUNDLE_KEY.PERFORMER_CODE).toString()

        val consultantName = arguments?.getString(BUNDLE_KEY.PERFORMER_NAME)
        val consultantAvatar = arguments?.getString(BUNDLE_KEY.PERFORMER_IMAGE)

        // Show consultant info
        binding.tvConsultantName.text = consultantName
        consultantAvatar?.let {
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(binding.ivConsultantAvatar)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnClick() {
        super.setOnClick()
        binding.toolBar.setOnToolBarClickListener(
            object : ToolBarCommon.OnToolBarClickListener() {
                override fun onClickLeft() {
                    appNavigation.navigateUp()
                }
            }
        )

        val rtReview = binding.layoutEdit.rtConsultantPointEdit
        val edtReview = binding.layoutEdit.edReview

        binding.layoutEdit.btnConfirm.setOnClickListener {
            if (edtReview.text.toString().length <= 140) {
                val point = rtReview.rating
                val review = edtReview.text

                binding.layoutEdit.root.visibility = View.GONE
                binding.layoutConfirm.root.visibility = View.VISIBLE

                binding.layoutConfirm.rtConsultantPoint.rating = point
                binding.layoutConfirm.tvConsultantReview.text = review
            } else {
                showAlertDialog(R.string.please_enter_review_less_than_140, R.string.text_OK)
            }
        }
        binding.layoutEdit.tvClose.setOnClickListener {
            appNavigation.navigateUp()
        }

        binding.layoutConfirm.tvCancel.setOnClickListener {
            // Show confirm layout
            binding.layoutEdit.root.visibility = View.VISIBLE
            binding.layoutConfirm.root.visibility = View.GONE
        }

        binding.layoutConfirm.btnReview.setOnClickListener {
            val point = rtReview.rating
            val review = edtReview.text.toString()
            viewModel.submitReview(
                point = point.roundToInt(),
                review = review
            )
        }
        binding.layoutEdit.edReview.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener false
            }
        }
        binding.layoutEdit.edReview.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if ((s != null && s.isNotEmpty()) && rtReview.rating > 0) activeBtnConfirm(true)
                    else activeBtnConfirm(false)
                    binding.layoutEdit.tvCount.text = (140 - s.toString().length).toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
// DO nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
// DO nothing
                }
            }
        )

        binding.layoutEdit.rtConsultantPointEdit
            .setOnRatingChangeListener { ratingBar, rating, fromUser ->
                run {
                    val s = edtReview.text
                    if ((s != null && s.isNotEmpty()) && rtReview.rating > 0) activeBtnConfirm(true)
                    else activeBtnConfirm(false)
                }
            }
    }

    private fun activeBtnConfirm(isActive: Boolean) {
        binding.layoutEdit.btnConfirm.apply {
            if (isActive) {
                isEnabled = true
                setTextColor(requireContext().getColor(R.color.white))
            } else {
                isEnabled = false
                setTextColor(requireContext().getColor(R.color.color_AEA2D1))
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.isLoading.observeForever(isLoadingObserver)
        viewModel.isSuccess.observeForever(isSuccessObserver)
        viewModel.showRating.observeForever(isShowRating)
        viewModel.reviewResponse.observeForever(isShowReviewSucess)
    }

    private var isLoadingObserver: Observer<Boolean> = Observer {
        showHideLoading(it)
    }

    private var isSuccessObserver: Observer<Boolean> = Observer {
        if (it) {
            appNavigation.navController?.let { it1 ->
                it1.popBackStack()
            }
        }
    }
    private var isShowRating: Observer<Boolean> = Observer {
        if (it)
            showRateApp()
    }

    private var isShowReviewSucess: Observer<Int> = Observer {
        appNavigation.navController?.previousBackStackEntry?.savedStateHandle?.set(
            BUNDLE_KEY.POINT_REVIEW,
            it
        )
        appNavigation.navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.isSuccess.removeObservers(viewLifecycleOwner)
        viewModel.isLoading.removeObservers(viewLifecycleOwner)
        viewModel.reviewResponse.removeObservers(viewLifecycleOwner)
    }

    private fun showRateApp() {
        val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo: ReviewInfo = task.result
                val flow: Task<Void> =
                    activity?.let { reviewManager!!.launchReviewFlow(it, reviewInfo) } as Task<Void>
                flow.addOnCompleteListener { task1 ->
                    appNavigation.navigateUp()
                }
            } else {
                appNavigation.navigateUp()
            }
        }
    }
}
