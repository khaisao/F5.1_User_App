package jp.careapp.counseling.android.ui.live_stream

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.ui.profile.detail_user.DetailUserProfileViewModel
import jp.careapp.counseling.databinding.FragmentDetailUserProfileBinding
import jp.careapp.counseling.databinding.FragmentExitLivestreamBinding

@AndroidEntryPoint
class AfterExitLivestreamFragment :
    BaseFragment<FragmentExitLivestreamBinding, DetailUserProfileViewModel>() {
    override val layoutId: Int = R.layout.fragment_detail_user_profile

    private val viewModel: DetailUserProfileViewModel by viewModels()

    override fun getVM(): DetailUserProfileViewModel = viewModel

    private var consultantResponseLocal: ConsultantResponse? = null

}

