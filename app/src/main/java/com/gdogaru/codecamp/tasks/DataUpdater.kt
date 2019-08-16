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
        val result = WorkManager.getInstance().enqueueUniqueWork(UpdateDataWorker::class.java.name, ExistingWorkPolicy.KEEP, request)
        appPreferences.activeJob = request.id.toString()
    }
}