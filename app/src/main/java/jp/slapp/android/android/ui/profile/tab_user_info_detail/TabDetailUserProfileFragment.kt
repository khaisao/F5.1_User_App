package jp.slapp.android.android.ui.profile.tab_user_info_detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.slapp.android.R
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.databinding.TabDetailUserProfileBinding

@AndroidEntryPoint
class TabDetailUserProfileFragment :
    BaseFragment<TabDetailUserProfileBinding, TabDetailViewModel>() {
    override val layoutId = R.layout.tab_detail_user_profile

    private val viewModel: TabDetailViewModel by viewModels()

    override fun getVM(): TabDetailViewModel = viewModel

    private var listGenRest: MutableMap<Int, String> = mutableMapOf(
        6 to "復縁",
        7 to "夫婦関係",
        9 to "不倫",
        10 to "浮気",
        11 to "離婚",
        12 to "片思い",
        32 to "その他"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            val performer =
                bundle.getSerializable(BUNDLE_KEY.USER_PROFILE) as? ConsultantResponse
            performer?.let {
                setData(it)
                viewModel.loadDetailUser(it.code ?: "-1")
            }
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.userProfileResult.observe(viewLifecycleOwner, handleResuleReviewUser)
    }

    private var handleResuleReviewUser: Observer<ConsultantResponse?> = Observer {
        it?.let { data ->
            setData(data)
        }
    }

    private fun setData(consultantResponse: ConsultantResponse) {
        val listStringGenres = mutableListOf<String>()
        consultantResponse.let {
            it.genres.forEach {
                if (listGenRest.containsKey(it)) {
                    listStringGenres.add(listGenRest.get(it) ?: "")
                }
            }
            binding.messageTv.setText(it.message)
        }
        binding.tagview.clear()
        binding.tagview.setTags(listStringGenres)
    }

    companion object {
        fun getInstance(
            consultantResponse: ConsultantResponse
        ): TabDetailUserProfileFragment {
            val tabDetailUserProfileFragment = TabDetailUserProfileFragment()
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY.USER_PROFILE, consultantResponse)
            tabDetailUserProfileFragment.arguments = bundle
            return tabDetailUserProfileFragment
        }
    }
}
