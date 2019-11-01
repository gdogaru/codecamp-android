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

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.api.model.EventSummary
import com.gdogaru.codecamp.api.model.Schedule
import com.gdogaru.codecamp.tasks.DataUpdater
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
@Singleton
class CodecampRepository @Inject constructor(
    val storage: InternalStorage,
    val dataUpdater: DataUpdater,
    val preferences: AppPreferences
) {


    private val events: LiveData<List<EventSummary>> =
        Transformations.map(preferences.lastUpdatedLiveData) {
            try {
                storage.readEvents(it)
            } catch (e: Exception) {
                Timber.e(e, "Could not read saved events")
                throw RuntimeException(e)
            }
        }

    fun eventData(id: Long): LiveData<Codecamp> {
        dataUpdater.updateIfNecessary()
        return Transformations
            .map(preferences.lastUpdatedLiveData) {
                try {
                    storage.readEvent(it, id)
                } catch (e: Exception) {
                    Timber.e(e, "Could not read saved data")
                    throw RuntimeException(e)
                }
            }
    }

    fun events(): LiveData<List<EventSummary>> {
        dataUpdater.updateIfNecessary()
        return events
    }

    fun startUpdate() {
        dataUpdater.update()
    }

    val currentEvent: LiveData<Codecamp> =
        Transformations.switchMap(preferences.activeEventLiveData) { eventData(it) }

    fun currentSchedule(): LiveData<Schedule?> {
        return Transformations.switchMap(preferences.activeScheduleLiveData) { s ->
            Transformations.map(currentEvent) { if (it.schedules?.size ?: 0 <= s) null else it.schedules!![s] }
        }
    }


}
