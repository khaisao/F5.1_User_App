package jp.slapp.android.android.ui.profile.tab_review

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.databinding.TabReviewUserBinding
import jp.slapp.android.android.model.user_profile.ReviewUserProfile
import jp.slapp.android.android.utils.BUNDLE_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TabReviewFragment : BaseFragment<TabReviewUserBinding, TabReviewViewModel>() {

    override val layoutId = R.layout.tab_review_user

    private val viewModel: TabReviewViewModel by viewModels()

    override fun getVM(): TabReviewViewModel = viewModel

    private lateinit var reviewUserAdapter: ReviewUserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            val performer =
                bundle.getSerializable(BUNDLE_KEY.USER_PROFILE) as? ConsultantResponse
            performer?.let {
                loadData(it.code)
            }
        }
    }

    override fun initView() {
        super.initView()
        reviewUserAdapter = ReviewUserAdapter(viewLifecycleOwner)
        binding.reviewUserRv.adapter = reviewUserAdapter
    }

    private fun loadData(code: String?) {
        code?.let {
            activity?.let { it1 -> viewModel.loadDetailUser(code) }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.userProfileResult.observe(viewLifecycleOwner, handleResuleReviewUser)
    }

    private var handleResuleReviewUser: Observer<List<ReviewUserProfile>?> = Observer {
        reviewUserAdapter.run {
            it?.let { data ->
                updateTitle?.onUpdate(
                    String.format(
                        getString(R.string.user_profile_review),
                        data.size
                    )
                )
                submitList(data)
            }
        }
    }

    var updateTitle: UpdateTitle? = null
    public fun setContentUpdateTitle(updateTitle: UpdateTitle) {
        this.updateTitle = updateTitle
    }

    interface UpdateTitle {
        fun onUpdate(title: String)
    }

    companion object {
        fun getInstance(
            consultantResponse: ConsultantResponse
        ): TabReviewFragment {
            var tabReviewFragment = TabReviewFragment()
            var bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY.USER_PROFILE, consultantResponse)
            tabReviewFragment.arguments = bundle
            return tabReviewFragment
        }
    }
}
