package com.gdogaru.codecamp.svc

import android.os.Environment
import android.support.v4.util.Pair
import com.gdogaru.codecamp.App
import com.gdogaru.codecamp.model.*
import com.gdogaru.codecamp.svc.events.DataLoadingEvent
import com.gdogaru.codecamp.util.IOUtils
import com.gdogaru.codecamp.util.Strings
import com.gdogaru.codecamp.util.Throwables
import com.google.firebase.perf.metrics.AddTrace
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Okio
import org.greenrobot.eventbus.EventBus
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import timber.log.Timber
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
@Singleton
class CodecampClient
@Inject constructor() {
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var app: App
    @Inject
    lateinit var client: OkHttpClient
    @Inject
    lateinit var appPreferences: AppPreferences
    @Inject
    lateinit var eventBus: EventBus
    @Inject
    lateinit var bookmarkingService: BookmarkingService
    private var currentCodecamp: Codecamp? = null
    private var eventList: EventList? = null


    /* Checks if external storage is available for read and write */
    val isExternalStorageWritable: Boolean
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /* Checks if external storage is available to at least read */
    val isExternalStorageReadable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
        }

    //todo
    val event: Codecamp?
        get() {
            if (currentCodecamp == null) {
                loadCodecamp()
            }
            return currentCodecamp
        }

    val schedule: Schedule
        get() = event!!.schedules[appPreferences.activeSchedule]

    val eventsSummary: EventList?
        get() {
            loadCodecamp()
            return eventList
        }

    @AddTrace(name = "downloadEventData")
    @Throws(Exception::class)
    private fun download(url: String, root: String, fileName: String): File {
        Timber.i("Downloading %s to %s %s", url, root, fileName)
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        val myDir = File(app.filesDir, root)
        myDir.mkdirs()
        val outputFile = File(myDir, fileName)

        val sink = Okio.buffer(Okio.sink(outputFile))
        // you can access body of response
        sink.writeAll(response.body()!!.source())
        sink.close()
        return outputFile
    }

    @AddTrace(name = "loadCodecampEvent")
    private fun loadCodecamp() {
        eventList = readData("events.json", EventList::class.java)
        //try to load from preferences
        var id: Long = 0
        val pe = appPreferences.activeEvent
        if (pe != 0L) {
            for (e in eventList!!) {
                if (e.refId == pe) {
                    id = pe
                    break
                }
            }
        }
        //if no preferences load first //todo load next
        if (id == 0L && eventList!!.size > 0) {
            id = eventList!![0].refId
            appPreferences.setActiveEvent(id)
        }
        currentCodecamp = if (id == 0L) null else readData("codecamp_$id.json", Codecamp::class.java)

        for (schedule in currentCodecamp!!.schedules) {
            val trackPositions = HashMap<String, Int>()
            for (t in schedule.tracks) {
                trackPositions[t.name] = t.displayOrder
            }
            trackPositions[""] = -1

            schedule.sessions.sortWith(Comparator { o1, o2 ->
                val result = o1.startTime.compareTo(o2.startTime)
                if (result == 0)
                    trackPositions[Strings.nullToEmpty(o2.track)]?.let { trackPositions[Strings.nullToEmpty(o1.track)]?.minus(it) }!!
                else result
            })
        }
    }

    private fun <T> readData(s: String, clazz: Class<T>): T {
        if (appPreferences.lastUpdated == 0L) {
            return readFromAssets(s, clazz)
        }
        try {
            return readFromStorage(s, clazz)
        } catch (e: Exception) {
            Timber.e(e, "Could not read from storage")
            return readFromAssets(s, clazz)
        }

    }

    @Throws(Exception::class)
    private fun <T> readFromStorage(file: String, clazz: Class<T>): T {
        val root = appPreferences.lastUpdated
        val myDir = File(app.filesDir, root.toString())
        val outputFile = File(myDir, file)
        val fileReader = FileReader(outputFile)
        fileReader.use { return gson.fromJson(it, clazz) }
    }

    private fun <T> readFromAssets(fileName: String, clazz: Class<T>): T {
        try {
            return gson.fromJson(IOUtils.toString(app.assets.open(fileName)), clazz)
        } catch (e: IOException) {
            throw Throwables.propagate(e)
        }

    }

    @AddTrace(name = "fetchAllData")
    @Throws(Exception::class)
    fun fetchAllData() {
        val now = System.currentTimeMillis()
        val root = now.toString()

        val file = download("https://connect.codecamp.ro/api/Conferences", root, "events.json")
        eventBus.post(DataLoadingEvent(20))
        val eventList = gson.fromJson(file.readText(), EventList::class.java)
        var progress = 20
        for (es in eventList) {
            download("https://connect.codecamp.ro/api/Conferences/" + es.refId, root, "codecamp_" + es.refId + ".json")
            progress += 70 / eventList.size
            eventBus.post(DataLoadingEvent(progress))
        }

        if (eventList.size > 0) {
            val prev = appPreferences.lastUpdated
            appPreferences.lastUpdated = now
            delete(File(app.filesDir, prev.toString()))
            removeExpiredPreferences()

            //set first
            eventList.sortWith(compareBy(nullsLast<LocalDateTime>()) { it -> it.startDate })

            appPreferences.activeEvent = eventList.filter { !it.startDate.toLocalDate().isBefore(LocalDate.now()) }
                    .sortedBy { it.startDate }
                    .first()?.refId ?: eventList[0].refId
        }
    }

    private fun removeExpiredPreferences() {
        eventsSummary?.map { it.title }
                ?.let { bookmarkingService.keepOnlyEvents(it.toSet()) }
    }

    @Throws(IOException::class)
    private fun delete(f: File) {
        if (f.isDirectory) {
            for (c in f.listFiles()) delete(c)
        } else {
            if (!f.delete()) Timber.w("Failed to delete file: %s", f)
        }
    }

    fun getSession(id: String) = schedule.sessions?.find { it.id == id }

    fun getSpeaker(id: String) = event?.speakers?.find { it.name == id }

    fun getTrack(track: String) = schedule.tracks?.find { it.name == track }

    fun getTrackExtended(track: String): Pair<Track, Schedule>? {
        for (s in event!!.schedules) {
            s.tracks.find { input -> input.name == track }
                    .let { return@getTrackExtended Pair(it, s) }
        }
        return null
    }

    fun getTrackSessionsIds(trackId: String?): ArrayList<String> {
        val result = ArrayList<String>()
        val sessions = schedule.sessions
        for (i in sessions.indices) {
            val s = sessions[i]
            if (trackId == null || s.track == null || trackId == s.track) {
                result.add(s.id)
            }
        }
        return result
    }

    fun setActiveEvent(id: Long) {
        appPreferences.activeEvent = id
        currentCodecamp = null
    }

    fun getSessionsBySpeaker(speakerId: String): List<Pair<Codecamp, Session>> {
        val event = event
        val schedules = event!!.schedules
        val result = ArrayList<Pair<Codecamp, Session>>()
        for (schedule in schedules) {
            for (s in schedule.sessions) {
                if (s.speakerIds == null || s.speakerIds.size == 0) continue
                for (sp in s.speakerIds) {
                    if (sp != null && sp == speakerId) {
                        result.add(Pair(event, s))
                        break
                    }
                }
            }
        }
        return result
    }
}

