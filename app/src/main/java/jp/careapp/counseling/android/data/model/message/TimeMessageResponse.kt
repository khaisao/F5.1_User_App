package jp.careapp.counseling.android.data.model.message

import jp.careapp.counseling.android.ui.message.ChatMessageAdapter.Companion.MESSAGE_TIME

class TimeMessageResponse(val time: String = "") : BaseMessageResponse(MESSAGE_TIME, time)
