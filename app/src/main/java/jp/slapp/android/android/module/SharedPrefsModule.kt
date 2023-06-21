package jp.slapp.android.android.module

import jp.slapp.android.android.data.pref.AppPreferences
import jp.slapp.android.android.data.pref.RxPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SharedPrefsModule {
    @Binds
    abstract fun provideRxPreference(preferences: AppPreferences): RxPreferences
}
