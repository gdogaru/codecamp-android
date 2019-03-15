package com.gdogaru.codecamp.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.gdogaru.codecamp.App
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.api.model.EventList
import com.gdogaru.codecamp.api.model.EventSummary
import org.threeten.bp.Instant
import timber.log.Timber
import java.io.File
import java.io.FileReader
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InternalStorage
@Inject constructor(val app: App, val mapper: ObjectMapper) {

    @Throws(Exception::class)
    private fun <T> readFromStorage(file: String, root: Instant, clazz: Class<T>): T {
        val myDir = rootDir(root)
        val outputFile = File(myDir, file)
        val fileReader = FileReader(outputFile)
        fileReader.use { return mapper.readValue(it, clazz) }
    }

    private fun rootDir(root: Instant): File = File(app.filesDir, root.toEpochMilli().toString())

    fun file(root: Instant, type: FileType, args: String? = null): File {
        return if (args.isNullOrBlank()) {
            File(rootDir(root), type.template)
        } else {
            File(rootDir(root), String.format(Locale.ENGLISH, type.template, args))
        }
    }

    fun readEvents(root: Instant): List<EventSummary> {
        return mapper.readValue(file(root, FileType.EVENTS).readText(), EventList::class.java)!!
    }

    fun readEvent(root: Instant, id: Long): Codecamp {
        return mapper.readValue(file(root, FileType.DETAILS, id.toString()).readText(), Codecamp::class.java)!!
    }

    fun deleteRoot(filename: String) {
        val f = File(app.filesDir, filename)
        delete(f)
    }

    private fun delete(f: File) {
        if (f.isDirectory) {
            for (c in f.listFiles()) delete(c)
        } else {
            if (!f.delete()) Timber.w("Failed to delete file: %s", f)
        }
    }
}

enum class FileType(val template: String) {
    EVENTS("events.json"),
    DETAILS("codecamp_%s.json")
}