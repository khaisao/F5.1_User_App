package jp.careapp.counseling.android.ui.message

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
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
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.message.*
import jp.careapp.counseling.android.data.network.ConsultantResponse
import jp.careapp.counseling.android.data.network.FreeTemplateResponse
import jp.careapp.counseling.android.data.network.MemberResponse
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.data.shareData.ShareViewModel
import jp.careapp.counseling.android.handle.HandleBuyPoint
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.buy_point.BuyPointBottomFragment
import jp.careapp.counseling.android.ui.calling.CallingViewModel
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
import jp.careapp.counseling.android.utils.extensions.toPayLength
import jp.careapp.counseling.android.utils.extensions.toPayPoint
import jp.careapp.counseling.databinding.FragmentChatMessageBinding
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ChatMessageFragment : BaseFragment<FragmentChatMessageBinding, ChatMessageViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    @Inject
    lateinit var handleBuyPoint: HandleBuyPoint

    private val viewModel: ChatMessageViewModel by viewModels()
    private val shareViewModel: ShareViewModel by activityViewModels()
    private val callingViewModel: CallingViewModel by activityViewModels()

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

    val keyboardLayoutListener = OnGlobalLayoutListener {
        val r = Rect()
        binding.rootLayout.getWindowVisibleDisplayFrame(r)
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
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
        when (performerDetail?.status) {
            Define.USER_BAD -> {
                binding.userBadIv.visibility = VISIBLE
                binding.contentSendMessRl.visibility = GONE
            }
            Define.USER_WITH_DRAWAL -> {
                binding.userBadIv.visibility = VISIBLE
                binding.contentSendMessRl.visibility = GONE
            }
            else -> {
                binding.userBadIv.visibility = GONE
                binding.contentSendMessRl.visibility = VISIBLE
            }
        }

        if (performerDetail != null) {
            binding.apply {
                if (ConsultantResponse.isWaiting(performerDetail.callStatus,performerDetail.chatStatus)) {
                    tvStatus.setBackgroundResource(R.drawable.bg_performer_status_waiting)
                    tvStatus.text =
                        resources.getString(R.string.presence_status_waiting)
                } else if (ConsultantResponse.isLiveStream(performerDetail.callStatus,performerDetail.chatStatus)) {
                    tvStatus.setBackgroundResource(R.drawable.bg_performer_status_live_streaming)
                    tvStatus.text =
                        resources.getString(R.string.presence_status_live_streaming)
                } else if (ConsultantResponse.isPrivateLiveStream(performerDetail.callStatus,performerDetail.chatStatus)) {
                    tvStatus.setBackgroundResource(R.drawable.bg_performer_status_private_delivery)
                    tvStatus.text =
                        resources.getString(R.string.presence_status_private_delivery)
                } else {
                    tvStatus.setBackgroundResource(R.drawable.bg_performer_status_offline)
                    tvStatus.text =
                        resources.getString(R.string.presence_status_offline)
                }
                if (ConsultantResponse.isWaiting(performerDetail.callStatus,performerDetail.chatStatus) || ConsultantResponse.isLiveStream(
                        performerDetail.callStatus,performerDetail.chatStatus
                    )
                ) {
                    llWatchLiveStream.visibility = VISIBLE
                } else {
                    llWatchLiveStream.visibility = GONE
                }
                if (ConsultantResponse.isLiveStream(performerDetail.callStatus,performerDetail.chatStatus)) {
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
    }

    private fun checkPoint() {
        if ((rxPreferences.getPoint() > 1000)) {
            showDialogRequestBuyPoint()
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
            .setContent(R.string.msg_title_request_buy_point)
            .setTextPositiveButton(R.string.buy_point)
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

    private fun showDialogConfirmCall() {
        CommonAlertDialog.getInstanceCommonAlertdialog(requireContext())
            .showDialog()
            .setContent(R.string.msg_confirm_call)
            .setTextPositiveButton(R.string.confirm_call)
            .setTextNegativeButton(R.string.send_free_mess)
            .setOnPositivePressed {
                it.dismiss()
                if (callingViewModel.isCalling()) {
                    showDialogWarningDuringCall()
                } else {
                    openCalling()
                }
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
        Bundle().also {
            it.putString(BUNDLE_KEY.PERFORMER_NAME, consultant?.name ?: "")
            it.putString(BUNDLE_KEY.PERFORMER_CODE, consultant?.code ?: "")
            it.putString(BUNDLE_KEY.PERFORMER_IMAGE, consultant?.imageUrl ?: "")
        }.let {
            appNavigation.openToCalling(it)
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
        viewModel.isHasTransmissionMessage.observe(viewLifecycleOwner, transmissionMessageResult)
        viewModel.isEnoughMessageForReview.observe(viewLifecycleOwner, showReviewResult)
        viewModel.openPayMessageResult.observe(viewLifecycleOwner, openPayMessageResponse)
        viewModel.isEnableButtonSend.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.sendMessageIv.isEnabled = it
            }
        })
    }

    private var messageResultResponse: Observer<DataMessage> = Observer {
        if (!it.isLoadMore && it.dataMsg.isNullOrEmpty()) {
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
                activity?.let { createNewReview() }
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

    fun reloadData() {
        templateMessageBottom?.dismiss()
        activity?.let { viewModel.loadMessage(it, this.performerCode, false) }
    }


    private fun createNewReview() {
        val bundle = Bundle().apply {
            putString(BUNDLE_KEY.PERFORMER_CODE, performerDetail?.code)
            putString(BUNDLE_KEY.PERFORMER_NAME, performerDetail?.name)
            putString(BUNDLE_KEY.PERFORMER_IMAGE, performerDetail?.imageUrl)
        }
        appNavigation.openReviewConsultant(bundle)
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
    private lateinit var clickItemView: TemplateBottomFragment.ClickItemView
    fun setClickItemView(clickItemView: TemplateBottomFragment.ClickItemView) {
        this.clickItemView = clickItemView
    }
}
