package com.gdogaru.codecamp.svc.jobs


/**
 * Created by Gabriel on 2/25/2018.
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
