package com.gdogaru.codecamp.svc.jobs

import android.app.Application
import android.content.Context
import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.evernote.android.job.JobManager
import com.gdogaru.codecamp.App
import com.gdogaru.codecamp.svc.AppPreferences
import com.gdogaru.codecamp.svc.CodecampClient
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton


/**
 * Created by Gabriel on 2/25/2018.
 */

@Module
class JobsModule {

    @Provides
    @Singleton
    fun provideJobManager(application: Application, jobCreator: AppJobCreator): JobManager {
        JobManager.create(application).addJobCreator(jobCreator)
        return JobManager.instance()
    }

    @Provides
    @IntoMap
    @StringKey(UpdateDataJob.TAG)
    fun updateDataJob(gson: Gson, eventBus: EventBus, codecampClient: CodecampClient, app: App, appPreferences: AppPreferences): Job {
        return UpdateDataJob(gson, eventBus, codecampClient, app, appPreferences)
    }

}

@Singleton
class AppJobCreator
@Inject constructor(val jobs: Map<String, @JvmSuppressWildcards Provider<Job>>) : JobCreator {

    override fun create(tag: String): Job? {
        return jobs[tag]?.get()
    }

}

class AddReceiver : JobCreator.AddJobCreatorReceiver() {
    override fun addJobCreator(context: Context, manager: JobManager) {
        // manager.addJobCreator(new DemoJobCreator());
    }
}
