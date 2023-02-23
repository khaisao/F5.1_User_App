package jp.careapp.counseling.android.ui.review_mode.user_detail_message

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import jp.careapp.core.base.BaseActivity
import jp.careapp.core.base.BaseFragment
import jp.careapp.core.utils.DeviceUtil
import jp.careapp.core.utils.convertSourceToPixel
import jp.careapp.core.utils.dialog.CommonAlertDialog
import jp.careapp.core.utils.onTextChange
import jp.careapp.core.utils.setMargins
import jp.careapp.counseling.R
import jp.careapp.counseling.android.data.model.message.BaseMessageResponse
import jp.careapp.counseling.android.data.model.message.DataMessage
import jp.careapp.counseling.android.data.model.message.RMMessageRequest
import jp.careapp.counseling.android.data.model.message.RMMessageResponse
import jp.careapp.counseling.android.data.network.socket.SocketActionRead
import jp.careapp.counseling.android.data.network.socket.SocketActionSend
import jp.careapp.counseling.android.data.pref.RxPreferences
import jp.careapp.counseling.android.model.network.RMConsultantResponse
import jp.careapp.counseling.android.navigation.AppNavigation
import jp.careapp.counseling.android.ui.review_mode.top.RMTopViewModel
import jp.careapp.counseling.android.ui.review_mode.user_detail_message.RMUserDetailMsgAdapter.Companion.MESSAGE_PERFORMER
import jp.careapp.counseling.android.ui.review_mode.user_detail_message.UserDetailMsgViewModel.Companion.DISABLE_LOAD_MORE
import jp.careapp.counseling.android.ui.review_mode.user_detail_message.UserDetailMsgViewModel.Companion.ENABLE_LOAD_MORE
import jp.careapp.counseling.android.ui.review_mode.user_detail_message.UserDetailMsgViewModel.Companion.HIDDEN_LOAD_MORE
import jp.careapp.counseling.android.utils.ActionState
import jp.careapp.counseling.android.utils.BUNDLE_KEY
import jp.careapp.counseling.android.utils.Define
import jp.careapp.counseling.databinding.FragmentRmUserDetailMessageBinding
import jp.careapp.counseling.databinding.LlUserDetailBottomSheetBinding
import javax.inject.Inject

