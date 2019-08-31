/*
 * Copyright (c) 2019 Gabriel Dogaru - gdogaru@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.gdogaru.codecamp

import android.app.Application
import android.os.StrictMode
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.evernote.android.state.StateSaver
import com.gdogaru.codecamp.di.AppInjector
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
class App : MultiDexApplication(), HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    @Volatile
    private var needToInject = true

    override fun onCreate() {
        super.onCreate()
        instance = this

        initTimber()
        initCrashlytics()
        AndroidThreeTen.init(this)

        initDebugState()
        StateSaver.setEnabledForAllActivitiesAndSupportFragments(this, true)

        injectIfNecessary()
    }

    private fun initDebugState() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
//                            .penaltyDeath()
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
//                            .penaltyDeath()
                    .build()
            )
        }
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

    override fun androidInjector() = androidInjector

    @Inject
    internal fun setInjected() {
        needToInject = false
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.plant(CrashlyticsTree())
    }

    private fun initCrashlytics() {
        val crashlyticsKit = Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()
        Fabric.with(this, crashlyticsKit)
    }

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