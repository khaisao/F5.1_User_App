package jp.slapp.android.android.ui.live_stream.live_stream_bottom_sheet.confirm

import android.app.Application
import android.text.SpannableString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.careapp.core.base.BaseViewModel
import jp.careapp.core.utils.convertStringToSpannableString
import jp.slapp.android.R
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.CHANGE_TO_PARTY_MODE
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.CHANGE_TO_PARTY_MODE_FROM_PEEP
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.PERFORMER_OUT_CONFIRM
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.PREMIUM_PRIVATE_MODE_REGISTER
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.PRIVATE_MODE_REGISTER
import jp.slapp.android.android.ui.live_stream.LiveStreamAction.PRIVATE_MODE_REGISTER_REVIEW_MODE
import javax.inject.Inject

const val LIVE_STREAM_CONFIRM_BOTTOM_SHEET_MODE = "LIVE_STREAM_CONFIRM_BOTTOM_SHEET_MODE"

@HiltViewModel
class LiveStreamConfirmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val application: Application
) : BaseViewModel() {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    private val _content = MutableLiveData<SpannableString>()
    val content: LiveData<SpannableString>
        get() = _content

    init {
        savedStateHandle.get<String>(LIVE_STREAM_CONFIRM_BOTTOM_SHEET_MODE)?.let {
            handleMode(it)
        }
    }

    private fun handleMode(mode: String) {
        when (mode) {
            PREMIUM_PRIVATE_MODE_REGISTER -> {
                _title.value = application.getString(R.string.premium_private_mode_register_confirm)
                _content.value = application.applicationContext.convertStringToSpannableString(
                    R.string.premium_private_mode_register,
                    R.color.color_ff288b,
                    31,
                    47
                )
            }

            PRIVATE_MODE_REGISTER -> {
                _title.value = application.getString(R.string.private_mode_register_confirm)
                _content.value = application.applicationContext.convertStringToSpannableString(
                    R.string.private_mode_register,
                    R.color.color_ff288b,
                    41,
                    57
                )
            }

            PRIVATE_MODE_REGISTER_REVIEW_MODE -> {
                _title.value = application.getString(R.string.private_mode_register_confirm)
                _content.value = application.applicationContext.convertStringToSpannableString(
                    R.string.private_mode_register_review_mode,
                    R.color.color_ff288b,
                    29,
                    45
                )
            }

            PERFORMER_OUT_CONFIRM -> {
                _title.value = application.getString(R.string.performer_out_confirm)
            }

            CHANGE_TO_PARTY_MODE -> {
                _title.value = application.getString(R.string.change_to_party_mode)
            }

            CHANGE_TO_PARTY_MODE_FROM_PEEP -> {
                _title.value = application.getString(R.string.want_to_switch_to_party_mode)
            }
        }
    }
}