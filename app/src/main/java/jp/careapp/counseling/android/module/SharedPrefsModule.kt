package jp.careapp.counseling.android.module

import jp.careapp.counseling.android.data.pref.AppPreferences
import jp.careapp.counseling.android.data.pref.RxPreferences
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
