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

package com.gdogaru.codecamp.svc.jobs


/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

//@Module
class JobsModule {

//    @Provides
//    @Singleton
//    fun provideJobManager(application: Application, jobCreator: AppJobCreator): JobManager {
//        JobManager.create(application).addJobCreator(jobCreator)
//        return JobManager.instance()
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey(UpdateDataJob.TAG)
//    fun updateDataJob(gson: Gson, eventBus: EventBus, codecampClient: CodecampClient, app: App, appPreferences: AppPreferences): Job {
//        return UpdateDataJob(gson, eventBus, codecampClient, app, appPreferences)
//    }

}

//@Singleton
//class AppJobCreator
//constructor(val jobs: Map<String, @JvmSuppressWildcards Provider<Job>>) : JobCreator {
//
//    override fun create(tag: String): Job? {
//        return jobs[tag]?.get()
//    }
//
//}

//class AddReceiver : JobCreator.AddJobCreatorReceiver() {
//    override fun addJobCreator(context: Context, manager: JobManager) {
//        // manager.addJobCreator(new DemoJobCreator());
//    }
//}
