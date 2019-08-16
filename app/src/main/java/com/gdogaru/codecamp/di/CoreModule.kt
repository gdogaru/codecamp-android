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
 * @author Gabriel Dogaru (gdogaru@gmail.com)
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
