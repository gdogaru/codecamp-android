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

package com.gdogaru.codecamp.di

import com.gdogaru.codecamp.App
import com.gdogaru.codecamp.db.DatabaseModule
import com.gdogaru.codecamp.tasks.UpdateDataWorker
import com.gdogaru.codecamp.view.di.ActivitiesModule
import com.gdogaru.codecamp.view.di.ViewModelModule
import com.procliq.walkie.di.modules.CoreModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidInjectionModule::class), (AndroidSupportInjectionModule::class),
    (ApplicationModule::class),
    (CoreModule::class), (DatabaseModule::class),
    (ActivitiesModule::class), (ViewModelModule::class)])
interface AppComponent {

    fun inject(app: App)

    fun inject(worker: UpdateDataWorker)

    @Component.Builder
    interface Builder {

        fun applicationModule(applicationModule: ApplicationModule): Builder

        fun build(): AppComponent
    }
}