@AndroidEntryPoint
class UserDetailMsgFragment :
    BaseFragment<FragmentRmUserDetailMessageBinding, UserDetailMsgViewModel>() {

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var rxPreferences: RxPreferences

    private val viewModel: UserDetailMsgViewModel by viewModels()
    override fun getVM(): UserDetailMsgViewModel = viewModel
    private val rmTopViewModel: RMTopViewModel by activityViewModels()

    override val layoutId = R.layout.fragment_rm_user_detail_message

    private var performerCode = ""
    private var performerName = ""

    private var isMessageFromServer = false
    private var isEnableSend = false

    private val listMessage = mutableListOf<BaseMessageResponse>()
    private var performerDetail: RMConsultantResponse? = null

    private var messageAreaTouchX: Float = 0f
    private var messageAreaTouchY: Float = 0f

    private var isLoadMore = false

    private val mAdapter: RMUserDetailMsgAdapter by lazy {
        RMUserDetailMsgAdapter(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            viewModel.listenEventSocket()

            arguments?.apply {
                performerCode = getString(BUNDLE_KEY.PERFORMER_CODE, "")
                performerName = getString(BUNDLE_KEY.PERFORMER_NAME, "")
            }

            activity?.let {
                viewModel.loadMessage(
                    it,
                    performerCode,
                    false,
                    needLoadUserInfo = true
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(false)
        }
        activity?.let {
            DeviceUtil.setupUI(
                view,
                listOf(binding.ivSend, binding.rcvMessage, binding.scrollView),
                it
            )
        }
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding.root.setOnApplyWindowInsetsListener { _, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                binding.root.setPadding(0, 0, 0, imeHeight)
                windowInsets.getInsets(WindowInsets.Type.ime() or WindowInsets.Type.systemGestures())
            }
            windowInsets
        }
    }

    override fun onStart() {
        super.onStart()
        binding.rootLayout.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
    }

    override fun initView() {
        super.initView()

        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = false

        binding.rcvMessage.apply {
            this.layoutManager = layoutManager
            itemAnimator = null

            adapter = mAdapter

            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom && mAdapter.itemCount > 0) {
                    binding.rcvMessage.smoothScrollToPosition(mAdapter.itemCount - 1)
                }
            }
        }
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
            object : UserDetailMsgAdapterLoadMore.LoadMorelistener {
                override fun onLoadMore() {
                    activity?.let {
                        mAdapter.setIsLoading(true)
                        viewModel.loadMessage(it, performerCode, true)
                    }
                }
            }
        )
    }

    private fun setViewPerformer(performerDetail: RMConsultantResponse?) {
        performerDetail?.name?.let { binding.toolBar.titleBackTv.text = it }
        this.performerDetail = performerDetail

        when (performerDetail?.status) {
            Define.USER_BAD -> {
                binding.bottomBar.visibility = GONE
            }
            Define.USER_WITH_DRAWAL -> {
                binding.bottomBar.visibility = GONE
            }
            else -> binding.bottomBar.visibility = VISIBLE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnClick() {
        super.setOnClick()

        binding.apply {
            toolBar.apply {
                btnCamera.setOnClickListener {}
                btnLeft.setOnClickListener {
                    if (!isDoubleClick) {
                        appNavigation.navigateUp()
                    }
                }
                btnMore.setOnClickListener {
                    if (!isDoubleClick) {
                        showBlockBottomSheet()
                    }
                }
            }

            ivSend.setOnClickListener {
                if (!isDoubleClick && isEnableSend) {
                    sendMessage(performerCode, binding.edtSentMsg.text.toString().trim())
                }
            }

            rcvMessage.setOnTouchListener { _, event ->
                onMessageAreaTouchEvent(event)
                false
            }

            edtSentMsg.onTextChange {
                onInputtedMessageChange(it)
            }
        }
    }

    private fun onInputtedMessageChange(editable: Editable?) {
        val length = editable?.toString()?.trim()?.replace("\n", "")?.length ?: 0
        handleButtonSend(length)
    }

    private fun handleButtonSend(msgLength: Int) {
        isEnableSend = if (msgLength == 0) {
            binding.ivSend.setImageResource(R.drawable.ic_send_user_detail_msg_disable)
            false
        } else {
            binding.ivSend.setImageResource(R.drawable.ic_send_user_detail_msg_enable)
            true
        }
    }

    private fun showBlockBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val bottomSheetBinding =
            LlUserDetailBottomSheetBinding.inflate(LayoutInflater.from(requireContext()))

        bottomSheetBinding.llItemBlockUser.setOnClickListener {
            showDialogBlock()
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.llItemReportUser.setOnClickListener {
            val bundle = Bundle().apply {
                putString(BUNDLE_KEY.PERFORMER_CODE, performerCode)
            }
            appNavigation.openUserDetailMsgToRMUserDetailReport(bundle)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    private fun showDialogBlock() {
        context?.let { context ->
            CommonAlertDialog.getInstanceCommonAlertdialog(context).showDialog()
                .setDialogTitle(String.format(getString(R.string.dialog_msg_block), performerName))
                .setTextPositiveButton(R.string.confirm_block_alert)
                .setTextNegativeButton(R.string.cancel)
                .setOnPositivePressed {
                    viewModel.blockUser(performerCode)
                    it.dismiss()
                }.setOnNegativePressed {
                    it.dismiss()
                }
        }
    }

    private fun sendMessage(code: String, message: String) {
        activity?.let { viewModel.sendMessage(RMMessageRequest(code, message)) }
    }

    override fun bindingStateView() {
        super.bindingStateView()
        viewModel.messageResult.observe(viewLifecycleOwner, messageResultResponse)
        viewModel.sendMessageResult.observe(viewLifecycleOwner, sendMessageResponse)
        viewModel.hiddenLoadMoreHandle.observe(viewLifecycleOwner, hiddenLoadMoreResult)
        viewModel.responseSendSocket.observe(viewLifecycleOwner, responseSendSocketResult)
        viewModel.responseReadSocket.observe(viewLifecycleOwner, responseReadSocketResult)
        viewModel.isEnableButtonSend.observe(viewLifecycleOwner, Observer {
            it?.let { binding.rcvMessage.isEnabled = it }
        })
        viewModel.actionState.observe(viewLifecycleOwner) {
            it?.let {
                if (it is ActionState.BlockUserSuccess) {
                    rmTopViewModel.isNeedUpdateData = true
                    appNavigation.openUserDetailMessageToRMTop()
                }
            }
        }
    }

    private var messageResultResponse: Observer<DataMessage> = Observer {
        viewModel.userProfileResult.apply {
            binding.toolBar.groupRightButton.isVisible = true
            setViewPerformer(this)
        }

        if (it.dataMsg.isNotEmpty()) {
            isLoadMore = it.isLoadMore
            val lastItemCount = mAdapter.itemCount

            val firstPerformer =
                it.dataMsg.filter { data -> data.typeMessage == MESSAGE_PERFORMER }[0] as RMMessageResponse

            isMessageFromServer = firstPerformer.fromOwnerMail ?: false
            if (firstPerformer.performer != null) {
                binding.toolBar.titleBackTv.text = firstPerformer.performer.name
            }
            binding.bottomBar.isVisible = !isMessageFromServer

            listMessage.apply {
                clear()
                addAll(it.dataMsg)
            }

            if (isLoadMore) {
                mAdapter.notifyItemRangeInserted(0, listMessage.size - lastItemCount)
                binding.rcvMessage.scrollToPosition(listMessage.size - lastItemCount)
            } else {
                when {
                    viewModel.isLoadMessageAfterSend -> {
                        mAdapter.submitList(listMessage)
                        viewModel.isLoadMessageAfterSend = false
                    }
                    viewModel.isMessageFromSocket -> {
                        mAdapter.notifyDataSetChanged()
                        viewModel.isMessageFromSocket = false
                    }
                    else -> mAdapter.submitList(listMessage)
                }
                if (mAdapter.itemCount > 0) {
                    binding.rcvMessage.scrollToPosition(mAdapter.itemCount - 1)
                }
            }
        }
    }

    private var sendMessageResponse: Observer<Boolean> = Observer {
        if (it) {
            viewModel.loadMessageAfterSend(performerCode)
            binding.edtSentMsg.setText("")
            viewModel.sendMessageResult.value = false
        }
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

    private var responseSendSocketResult: Observer<SocketActionSend> = Observer {
        if (it.senderCode == performerCode && !mAdapter.checkContainMsg(it.mailCode)) {
            viewModel.loadMessage(
                requireActivity(),
                performerCode,
                isLoadMore = false,
                isShowLoading = false
            )
        }
    }
    private var responseReadSocketResult: Observer<SocketActionRead> = Observer {
        viewModel.loadMessage(
            requireActivity(),
            performerCode,
            isShowLoading = false,
            isLoadMore = false
        )
    }

    override fun onStop() {
        binding.rootLayout.viewTreeObserver
            .removeOnGlobalLayoutListener(keyboardLayoutListener)
        super.onStop()
        if (activity is BaseActivity<*, *>) {
            (activity as BaseActivity<*, *>).setHandleDispathTouch(true)
        }
    }

    private val keyboardLayoutListener = OnGlobalLayoutListener {
        val r = Rect()
        binding.rootLayout.getWindowVisibleDisplayFrame(r)
        val screenHeight = binding.rootLayout.rootView.height
        val keypadHeight = screenHeight - r.bottom
        if (keypadHeight > screenHeight * 0.15) {
            binding.edtSentMsg.setMargins(
                requireContext().convertSourceToPixel(R.dimen._16sdp),
                requireContext().convertSourceToPixel(R.dimen.dimen_8),
                0,
                requireContext().convertSourceToPixel(R.dimen.dimen_8)
            )
        } else {
            binding.edtSentMsg.setMargins(
                0,
                requireContext().convertSourceToPixel(R.dimen.dimen_8),
                0,
                requireContext().convertSourceToPixel(R.dimen._42sdp)
            )
        }
    }

    private fun onMessageAreaTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                messageAreaTouchX = event.x
                messageAreaTouchY = event.y
                DeviceUtil.hideSoftKeyboard(activity)
                if (binding.edtSentMsg.isFocused) {
                    binding.edtSentMsg.clearFocus()
                }
            }
            MotionEvent.ACTION_UP -> {
                messageAreaTouchX = 0f
                messageAreaTouchY = 0f
            }
        }
    }

    override fun onDestroy() {
        activity?.let {
            viewModel.closeListenEvent()
            if (viewModel.isListMessageChange) {
                requireActivity().supportFragmentManager.setFragmentResult(
                    BUNDLE_KEY.BACK_TO_CHAT_BOX,
                    Bundle()
                )
            }
        }
        super.onDestroy()
    }
}