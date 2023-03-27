package jp.careapp.counseling.android.ui.live_stream

object LiveStreamMode {
    const val PRIVATE = 1
    const val PREMIUM_PRIVATE = 2
    const val PARTY = 3
    const val PEEP = 4
}

object LiveStreamCommentType {
    const val NORMAL = 1
    const val PERFORMER = 2
    const val WHISPER = 3
}

object LiveStreamAction {
    const val PREMIUM_PRIVATE_MODE_REGISTER = "PREMIUM_PRIVATE_MODE_REGISTER"
    const val PRIVATE_MODE_REGISTER = "PRIVATE_MODE_REGISTER"
    const val PERFORMER_OUT_CONFIRM = "PERFORMER_OUT_CONFIRM"
    const val CHANGE_TO_PARTY_MODE = "CHANGE_TO_PARTY_MODE"
}