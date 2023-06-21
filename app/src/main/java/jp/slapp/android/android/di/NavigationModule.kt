package jp.slapp.android.android.di

import jp.careapp.core.navigationComponent.BaseNavigator
import jp.slapp.android.android.navigation.AppNavigation
import jp.slapp.android.android.navigation.AppNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class NavigationModule {

    @Binds
    abstract fun provideBaseNavigation(navigation: AppNavigatorImpl): BaseNavigator

    @Binds
    abstract fun provideAppNavigation(navigation: AppNavigatorImpl): AppNavigation
}
