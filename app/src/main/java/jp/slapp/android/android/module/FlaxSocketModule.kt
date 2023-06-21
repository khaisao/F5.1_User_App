package jp.slapp.android.android.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.slapp.android.android.network.socket.FlaxWebSocketManager
import jp.slapp.android.android.network.socket.MaruCastManager
import org.marge.marucast_android_client.MediaClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FlaxSocketModule {

    @Provides
    @Singleton
    fun providerFlaxSocketManager(): FlaxWebSocketManager {
        return FlaxWebSocketManager()
    }

    @Provides
    @Singleton
    fun providerMaruCastManager(): MaruCastManager {
        return MaruCastManager(MediaClient())
    }
}