package jp.careapp.counseling.android.ui.notification

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseFragment
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.UpdateNotificationParams
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.ui.mypage.MyPageViewModel
import jp.careapp.counseling.databinding.FragmentNotificationBinding

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding, NotificationViewModel>() {

    private val viewModels: NotificationViewModel by viewModels()
    private val myPageViewModels: MyPageViewModel by viewModels()
    override val layoutId = R.layout.fragment_notification
    override fun getVM(): NotificationViewModel = viewModels

    private var mMember: MemberResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            mMember = bundle.getParcelable("member")
        }
    }

    override fun bindingStateView() {
        super.bindingStateView()

        viewModels.error.observe(
            viewLifecycleOwner,
            Observer {
            }
        )
        viewModels.updateNotificationLoading.observe(
            viewLifecycleOwner,
            Observer {
                showHideLoading(it)
            }
        )
        binding.apply {
            viewModels.saveStateSwitch.observe(
                viewLifecycleOwner,
                Observer { member ->
                    pushNotification.apply {
                        btnNotifi.isChecked = member.pushMail == 1
                        var check = btnNotifi.isChecked
                        btnNotifi.setOnClickListener {
                            if (btnNotifi.isChecked != check) {
                                val newState = getCheck(btnNotifi.isChecked)
                                viewModels.setParamsUpdate(
                                    UpdateNotificationParams(
                                        pushMail = newState,
                                    )
                                )
                                member.pushMail = newState
                                check = !check
                            }
                        }
                    }
                }
            )

            viewModels.openDirect.observe(
                viewLifecycleOwner
            ) {
                if (it) myPageViewModels.forceRefresh()
            }

            myPageViewModels.uiMember.observe(
                viewLifecycleOwner
            ) {
                viewModels.setSaveStateSwitchFromMemberResponse(it)
            }
        }
    }

    private fun getCheck(check: Boolean): Int {
        return if (check) 1 else 0
    }
}

data class SaveStateSwitch(
    var pushMail: Int = 0,
)
