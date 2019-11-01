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

package com.gdogaru.codecamp.api

import com.gdogaru.codecamp.App
import com.google.firebase.perf.metrics.AddTrace
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
@Singleton
class CodecampClient
@Inject constructor() {
    @Inject
    lateinit var app: App

    var okHttpClient: OkHttpClient


    init {
        okHttpClient = OkHttpClient()
    }

    @AddTrace(name = "downloadEventData")
    @Throws(Exception::class)
    private fun downloadToFile(url: String, outputFile: File): ApiResponse<File> {
        val myDir = outputFile.parentFile
        try {
            Timber.i("Downloading %s to %s", url, outputFile)
            val request = Request.Builder().url(url).build()
            okHttpClient.newCall(request).execute().use { response ->

                if (myDir.exists().not() && myDir.mkdirs().not()) {
                    return ApiResponse.create(IllegalStateException("Could not create dir structure: " + myDir.path))
                }
                if (outputFile.exists().not() && outputFile.createNewFile().not()) {
                    return ApiResponse.create(IllegalStateException("Could not create events file: " + outputFile.path))
                }
                response.body?.bytes()?.let {
                    outputFile.writeBytes(it)
                }
                return ApiResponse.create(outputFile)
            }
        } catch (t: Throwable) {
            return ApiResponse.create(t)
        }
    }

    fun downloadEvents(file: File): ApiResponse<File> =
        downloadToFile("https://connect.codecamp.ro/api/Conferences", file)

    fun downloadEvent(id: Long, file: File) =
        downloadToFile("https://connect.codecamp.ro/api/Conferences/$id", file)


}


