package com.gdogaru.codecamp.tasks

import androidx.lifecycle.Transformations
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.BookmarkRepository
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataUpdater @Inject constructor(
        val appPreferences: AppPreferences,
        val bookmarkingService: BookmarkRepository) {

    fun shouldUpdate(): Boolean {
        return isDone() && appPreferences.lastUpdated.isBefore(Instant.now().minus(6, ChronoUnit.HOURS))
    }

    private fun isDone(): Boolean {
        return try {
            val status = WorkManager.getInstance().getWorkInfoById(UUID.fromString(appPreferences.activeJob))
            status.isDone || status.isCancelled
        } catch (ignored: Exception) {
            true
        }
    }

    fun updateIfNecessary() {
        if (shouldUpdate()) update()
    }

    fun lastJobStatus() = Transformations
            .switchMap(appPreferences.activeJobLiveData)
            { id ->
                try {
                    WorkManager.getInstance().getWorkInfoByIdLiveData(UUID.fromString(id))
                } catch (e: java.lang.Exception) {
                    Timber.w(e, "could not get status")
                    null
                }
            }


    fun update() {
        appPreferences.updateProgress = 0F
        val request = OneTimeWorkRequest.Builder(UpdateDataWorker::class.java).build()
        val result = WorkManager.getInstance().enqueueUniqueWork(UpdateDataWorker::class.java.name, ExistingWorkPolicy.REPLACE, request)
        appPreferences.activeJob = request.id.toString()
    }
}