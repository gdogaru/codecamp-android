/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdogaru.codecamp.di

import com.firstpremier.mypremiercreditcard.ui.di.ViewModelModule
import com.gdogaru.codecamp.App
import com.gdogaru.codecamp.svc.jobs.JobsModule
import com.gdogaru.codecamp.view.di.ActivitiesModule
import com.procliq.walkie.di.modules.CoreModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidInjectionModule::class), (AndroidSupportInjectionModule::class),
    (ApplicationModule::class),
    (CoreModule::class),
    (JobsModule::class),
    (ActivitiesModule::class), (ViewModelModule::class)])
interface AppComponent {

    fun inject(app: App)

    @Component.Builder
    interface Builder {

        fun applicationModule(applicationModule: ApplicationModule): Builder

        fun build(): AppComponent
    }
}