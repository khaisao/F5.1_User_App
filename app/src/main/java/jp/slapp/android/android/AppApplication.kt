package jp.slapp.android.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import dagger.hilt.android.HiltAndroidApp
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import jp.slapp.android.BuildConfig
import jp.slapp.android.android.utils.appsflyer.AppsFlyerUtil
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AppApplication @Inject constructor() : Application() {

    companion object {
        var context: Context? = null
        fun getAppContext(): Context? {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        context = this.applicationContext

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("care_realm")
            .migration(AppMigration())
            .schemaVersion(1)
            .allowWritesOnUiThread(true)
            .deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)

        registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())

        AppsFlyerUtil.createAppsFlyer(this)
    }

    private class AdjustLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityDestroyed(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    }

    internal class AppMigration : RealmMigration {
        override fun migrate(
            realm: DynamicRealm,
            oldVersion: Long,
            newVersion: Long
        ) {
            // TODO
        }
    }
}
