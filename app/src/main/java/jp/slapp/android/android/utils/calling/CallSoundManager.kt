package jp.slapp.android.android.utils.calling

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import jp.slapp.android.R
import timber.log.Timber

interface CallSoundManager {
    fun playRingBack()
    fun stopRingBack()
}

class CallSoundManagerImpl(
    private val context: Context,
    private val audioManager: AudioManager
) : CallSoundManager {

    companion object {
        const val TAG = "CallSoundManager"
    }

    private var mediaPlayer: MediaPlayer = MediaPlayer()

    override fun playRingBack() {
        try {
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setLegacyStreamType(AudioManager.STREAM_VOICE_CALL).build()
            )
            audioManager.isSpeakerphoneOn = false
            stopRingBack()
            mediaPlayer.setDataSource(
                context,
                Uri.parse("android.resource://" + context.packageName + "/" + R.raw.ring_back)
            )
            mediaPlayer.isLooping = true
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }

    override fun stopRingBack() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
        }
    }
}