package jp.careapp.counseling.android.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.careapp.counseling.android.keystore.KeyService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class KeyModule {

    @Provides
    @Singleton
    fun providerKeyService(): KeyService {
        return KeyService().prepareKeyStore()
    }
}