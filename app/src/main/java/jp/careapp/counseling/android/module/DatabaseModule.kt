package jp.careapp.counseling.android.module

import jp.careapp.counseling.android.data.database.DatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(databaseRepository: DatabaseRepository): DatabaseRepository {
        return databaseRepository
    }
}
