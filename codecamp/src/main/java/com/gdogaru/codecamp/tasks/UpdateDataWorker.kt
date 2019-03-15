package com.gdogaru.codecamp.tasks

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.gdogaru.codecamp.api.ApiErrorResponse
import com.gdogaru.codecamp.api.ApiResponse
import com.gdogaru.codecamp.api.ApiSuccessResponse
import com.gdogaru.codecamp.api.CodecampClient
import com.gdogaru.codecamp.api.model.EventSummary
import com.gdogaru.codecamp.di.AppInjector
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.FileType
import com.gdogaru.codecamp.repository.InternalStorage
import com.gdogaru.codecamp.svc.BookmarkingService
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import javax.inject.Inject


const val PROGRESS_ERROR = -1
const val PROGRESS_PENDING = -2

class UpdateDataWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @Inject
    lateinit var appPreferences: AppPreferences
    @Inject
    lateinit var storage: InternalStorage
    @Inject
    lateinit var client: CodecampClient
    @Inject
    lateinit var bookmarkingService: BookmarkingService

    init {
        AppInjector.appComponent.inject(this)
    }

    override fun doWork(): ListenableWorker.Result {
        val result = fetchAllData()
        return when (result) {
            is ApiSuccessResponse -> Result.success()
            is ApiErrorResponse -> Result.failure()
            else -> throw IllegalStateException("Unexpected result: $result")
        }
    }

    fun fetchAllData(): ApiResponse<Float> {
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

            appPreferences.activeEvent = eventList
                    .filter {
                        it.startDate?.toLocalDate()?.isBefore(LocalDate.now())?.not()
                                ?: false
                    }
                    .sortedBy { it.startDate }
                    .first().refId
        }
        postProgress(1F)
        return ApiResponse.create(1F)
    }

    private fun postProgress(status: Float) {
        appPreferences.updateProgress = status
    }

    private fun removeExpiredPreferences(eventList: List<EventSummary>) {
        eventList.map { it.title }
                .let { bookmarkingService.keepOnlyEvents(it.toSet()) }
    }

}
