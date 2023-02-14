package jp.careapp.counseling.android.ui.notification

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
            appBar.apply {
                tvTitle.text = getString(R.string.notification_setting)
                btnLeft.setImageDrawable(resources.getDrawable(R.drawable.ic_back_left))
                btnLeft.setOnClickListener {
                    if (!isDoubleClick)
                        findNavController().navigateUp()
                }
                viewStatusBar.visibility = View.GONE
            }

            viewModels.saveStateSwitch.observe(
                viewLifecycleOwner,
                Observer { member ->
                    notifiWeb.apply {
                        tvStart.text = resources.getString(R.string.notifi_web)
                        btnNotifi.isChecked = member.pushNewsletter == 1
                        var check = btnNotifi.isChecked
                        btnNotifi.setOnClickListener {
                            if (btnNotifi.isChecked != check) {
                                viewModels.setParamsUpdate(
                                    UpdateNotificationParams(
                                        member.receiveNewsletterMail,
                                        getCheck(btnNotifi.isChecked),
                                        member.pushMail,
                                        member.pushOnline,
                                        member.pushCounseling,
                                        member.receiverNoticeMail,
                                    )
                                )
                                member.pushNewsletter = getCheck(btnNotifi.isChecked)
                                check = !check
                            }
                        }
                    }
                    notifiReceiveMess.apply {
                        tvStart.text = resources.getString(R.string.notifi_receive_mess)
                        btnNotifi.isChecked = member.pushMail == 1
                        var check = btnNotifi.isChecked
                        btnNotifi.setOnClickListener {
                            if (btnNotifi.isChecked != check) {
                                viewModels.setParamsUpdate(
                                    UpdateNotificationParams(
                                        member.receiveNewsletterMail,
                                        member.pushNewsletter,
                                        getCheck(btnNotifi.isChecked),
                                        member.pushOnline,
                                        member.pushCounseling,
                                        member.receiverNoticeMail
                                    )
                                )
                                member.pushMail = getCheck(btnNotifi.isChecked)
                                check = !check
                            }
                        }
                    }
                    notifiOnline.apply {
                        tvStart.text = resources.getString(R.string.notifi_online)
                        btnNotifi.isChecked = member.pushOnline == 1
                        divider.visibility = View.VISIBLE
                        var check = btnNotifi.isChecked
                        btnNotifi.setOnClickListener {
                            if (btnNotifi.isChecked != check) {
                                viewModels.setParamsUpdate(
                                    UpdateNotificationParams(
                                        member.receiveNewsletterMail,
                                        member.pushNewsletter,
                                        member.pushMail,
                                        getCheck(btnNotifi.isChecked),
                                        member.pushCounseling,
                                        member.receiverNoticeMail
                                    )
                                )
                                member.pushOnline = getCheck(btnNotifi.isChecked)
                                check = !check
                            }
                        }
                    }
                    notifiLab.apply {
                        tvStart.text = resources.getString(R.string.notifi_lab)
                        btnNotifi.isChecked = member.pushCounseling == 1
                        divider.visibility = View.VISIBLE
                        var check = btnNotifi.isChecked
                        btnNotifi.setOnClickListener {
                            if (btnNotifi.isChecked != check) {
                                viewModels.setParamsUpdate(
                                    UpdateNotificationParams(
                                        member.receiveNewsletterMail,
                                        member.pushNewsletter,
                                        member.pushMail,
                                        member.pushOnline,
                                        getCheck(btnNotifi.isChecked),
                                        member.receiverNoticeMail
                                    )
                                )
                                member.pushCounseling = getCheck(btnNotifi.isChecked)
                                check = !check
                            }
                        }
                    }
                    notifiReceiveEmail.apply {
                        tvStart.text = resources.getString(R.string.notifi_receive_email)
                        btnNotifi.isChecked = member.receiveNewsletterMail == 1
                        divider.visibility = View.GONE
                        var check = btnNotifi.isChecked
                        btnNotifi.setOnClickListener {
                            if (btnNotifi.isChecked != check) {
                                viewModels.setParamsUpdate(
                                    UpdateNotificationParams(
                                        getCheck(btnNotifi.isChecked),
                                        member.pushNewsletter,
                                        member.pushMail,
                                        member.pushOnline,
                                        member.pushCounseling,
                                        member.receiverNoticeMail
                                    )
                                )
                                member.receiveNewsletterMail = getCheck(btnNotifi.isChecked)
                                check = !check
                            }
                        }
                        container.visibility = if (mMember?.signupStatus == 2) GONE else VISIBLE
                    }
                    receiveNoticeEmail.apply {
                        tvStart.text = getString(R.string.receiver_notice_email)
                        btnNotifi.isChecked = member.receiverNoticeMail == 1
                        var check = btnNotifi.isChecked
                        btnNotifi.setOnClickListener {
                            if (btnNotifi.isChecked != check) {
                                viewModels.setParamsUpdate(
                                    UpdateNotificationParams(
                                        member.receiveNewsletterMail,
                                        member.pushNewsletter,
                                        member.pushMail,
                                        member.pushOnline,
                                        member.pushCounseling,
                                        getCheck(btnNotifi.isChecked)
                                    )
                                )
                                member.receiverNoticeMail = getCheck(btnNotifi.isChecked)
                                check = !check
                            }
                        }
                        container.visibility = if (mMember?.signupStatus == 2) GONE else VISIBLE
                    }
                }
            )

            viewModels.openDirect.observe(
                viewLifecycleOwner,
                {
                    if (it) myPageViewModels.forceRefresh()
                }
            )

            myPageViewModels.uiMember.observe(
                viewLifecycleOwner,
                {
                    viewModels.setSaveStateSwitchFromMemberResponse(it)
                }
            )
        }
    }

    private fun getCheck(check: Boolean): Int {
        return if (check) 1 else 0
    }
}

data class SaveStateSwitch(
    var receiveNewsletterMail: Int = 0,
    var pushNewsletter: Int = 0,
    var pushMail: Int = 0,
    var pushOnline: Int = 0,
    var pushCounseling: Int = 0,
    var receiverNoticeMail: Int = 0
)
