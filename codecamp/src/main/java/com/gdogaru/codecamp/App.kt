package com.gdogaru.codecamp

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.app.Service
import android.content.BroadcastReceiver
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.evernote.android.job.JobManager
import com.gdogaru.codecamp.di.AppInjector
import dagger.android.*
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Inject

/**
 */
class App : MultiDexApplication(), HasActivityInjector, HasFragmentInjector, HasServiceInjector, HasBroadcastReceiverInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var broadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>
    @Volatile
    private var needToInject = true

    @Inject
    lateinit var jobManager: JobManager

    override fun onCreate() {
        super.onCreate()
        instance = this

        initCalligraphy()
        initCrashlytics()
        initTimber()

        injectIfNecessary()
    }

    /**
     * Lazily injects the [DaggerApplication]'s members. Injection cannot be performed in [ ][Application.onCreate] since [android.content.ContentProvider]s' [ ][android.content.ContentProvider.onCreate] method will be called first and might
     * need injected members on the application. Injection is not performed in the constructor, as
     * that may result in members-injection methods being called before the constructor has completed,
     * allowing for a partially-constructed instance to escape.
     */
    private fun injectIfNecessary() {
        if (needToInject) {
            synchronized(this) {
                if (needToInject) {
                    AppInjector.init(this)
                    if (needToInject) {
                        throw IllegalStateException("The AndroidInjector returned from applicationInjector() did not inject the DaggerApplication")
                    }
                }
            }
        }
    }

    @Inject
    internal fun setInjected() {
        needToInject = false
    }


    private fun initCalligraphy() {
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.font_roboto_regular))
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
        Timber.plant(CrashlyticsTree())
    }

    private fun initCrashlytics() {
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(this, crashlyticsKit)
    }

    override fun activityInjector() = activityInjector

    override fun fragmentInjector() = fragmentInjector

    override fun broadcastReceiverInjector() = broadcastReceiverInjector

    override fun serviceInjector() = serviceInjector

    companion object {

        private var instance: App? = null

        fun instance(): App? {
            return instance
        }

    }

}

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority >= Log.WARN) {
            if (t == null) {
                Crashlytics.log(priority, CRASHLYTICS_KEY_TAG, message)
            } else {

                Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
                Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
                Crashlytics.setString("message", message)
                Crashlytics.logException(t)
            }
        }
    }

    companion object {
        private val CRASHLYTICS_KEY_MESSAGE = "message"
        private val CRASHLYTICS_KEY_PRIORITY = "priority"
        private val CRASHLYTICS_KEY_TAG = "proliq"
    }
}