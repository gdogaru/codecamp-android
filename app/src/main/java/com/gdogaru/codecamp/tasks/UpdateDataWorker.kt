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

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.gdogaru.codecamp.api.ApiErrorResponse
import com.gdogaru.codecamp.api.ApiResponse
import com.gdogaru.codecamp.api.ApiSuccessResponse
import com.gdogaru.codecamp.api.CodecampClient
import com.gdogaru.codecamp.api.model.EventSummary
import com.gdogaru.codecamp.di.AppInjector
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.BookmarkRepository
import com.gdogaru.codecamp.repository.FileType
import com.gdogaru.codecamp.repository.InternalStorage
import kotlinx.coroutines.runBlocking
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class UpdateDataWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @Inject
    lateinit var appPreferences: AppPreferences
    @Inject
    lateinit var storage: InternalStorage
    @Inject
    lateinit var client: CodecampClient
    @Inject
    lateinit var bookmarkingService: BookmarkRepository

    init {
        AppInjector.appComponent.inject(this)
    }

    override fun doWork(): Result {
        return when (val result = fetchAllData()) {
            is ApiSuccessResponse -> Result.success()
            is ApiErrorResponse -> Result.failure()
            else -> throw IllegalStateException("Unexpected result: $result")
        }
    }

    @Suppress("RemoveExplicitTypeArguments")
    private fun fetchAllData(): ApiResponse<Float> {
        val startTime = Instant.now()

        val resp = client.downloadEvents(storage.file(startTime, FileType.EVENTS))
        if (resp is ApiErrorResponse) {
            return ApiResponse.create(resp.error)
        }
        postProgress(0.2F)

        val eventList = storage.readEvents(startTime)
        var progress = 0.2F
        for (es in eventList) {
            val er = client.downloadEvent(es.refId, storage.file(startTime, FileType.DETAILS, es.refId.toString()))
            if (er is ApiErrorResponse) {
                return ApiResponse.create(er.error)
            }
            progress += 0.7F / eventList.size
            postProgress(progress)
        }

        if (eventList.isNotEmpty()) {
            val prev = appPreferences.lastUpdated
            appPreferences.lastUpdated = startTime
            storage.deleteRoot(prev.toString())
            removeExpiredPreferences(eventList)

            //set first
            eventList.sortedWith(compareBy(nullsLast<LocalDateTime>()) { it.startDate })

            //replace active if events changed
            if (eventList.map { it.refId }.contains(appPreferences.activeEvent).not()) {
                appPreferences.activeEvent = eventList
                        .filter {
                            it.startDate?.toLocalDate()?.isBefore(LocalDate.now())?.not()
                                    ?: false
                        }
                        .minBy { it.startDate ?: LocalDateTime.MAX }
                        ?.refId ?: 0L
            }
        }
        postProgress(1F)
        return ApiResponse.create(1F)
    }

    private fun postProgress(status: Float) {
        appPreferences.updateProgress = status
    }

    private fun removeExpiredPreferences(eventList: List<EventSummary>) {
        runBlocking { bookmarkingService.keepOnlyEvents(eventList.map { it.title.orEmpty() }.toSet()) }
    }

}
