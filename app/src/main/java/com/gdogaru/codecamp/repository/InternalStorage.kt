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

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
@Singleton
class InternalStorage @Inject constructor(
    private val app: App,
    private val mapper: ObjectMapper
) {

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
        try {
            return mapper.readValue(file(root, FileType.EVENTS).readText(), EventList::class.java)
                .orEmpty()
        } catch (e: Throwable) {
            Timber.e(e, "Could not read storage events")
            throw e
        }
    }

    fun readEvent(root: Instant, id: Long): Codecamp {
        try {
            return mapper.readValue(
                file(root, FileType.DETAILS, id.toString()).readText(),
                Codecamp::class.java
            )
        } catch (e: Throwable) {
            Timber.e(e, "Could not read event with id %s", id)
            throw e
        }
    }

    fun deleteRoot(filename: String) {
        val f = File(app.filesDir, filename)
        delete(f)
    }

    private fun delete(f: File) {
        if (f.isDirectory) {
            f.listFiles()?.forEach { delete(it) }
        } else {
            if (!f.delete()) Timber.w("Failed to delete file: %s", f)
        }
    }
}

enum class FileType(val template: String) {
    EVENTS("events.json"),
    DETAILS("codecamp_%s.json")
}
