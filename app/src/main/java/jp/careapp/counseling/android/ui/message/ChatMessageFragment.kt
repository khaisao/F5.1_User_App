package jp.careapp.counseling.android.ui.message

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.live_stream.ConnectResult
import jp.careapp.counseling.android.data.model.message.*
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.FreeTemplateResponse
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.handle.HandleBuyPoint
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.buy_point.bottom_sheet.BuyPointBottomFragment
import jp.careapp.counseling.android.ui.calling.CallConnectionDialog
import jp.careapp.counseling.android.ui.message.ChatMessageViewModel.Companion.DISABLE_LOAD_MORE
import jp.careapp.counseling.android.ui.message.ChatMessageViewModel.Companion.ENABLE_LOAD_MORE
import jp.careapp.counseling.android.ui.message.ChatMessageViewModel.Companion.HIDDEN_LOAD_MORE
import jp.careapp.counseling.android.ui.message.dialog.OpenPaidMessDialog
import jp.careapp.counseling.android.ui.message.template.TemplateAdapter
import jp.careapp.counseling.android.ui.message.template.TemplateBottomFragment
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.CHAT_MESSAGE
import jp.careapp.counseling.android.utils.BUNDLE_KEY.Companion.THRESHOLD_SHOW_REVIEW_APP
import jp.careapp.counseling.android.utils.CallRestriction
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.android.utils.SocketInfo
import jp.careapp.counseling.android.utils.extensions.hasPermissions
import jp.careapp.counseling.android.utils.extensions.toPayLength
import jp.careapp.counseling.android.utils.extensions.toPayPoint
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatus
import jp.careapp.counseling.android.utils.performer_extension.PerformerStatusHandler
import jp.careapp.counseling.databinding.FragmentChatMessageBinding
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

    private val listFreeTemplate: List<FreeTemplateResponse>? by lazy {
        rxPreferences.getListTemplate()
    }

    private var sendMessageEnable = false
    private var countPointMessage = 0
    private var performerDetail: ConsultantResponse? = null
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
                performerDetail?.let {
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkPoint()
        }
    }

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
            activity?.let { viewModel.loadMessage(it, this.performerCode, false) }
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

        handleShowDialogPointFree()

        arguments?.apply {
            if (containsKey(BUNDLE_KEY.IS_SHOW_FREE_MESS) && getBoolean(BUNDLE_KEY.IS_SHOW_FREE_MESS)) {
                remove(BUNDLE_KEY.IS_SHOW_FREE_MESS)
            }
        }
        val callRestriction = arguments?.getInt(BUNDLE_KEY.CALL_RESTRICTION)
        val listTemplate = if (callRestriction == CallRestriction.POSSIBLE) {
            listFreeTemplate
        } else {
            listFreeTemplate?.filterNot { it.body == getString(R.string.free_message_consult_call) }
        }
        binding.rvMessageTemplate.adapter = templateAdapter
        templateAdapter.submitList(listTemplate)

        if(performerCode == ""){
            binding.tvStatus.visibility = GONE
            binding.tvName.text = requireContext().resources.getString(R.string.notice_from_management)
            binding.bottomBar.visibility = GONE
            binding.rvMessageTemplate.visibility = GONE
        }
    }

    private fun handleShowDialogPointFree() {
        if (rxPreferences.getTimeBuy() == 0L && rxPreferences.getPoint() == 1000)
            showDialogPointFree(requireActivity())
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
        this.performerDetail = performerDetail
        this.pointPerchar = performerDetail?.pointPerChar ?: 0
        mAdapter.setPointPerChar(this.pointPerchar)
        formatCharactorToPoint(binding.contentMessageEdt.text.toString().length, this.pointPerchar)

        if (performerDetail != null) {
            binding.apply {

                val status = PerformerStatusHandler.getStatus(performerDetail.callStatus,performerDetail.chatStatus)

                val statusText = PerformerStatusHandler.getStatusText(status, requireContext().resources)

                val statusBg = PerformerStatusHandler.getStatusBg(status)

                tvStatus.text = statusText

                tvStatus.setBackgroundResource(statusBg)

                if (status == PerformerStatus.WAITING || status == PerformerStatus.LIVE_STREAM) {
                    llWatchLiveStream.visibility = VISIBLE
                } else {
                    llWatchLiveStream.visibility = GONE
                }
                if (status == PerformerStatus.LIVE_STREAM) {
                    llPeep.visibility = VISIBLE
                } else {
                    llPeep.visibility = GONE
                }
                tvName.text = performerDetail.name
                ivAvtBackground.loadImage(performerDetail.thumbnailImageUrl)
            }
        }
    }

    override fun setOnClick() {
        super.setOnClick()
        // send message
        binding.sendMessageIv.setOnClickListener {
            if (!isDoubleClick && sendMessageEnable) {
                if (countPointMessage <= rxPreferences.getPoint()) {
                    val code = this.performerCode
                    val message = binding.contentMessageEdt.text.toString().trim()
                    sendMessage(code, message, "")
                } else {
                    handleBuyPoint.buyPoint(childFragmentManager, isClickSendMessage = true)
                }
                binding.contentMessageEdt.clearFocus()
            }
        }

        // handle text input change
        binding.contentMessageEdt.addTextChangedListener {
            val countCharactor =
                it.toString().trim().replace("\n", "").length
            formatCharactorToPoint(countCharactor, pointPerchar)
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

            binding.llWatchLiveStream.setOnClickListener {
                if (!isDoubleClick) {
                    when {
                        hasPermissions(arrayOf(Manifest.permission.RECORD_AUDIO)) -> {
                            checkPoint()
                        }
                        shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                            showDialogNeedMicrophonePermission()
                        }
                        else -> {
                            showDialogRequestMicrophonePermission()
                        }
                    }
                    viewerType = 0
                    viewModel.viewerStatus = 0
                }
            }

            binding.llPeep.setOnClickListener {
                if (!isDoubleClick) {
                    viewerType = 1
                    viewModel.viewerStatus = 1
                    checkPointForPeep()
                }
            }
        }
    }

    private fun checkPoint() {
        if ((rxPreferences.getPoint() > 1000)) {
            showDialogRequestBuyPoint()
        } else {
            showDialogConfirmCall()
        }
    }

    private fun checkPointForPeep() {
        if ((rxPreferences.getPoint() == 0)) {
            showDialogRequestBuyPointForPeep()
        } else {
            showDialogConfirmCall()
        }
    }

    private fun showDialogRequestMicrophonePermission() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.msg_title_request_mic)
            .setContent(R.string.msg_explain_request_mic)
            .setTextPositiveButton(R.string.accept_permission)
            .setTextNegativeButton(R.string.denied_permission)
            .setOnPositivePressed {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                it.dismiss()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun showDialogNeedMicrophonePermission() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setContent(R.string.msg_need_mic_permission)
            .setTextPositiveButton(R.string.setting)
            .setTextNegativeButton(R.string.cancel)
            .setOnPositivePressed {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context?.packageName, null)
                })
                it.dismiss()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun showDialogRequestBuyPoint() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setContent(R.string.live_stream_title_request_buy_point)
            .setTextPositiveButton(R.string.live_stream_buy_point)
            .setTextNegativeButton(R.string.cancel_block_alert)
            .setOnPositivePressed { dialog ->
                val bundle = Bundle().also {
                    it.putInt(BUNDLE_KEY.TYPE_BUY_POINT, Define.BUY_POINT_FIRST)
                }
                handleBuyPoint.buyPoint(childFragmentManager, bundle,
                    object : BuyPointBottomFragment.HandleBuyPoint {
                        override fun buyPointSucess() {
                            checkPoint()
                        }
                    }
                )
                dialog.dismiss()
            }.setOnNegativePressed {
                it.dismiss()
            }
    }

    private fun showDialogRequestBuyPointForPeep() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setContent(R.string.live_stream_peep_title_request_buy_point)
            .setTextPositiveButton(R.string.live_stream_buy_point)
            .setTextNegativeButton(R.string.cancel_block_alert)
            .setOnPositivePressed { dialog ->
                val bundle = Bundle().also {
                    it.putInt(BUNDLE_KEY.TYPE_BUY_POINT, Define.BUY_POINT_FIRST)
                }
                handleBuyPoint.buyPoint(childFragmentManager, bundle,
                    object : BuyPointBottomFragment.HandleBuyPoint {
                        override fun buyPointSucess() {
                            checkPointForPeep()
                        }
                    }
                )
                dialog.dismiss()
            }.setOnNegativePressed {
                it.dismiss()
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

    private fun showDialogWarningDuringCall() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setDialogTitle(R.string.msg_warning_during_call)
            .setTextPositiveButton(R.string.text_OK)
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

    private fun formatCharactorToPoint(countCharactor: Int, pointPerchar: Int) {
        if (countCharactor == 0) {
            //TODO (Handler logic count charactor equal 0)
        } else {
            countPointMessage = (pointPerchar) * (((countCharactor - 1) / 10) + 1)
        }
    }

    private fun loadData() {
        activity?.let { viewModel.loadDetailUser(it, performerCode) }
        viewModel.loadMemberInfo()
    }

    private fun setButtonEnable(isEnable: Boolean) {
        binding.llWatchLiveStream.isEnabled = isEnable
        binding.llPeep.isEnabled = isEnable
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.messageResult.observe(viewLifecycleOwner, messageResultResponse)
        viewModel.sendMessageResult.observe(viewLifecycleOwner, sendMessageResponse)
        viewModel.subtractPoint.observe(viewLifecycleOwner, subtractPointResponse)
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
    }

    private val connectResultHandle: Observer<ConnectResult> = Observer {
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

    private val loginSuccessHandle: Observer<Boolean> = Observer {
        if (it) {
            val fragment: Fragment? =
                childFragmentManager.findFragmentByTag("CallConnectionDialog")
            if (fragment != null) (fragment as CallConnectionDialog).dismiss()
            val bundle = Bundle().apply {
                putSerializable(
                    BUNDLE_KEY.FLAX_LOGIN_AUTH_RESPONSE,
                    viewModel.flaxLoginAuthResponse
                )
                viewModel.getCurrentConsultant()?.let { userProfile ->
                    putSerializable(BUNDLE_KEY.USER_PROFILE, userProfile)
                }
                putInt(BUNDLE_KEY.VIEW_STATUS, viewerType)
            }
            appNavigation.openUserDetailToLiveStream(bundle)
            viewModel.resetData()
        }
    }

    private val buttonEnableHandle: Observer<Boolean> = Observer {
        setButtonEnable(it)
    }

    private var messageResultResponse: Observer<DataMessage> = Observer {
        if (!it.isLoadMore && it.dataMsg.isEmpty()) {
            mAdapter.submitList(
                listOf(MessageResponse(body = getString(R.string.message_default_performer)))
            )
            activity?.let { it1 ->
                isSendFirstMsg = true
                viewModel.sendFirstMessage(
                    MessageRequest(
                        this.performerCode,
                        getString(R.string.subject_message_first),
                        getString(R.string.message_default_member),
                        ""
                    ),
                    it1
                )
            }
        } else {
            val lastItemCount = mAdapter.itemCount
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
                mAdapter.notifyItemRangeInserted(0, listMessage.size - lastItemCount)
                binding.messageRv.scrollToPosition(listMessage.size - lastItemCount)
            } else {
                when {
                    viewModel.isLoadMessageAfterSend -> {
                        if (!isSendFirstMsg) {
                            mAdapter.notifyItemRangeInserted(
                                lastItemCount,
                                listMessage.size - lastItemCount
                            )
                        } else {
                            mAdapter.submitList(listMessage)
                            isSendFirstMsg = false
                        }
                        viewModel.isLoadMessageAfterSend = false
                    }
                    viewModel.isMessageFromSocket -> {
                        mAdapter.notifyDataSetChanged()
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

    private var subtractPointResponse: Observer<Boolean> = Observer {
        if (it) {
            handleShowDialogPointFree()
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
    }

    fun reloadData() {
        templateMessageBottom?.dismiss()
        activity?.let { viewModel.loadMessage(it, this.performerCode, false) }
    }

    private fun showDialogPointFree(context: Context) {
        val view = inflate(context, R.layout.dialog_recommend_use_point_free, null)
        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isOutsideTouchable = true
    }

}
