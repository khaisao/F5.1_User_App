package jp.slapp.android.android.ui.message

import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.dialog.OnPositiveDialogListener
import jp.careapp.core.utils.loadImage
import jp.careapp.core.utils.setMargins
import jp.slapp.android.R
import jp.slapp.android.android.data.model.live_stream.ConnectResult
import jp.slapp.android.android.data.model.message.BaseMessageResponse
import jp.slapp.android.android.data.model.message.DataMessage
import jp.slapp.android.android.data.model.message.FreeTemplateRequest
import jp.slapp.android.android.data.model.message.MessageRequest
import jp.slapp.android.android.data.model.message.MessageResponse
import jp.slapp.android.android.data.model.message.SendMessageResponse
import jp.slapp.android.android.data.network.ConsultantResponse
import jp.slapp.android.android.data.network.FreeTemplateResponse
import jp.slapp.android.android.data.network.MemberResponse
import jp.slapp.android.android.data.network.socket.SocketActionSend
import jp.slapp.android.android.data.pref.RxPreferences
import jp.slapp.android.android.data.shareData.ShareViewModel
import jp.slapp.android.android.handle.HandleBuyPoint
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.ui.buy_point.bottom_sheet.BuyPointBottomFragment
import jp.slapp.android.android.ui.live_stream.CallConnectionDialog
import jp.slapp.android.android.ui.live_stream.LiveStreamBuyPointFragment
import jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.buy_point.PurchasePointBottomSheet
import jp.slapp.android.android.ui.message.ChatMessageViewModel.Companion.DISABLE_LOAD_MORE
import jp.slapp.android.android.ui.message.ChatMessageViewModel.Companion.ENABLE_LOAD_MORE
import jp.slapp.android.android.ui.message.ChatMessageViewModel.Companion.HIDDEN_LOAD_MORE
import jp.slapp.android.android.ui.message.dialog.OpenPaidMessDialog
import jp.slapp.android.android.ui.message.template.TemplateAdapter
import jp.slapp.android.android.ui.message.template.TemplateBottomFragment
import jp.slapp.android.android.utils.BUNDLE_KEY
import jp.slapp.android.android.utils.BUNDLE_KEY.Companion.CHAT_MESSAGE
import jp.slapp.android.android.utils.BUNDLE_KEY.Companion.THRESHOLD_SHOW_REVIEW_APP
import jp.slapp.android.android.utils.CallRestriction
import jp.slapp.android.android.utils.Define
import jp.slapp.android.android.utils.Define.Companion.BUY_POINT_CHAT_MESSAGE
import jp.slapp.android.android.utils.SocketInfo
import jp.slapp.android.android.utils.extensions.toPayLength
import jp.slapp.android.android.utils.extensions.toPayPoint
import jp.slapp.android.android.utils.performer_extension.PerformerStatus
import jp.slapp.android.android.utils.performer_extension.PerformerStatusHandler
import jp.slapp.android.databinding.FragmentChatMessageBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChatMessageFragment : BaseFragment<FragmentChatMessageBinding, ChatMessageViewModel>(),
    CallConnectionDialog.CallingCancelListener {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    @Inject
    lateinit var handleBuyPoint: HandleBuyPoint

    private val viewModel: ChatMessageViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()

    override val layoutId = R.layout.fragment_chat_message
    override fun getVM(): ChatMessageViewModel = viewModel

    // performer from chat list
    private var performerCode = ""
    private var performerName = ""
    private var isMessageFromServer = false
    private var pointPerchar = 0
    private var payMailCode = ""
    private var isSendFirstMsg = false
    
    private var sendMessageEnable = false
    private var consultantResponse: ConsultantResponse? = null
    private var isSendMessageSuccess = false
    private var reviewManager: ReviewManager? = null
    private val listMessage = mutableListOf<BaseMessageResponse>()

    // value check transition position adapter to top(value true) else bottom (value false)
    private var isLoadMore = false

    private var isSendFreeMessage = false

    private var isReviewEnable = false

    private var viewerType = 0

    private val mAdapter: ChatMessageAdapter by lazy {
        ChatMessageAdapter(requireContext(), onClickListener = ({ message, typeClick ->
            DeviceUtil.hideSoftKeyboard(activity)
            if (binding.contentMessageEdt.isFocused) {
                binding.contentMessageEdt.clearFocus()
            }
            if (typeClick == ChatMessageAdapter.CLICK_AVATAR && !isMessageFromServer) {
                consultantResponse?.let {
                    val bundle = Bundle()
                    bundle.putString(BUNDLE_KEY.SCREEN_TYPE, CHAT_MESSAGE)
                    bundle.putInt(BUNDLE_KEY.POSITION_SELECT, 0)
                    bundle.putSerializable(
                        BUNDLE_KEY.LIST_USER_PROFILE, ArrayList(listOf(it))
                    )
                    appNavigation.openChatMessageToUserProfile(bundle)
                }
            } else if (typeClick == ChatMessageAdapter.CLICK_PAY_MESSAGE) {
                showUnlockPayMessage(message)
            }
        }))
    }

    private val templateAdapter: TemplateAdapter by lazy {
        TemplateAdapter(
            requireContext(),
            onClickListener = { message ->
                CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                    .showDialog()
                    .setDialogTitle(R.string.template_title_dialog)
                    .setContent(message.body)
                    .setTextPositiveButton(R.string.ok)
                    .setTextNegativeButton(R.string.cancel_block_alert)
                    .setOnPositivePressed {
                        it.dismiss()
                        val code = this@ChatMessageFragment.performerCode
                        isSendFreeMessage = true
                        sendFreeTemplate(code, message.id)
                    }.setOnNegativePressed {
                        it.dismiss()
                    }
                    .tvSubTitle.visibility = VISIBLE
            }
        )
    }

    private val keyboardLayoutListener = OnGlobalLayoutListener {
        val r = Rect()
        binding.rootLayout.getWindowVisibleDisplayFrame(r)
        val screenHeight = binding.rootLayout.rootView.height
        val keypadHeight = screenHeight - r.bottom
        if (keypadHeight > screenHeight * 0.15) {
            binding.bottomBar.setMargins(
                0,
                0,
                0,
                0
            )
        } else {
            binding.bottomBar.setMargins(
                0,
                0,
                0,
                0
            )
        }
    }

    private var templateMessageBottom: TemplateBottomFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            viewModel.listenEventSocket()
            // handle load data message
            val bundle = arguments
            if (bundle != null) {
                performerCode = bundle.getString(BUNDLE_KEY.PERFORMER_CODE, "")
                performerName =
                    bundle.getString(
                        BUNDLE_KEY.PERFORMER_NAME,
                        resources.getString(R.string.notice_from_management)
                    )
            }
            isReviewEnable = false
            shareViewModel.setMessagePerformerCode(performerCode)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(false)
        }
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        loadData()
    }

    override fun onStart() {
        super.onStart()
        binding.rootLayout.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
        viewModel.loadMessage(requireActivity(), this.performerCode, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMemberInfo()
    }

    override fun initView() {
        super.initView()
        // point review when back from ReviewConsultantFragment
        reviewManager = activity?.let { ReviewManagerFactory.create(it) }
        handleBackFromReview()
        // init recycleview
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = false
        binding.messageRv.layoutManager = layoutManager
        binding.messageRv.itemAnimator = null
        binding.messageRv.adapter = mAdapter
        mAdapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    if (!isLoadMore) {
                        layoutManager.scrollToPosition(mAdapter.itemCount - 1)
                    }
                }
            }
        )

        mAdapter.setLoadMorelistener(
            object : BaseMessageAdapterLoadMore.LoadMorelistener {
                override fun onLoadMore() {
                    activity?.let {
                        mAdapter.setIsLoading(true)
                        viewModel.loadMessage(it, performerCode, true)
                    }
                }
            }
        )
        binding.messageRv.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom && mAdapter.itemCount > 0) {
                binding.messageRv.scrollToPosition(mAdapter.itemCount - 1)
            }
        }

        arguments?.apply {
            if (containsKey(BUNDLE_KEY.IS_SHOW_FREE_MESS) && getBoolean(BUNDLE_KEY.IS_SHOW_FREE_MESS)) {
                remove(BUNDLE_KEY.IS_SHOW_FREE_MESS)
            }
        }
        
        binding.rvMessageTemplate.adapter = templateAdapter

        if(performerCode == ""){
            binding.tvStatus.visibility = GONE
            binding.tvName.text = requireContext().resources.getString(R.string.notice_from_management)
            binding.bottomBar.visibility = GONE
            binding.rvMessageTemplate.visibility = GONE
        }
    }

    private fun handleBackFromReview() {
        appNavigation.navController?.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(
            BUNDLE_KEY.POINT_REVIEW
        )?.observe(viewLifecycleOwner) { data ->
            if (data != -1) {
                CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
                    .showDialog()
                    .setDialogTitle(R.string.review_title_submit_ok)
                    .setContent(R.string.review_content_submit_ok)
                    .setTextPositiveButton(R.string.text_OK)
                    .setOnPositivePressed {
                        if (data > THRESHOLD_SHOW_REVIEW_APP) {
                            showRateApp()
                        }
                        it.dismiss()
                    }
            }
            appNavigation.navController?.currentBackStackEntry?.savedStateHandle?.remove<Int>(
                BUNDLE_KEY.POINT_REVIEW
            )
        }
    }

    private fun showRateApp() {
        val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo: ReviewInfo = task.result
                activity?.let { reviewManager!!.launchReviewFlow(it, reviewInfo) } as Task<Void>
            }
        }
    }

    private fun showUnlockPayMessage(message: MessageResponse) {
        payMailCode = message.code
        OpenPaidMessDialog.newInstance(
            message.body.toPayLength(),
            message.body.toPayPoint(pointPerchar),
            object : OnPositiveDialogListener {
                override fun onClick() {
                    activity?.let {
                        viewModel.openPayMessage(it, message.code)
                    }
                }
            }
        ).show(childFragmentManager, OpenPaidMessDialog.TAG)
    }

    private fun setViewPerformer(performerDetail: ConsultantResponse?) {
        this.consultantResponse = performerDetail
        this.pointPerchar = performerDetail?.pointPerChar ?: 0
        mAdapter.setPointPerChar(this.pointPerchar)

        if (performerDetail != null) {
            binding.apply {

                val status = PerformerStatusHandler.getStatus(performerDetail.callStatus,performerDetail.chatStatus)

                val statusText = PerformerStatusHandler.getStatusText(status, requireContext().resources)

                val statusBg = PerformerStatusHandler.getStatusBg(status)

                tvStatus.text = statusText

                tvStatus.setBackgroundResource(statusBg)

                if (status == PerformerStatus.WAITING || status == PerformerStatus.LIVE_STREAM) {
                    llCallConsult.visibility = VISIBLE
                } else {
                    llCallConsult.visibility = GONE
                }
                if (status == PerformerStatus.LIVE_STREAM) {
                    llPeep.visibility = VISIBLE
                } else {
                    llPeep.visibility = GONE
                }
                tvName.text = performerDetail.name
                ivAvtBackground.loadImage(
                    performerDetail.imageUrl,
                    R.drawable.default_avt_performer
                )
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        // send message
        binding.sendMessageIv.setOnClickListener {
            if (!isDoubleClick && sendMessageEnable) {
                if (rxPreferences.getPoint() >= (consultantResponse?.pointSetting?.mail ?: 0)) {
                    val code = this.performerCode
                    val message = binding.contentMessageEdt.text.toString().trim()
                    sendMessage(code, message, "")
                } else {
                    showPointPurchaseBottomSheet(BUY_POINT_CHAT_MESSAGE)
                }
                binding.contentMessageEdt.clearFocus()
            }
        }

        // handle text input change
        binding.contentMessageEdt.addTextChangedListener {
            if (TextUtils.isEmpty(it?.toString()?.trim() ?: "")) {
                binding.sendMessageIv.setImageResource(R.drawable.ic_message_inactive)
                sendMessageEnable = false
            } else {
                binding.sendMessageIv.setImageResource(R.drawable.ic_message_send_active)
                sendMessageEnable = true
            }
        }

        binding.messageRv.setOnClickListener {
            DeviceUtil.hideSoftKeyboard(activity)
            if (binding.contentMessageEdt.isFocused) {
                binding.contentMessageEdt.clearFocus()
            }
        }

        binding.ivBack.setOnClickListener {
            appNavigation.navigateUp()
        }

        binding.llCallConsult.setOnClickListener {
            if (!isDoubleClick) {
                if (viewModel.userProfileResult.value?.isBlocked == true) {
                    showDialogBlockedByPerformer()
                } else {
                    checkPointForNormal()
                    viewerType = 0
                    viewModel.viewerStatus = 0
                }
            }
        }

        binding.llPeep.setOnClickListener {
            if (!isDoubleClick) {
                if (viewModel.userProfileResult.value?.isBlocked == true) {
                    showDialogBlockedByPerformer()
                } else {
                    viewerType = 1
                    viewModel.viewerStatus = 1
                    checkPointForPeep()
                }
            }
        }
    }

    private fun showPointPurchaseBottomSheet(typeBuyPoint: Int) {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_KEY.TYPE_BUY_POINT, typeBuyPoint)
        handleBuyPoint.buyPointLiveStream(
            childFragmentManager, bundle,
            object : PurchasePointBottomSheet.PurchasePointCallback {
                override fun onPointItemClick(point: Int, money: Int) {
                    val purchasePointUrl = buildString {
                        append(Define.URL_LIVE_STREAM_POINT_PURCHASE)
                        append("?token=${rxPreferences.getToken()}")
                        append("&&point=${point}")
                        append("&money=${money}")
                    }
                    val buyPointScreen = LiveStreamBuyPointFragment.newInstance(purchasePointUrl)
                    buyPointScreen.isCancelable = false
                    buyPointScreen.show(childFragmentManager, "LiveStreamBuyPointFragment")
                }
            }
        )
    }

    private fun showDialogBlockedByPerformer() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.blocked_by_performer)
            .setTextOkButton(R.string.confirm_block_alert)
            .setOnOkButtonPressed {
                it.dismiss()
            }
    }

    private fun checkPointForNormal() {
        if (consultantResponse != null) {
            if (rxPreferences.getPoint() < (consultantResponse!!.pointSetting?.normalChatPerMinute
                    ?: Int.MAX_VALUE)
            ) {
                showDialogRequestBuyPoint()
            } else {
                showDialogConfirmCall()
            }
        }
    }

    private fun checkPointForPeep() {
        if (consultantResponse != null) {
            if (rxPreferences.getPoint() < (consultantResponse!!.pointSetting?.peepingPerMinute
                    ?: Int.MAX_VALUE)
            ) {
                showDialogRequestBuyPoint()
            } else {
                showDialogConfirmCall()
            }
        }
    }

    private fun showDialogRequestBuyPoint() {
        CommonAlertDialog.getInstanceCommonAlertdialog(
            requireContext()
        )
            .showDialog()
            .setDialogTitle(R.string.error_not_enough_point)
            .setTextNegativeButton(R.string.text_OK)
            .setTextPositiveButton(R.string.buy_point)
            .setOnNegativePressed { dialog ->
                dialog.dismiss()
            }.setOnPositivePressed { dialog ->
                dialog.dismiss()
                handleBuyPoint.buyPoint(childFragmentManager, bundleOf(),
                    object : BuyPointBottomFragment.HandleBuyPoint {
                        override fun buyPointSucess() {
                        }
                    }
                )
            }
    }

    private fun showDialogConfirmCall() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(
                getString(
                    R.string.content_confirm_call,
                    viewModel.getCurrentConsultant()?.name
                )
            )
            .setTextPositiveButton(R.string.confirm_block_alert)
            .setTextNegativeButton(R.string.cancel_block_alert)
            .setOnPositivePressed {
                it.dismiss()
                openCalling()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun openCalling() {
        val consultant = viewModel.getCurrentConsultant()
        consultant?.let { performer ->
            val status =
                PerformerStatusHandler.getStatus(performer.callStatus, performer.chatStatus)
            performer.code?.let {
                viewModel.connectLiveStream(it, status)
            }
        }
    }

    private fun sendFreeTemplate(code: String, templateId: Int) {
        activity?.let {
            viewModel.sendFreeTemplate(FreeTemplateRequest(code, templateId), it)
        }
    }

    private fun sendMessage(
        code: String,
        message: String,
        subject: String
    ) {
        activity?.let { it1 ->
            viewModel.sendMessage(
                MessageRequest(code, subject, message, ""),
                it1
            )
        }
    }

    private fun loadData() {
        activity?.let { viewModel.loadDetailUser(it, performerCode) }
        viewModel.loadMemberInfo()
    }

    private fun setButtonEnable(isEnable: Boolean) {
        binding.llCallConsult.isEnabled = isEnable
        binding.llPeep.isEnabled = isEnable
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.messageResult.observe(viewLifecycleOwner, messageResultResponse)
        viewModel.sendMessageResult.observe(viewLifecycleOwner, sendMessageResponse)
        viewModel.userProfileResult.observe(viewLifecycleOwner, userResultResponse)
        viewModel.hiddenLoadMoreHandle.observe(viewLifecycleOwner, hiddenLoadMoreResult)
        viewModel.responseSocket.observe(viewLifecycleOwner, responseSocketResult)
        viewModel.memberInFoResult.observe(viewLifecycleOwner, handleMemberInfoResult)
        viewModel.isCloseFirstMessageInLocal.observe(
            viewLifecycleOwner,
            closeFirstMessagePerformerInLocal
        )
        viewModel.isHasTransmissionMessage.observe(
            viewLifecycleOwner,
            transmissionMessageResult
        )
        viewModel.isEnoughMessageForReview.observe(viewLifecycleOwner, showReviewResult)
        viewModel.openPayMessageResult.observe(viewLifecycleOwner, openPayMessageResponse)
        viewModel.isEnableButtonSend.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.sendMessageIv.isEnabled = it
            }
        })
        viewModel.connectResult.observe(viewLifecycleOwner, connectResultHandle)
        viewModel.isButtonEnable.observe(viewLifecycleOwner, buttonEnableHandle)
        viewModel.isLoginSuccess.observe(viewLifecycleOwner, loginSuccessHandle)
        viewModel.freeTemplateResponse.observe(viewLifecycleOwner, freeTemplateResponseHandler)
    }

    private val freeTemplateResponseHandler: Observer<List<FreeTemplateResponse>> =
        Observer { listFreeTemplate ->
            templateAdapter.submitList(listFreeTemplate)
        }

    private val connectResultHandle: Observer<ConnectResult> = Observer {
        if (it.result != SocketInfo.RESULT_NONE) {
            viewModel.getCurrentConsultant()?.let { performerResponse ->
                run {
                    val fragment: Fragment? =
                        childFragmentManager.findFragmentByTag("CallConnectionDialog")
                    val dialog: CallConnectionDialog
                    if (fragment != null) {
                        dialog = fragment as CallConnectionDialog
                        dialog.setCallingCancelListener(this@ChatMessageFragment)
                        if (it.result == SocketInfo.RESULT_NG) {
                            dialog.setMessage(it.message, true)
                        } else {
                            dialog.setMessage(getString(R.string.call_content))
                        }
                    } else {
                        val message =
                            if (it.result == SocketInfo.RESULT_NG) it.message else getString(R.string.call_content)
                        val isError = it.result == SocketInfo.RESULT_NG
                        dialog =
                            CallConnectionDialog.newInstance(performerResponse, message, isError)
                        dialog.setCallingCancelListener(this@ChatMessageFragment)
                        dialog.show(childFragmentManager, "CallConnectionDialog")
                    }
                }
            }
        }
    }

    private val loginSuccessHandle: Observer<Boolean> = Observer {
        if (it) {
            val fragment: Fragment? = childFragmentManager.findFragmentByTag("CallConnectionDialog")
            if (fragment != null) (fragment as CallConnectionDialog).dismiss()
            val bundle = Bundle().apply {
                putSerializable(BUNDLE_KEY.FLAX_LOGIN_AUTH_RESPONSE, viewModel.flaxLoginAuthResponse)
                putSerializable(BUNDLE_KEY.USER_PROFILE, viewModel.getCurrentConsultant())
                putInt(BUNDLE_KEY.VIEW_STATUS, viewerType)
                putInt(BUNDLE_KEY.ROOT_SCREEN, BUNDLE_KEY.SCREEN_DETAIL)
            }
            appNavigation.openChatMessageToLivestreamScreen(bundle)
            viewModel.resetData()
        }
    }

    private val buttonEnableHandle: Observer<Boolean> = Observer {
        setButtonEnable(it)
    }

    private var messageResultResponse: Observer<DataMessage> = Observer {
        if (!it.isLoadMore && it.dataMsg.isEmpty()) {
//            if empty message
        } else {
            val firstPerformer =
                it.dataMsg.filter { data -> data.typeMessage == 2 }[0] as MessageResponse
            isMessageFromServer = firstPerformer.fromOwnerMail
            if (!isMessageFromServer)
                binding.bottomBar.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            isLoadMore = it.isLoadMore

            listMessage.apply {
                clear()
                addAll(it.dataMsg)
            }

            if (isLoadMore) {
                mAdapter.submitList(listMessage)
                binding.messageRv.scrollToPosition(listMessage.size)
            } else {
                when {
                    viewModel.isLoadMessageAfterSend -> {
                        if (!isSendFirstMsg) {
                            mAdapter.submitList(
                                listMessage
                            )
                        } else {
                            mAdapter.submitList(listMessage)
                            isSendFirstMsg = false
                        }
                        viewModel.isLoadMessageAfterSend = false
                    }

                    viewModel.isMessageFromSocket -> {
                        mAdapter.submitList(listMessage)
                        viewModel.isMessageFromSocket = false
                    }

                    else -> {
                        mAdapter.submitList(listMessage)
                    }
                }
                if (mAdapter.itemCount > 0) {
                    binding.messageRv.scrollToPosition(mAdapter.itemCount - 1)
                }

            }
        }
    }

    private var sendMessageResponse: Observer<SendMessageResponse?> = Observer { data ->
        data?.let {
            if (isSendFreeMessage) {
                templateMessageBottom?.dismiss()
                isSendFreeMessage = false
            } else {
                binding.contentMessageEdt.setText("")
            }
            rxPreferences.setPoint(it.point)
            activity?.let { it1 ->
                viewModel.loadMessageAfterSend(
                    it1,
                    this.performerCode
                )
            }
            viewModel.sendMessageResult.value = null
            this.isSendMessageSuccess = true
        }
    }

    private var openPayMessageResponse: Observer<SendMessageResponse> = Observer { data ->
        data?.let {
            rxPreferences.setPoint(it.point)
            viewModel.openPayMessageResult.value = null
            mAdapter.openPayMessage(payMailCode)
        }
    }

    private var userResultResponse: Observer<ConsultantResponse> = Observer {
        setViewPerformer(it)
    }

    private var hiddenLoadMoreResult: Observer<Int> = Observer {
        when (it) {
            DISABLE_LOAD_MORE -> {
                mAdapter.setDisableLoadmore(true)
            }
            HIDDEN_LOAD_MORE -> {
                mAdapter.setIsLoading(false)
            }
            ENABLE_LOAD_MORE -> {
                mAdapter.setDisableLoadmore(false)
            }
        }
    }

    private var responseSocketResult: Observer<SocketActionSend> = Observer {
        if (it.senderCode == performerCode && !mAdapter.checkContainMsg(it.mailCode)) {
            activity?.let { viewModel.loadMessage(it, performerCode, false, false) }
        }
    }

    private var handleMemberInfoResult: Observer<MemberResponse?> = Observer {
        it?.let { data ->
            shareViewModel.getCreditPrices(data.firstBuyCredit)
            rxPreferences.saveMemberInfo(data)
        }
    }

    /**
     * if is first message of performer hardcode local befor send first message then remove
     */
    private var closeFirstMessagePerformerInLocal: Observer<Boolean> = Observer {
        if (it) {
            mAdapter.submitList(mutableListOf())
            viewModel.isCloseFirstMessageInLocal.value = false
        }
    }

    private var transmissionMessageResult: Observer<Boolean> = Observer {
        if (it && !isReviewEnable) {
            isReviewEnable = true
            if (!rxPreferences.isFirstReview(rxPreferences.getMemberCode(), performerCode)) {
                rxPreferences.saveFirstReview(true, rxPreferences.getMemberCode(), performerCode)
            }
        }
    }

    private var showReviewResult: Observer<Boolean> = Observer {
        if (it) {
            activity?.let { viewModel.loadTransmissionMessage(it, this.performerCode, false) }
        }
    }

    override fun onStop() {
        binding.rootLayout.viewTreeObserver
            .removeOnGlobalLayoutListener(keyboardLayoutListener)
        super.onStop()
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(true)
        }
    }

    override fun onDestroy() {
        activity?.let {
            viewModel.closeListenEvent()
            shareViewModel.setMessagePerformerCode("")
        }
        super.onDestroy()
    }

    override fun callingCancel(isError: Boolean) {
        viewModel.cancelCall(isError)
        lifecycleScope.launch {
            delay(500)
            viewModel.loadMessage(requireActivity(), performerCode, false)
        }
    }

    fun reloadData() {
        templateMessageBottom?.dismiss()
        activity?.let { viewModel.loadMessage(it, this.performerCode, false) }
    }
}
