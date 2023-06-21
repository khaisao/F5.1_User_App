package jp.slapp.android.android.data.model.message

import jp.slapp.android.android.ui.message.ChatMessageAdapter.Companion.MESSAGE_TIME

class TimeMessageResponse(val time: String = "") : BaseMessageResponse(MESSAGE_TIME, time)
