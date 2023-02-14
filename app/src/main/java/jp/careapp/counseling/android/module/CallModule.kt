package jp.careapp.counseling.android.module

import android.content.Context
import android.media.AudioManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.careapp.counseling.android.utils.calling.CallSoundManager
import jp.careapp.counseling.android.utils.calling.CallSoundManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CallModule {
    @Singleton
    @Provides
    fun provideAudioManager(@ApplicationContext context: Context): AudioManager {
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    @Singleton
    fun provideCallSoundManager(
        @ApplicationContext context: Context,
        audioManager: AudioManager
    ): CallSoundManager {
        return CallSoundManagerImpl(context, audioManager)
    }
}