package com.procliq.walkie.di.modules

import android.app.Application
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.gdogaru.codecamp.App
import com.gdogaru.codecamp.api.model.json.ThreeTenModule
import com.gdogaru.codecamp.repository.AppPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
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
    fun createObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(ThreeTenModule())
        mapper.registerModule(KotlinModule())
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        return mapper
    }

    @Provides
    @Singleton
    fun firebaseAnalytics(app: Application): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(app)
    }
}
