package com.procliq.walkie.di.modules

import android.app.Application
import com.gdogaru.codecamp.App
import com.gdogaru.codecamp.model.json.DateTypeAdapter
import com.gdogaru.codecamp.model.json.LocalDateTimeTypeAdapter
import com.gdogaru.codecamp.model.json.LocalTimeTypeAdapter
import com.gdogaru.codecamp.svc.AppPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import java.util.*
import javax.inject.Singleton

/**
 * Created by Gabriel on 3/23/2018.
 */
@Module
class CoreModule {

    @Provides
    @Singleton
    internal fun appPreferences(app: App): AppPreferences {
        return AppPreferences(app)
    }

    @Provides
    @Singleton
    fun createGson(): Gson {
        return GsonBuilder()
                .setDateFormat(DateTypeAdapter.FORMAT)
                .registerTypeAdapter(Date::class.java, DateTypeAdapter())
                .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                .create()
    }

    @Provides
    @Singleton
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    @Singleton
    fun eventBus(): EventBus {
        return EventBus.getDefault()
    }

    @Provides
    @Singleton
    fun firebaseAnalytics(app: Application): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(app)
    }
}
