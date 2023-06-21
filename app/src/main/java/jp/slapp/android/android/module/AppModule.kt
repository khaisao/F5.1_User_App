package jp.slapp.android.android.module

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

//    @Provides
//    @Singleton
//    fun providerRequestOption():RequestOptions{
//        return RequestOptions
//    }

    @Provides
    @Singleton
    fun providerRequestManager(@ApplicationContext context: Context): RequestManager {
        return Glide.with(context)
    }
}
