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

package com.gdogaru.codecamp.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.tasks.DataUpdater
import javax.inject.Inject

class SplashViewModel
@Inject constructor(
    private val dataUpdater: DataUpdater,
    private val preferences: AppPreferences
) : ViewModel() {


    var jobState: LiveData<WorkerState> =
        Transformations.map(dataUpdater.lastJobStatus()) { toWorkerState(it.state) }
    var updateProgress: LiveData<Int> =
        Transformations.map(preferences.updateProgressLiveData) { (it * 100).toInt() }
    var loadingState: LiveData<Boolean> =
        Transformations.map(jobState) { it == WorkerState.IN_PROGRESS }
    var errorState: LiveData<Boolean> = Transformations.map(jobState) { it == WorkerState.ERROR }

    fun reloadData() {
        dataUpdater.update()
    }

    fun shouldReload(): Boolean {
        return if (preferences.hasUpdated()) {
            false
        } else {
            reloadData()
            true
        }
    }

    private fun toWorkerState(state: WorkInfo.State): WorkerState {
        return when (state) {
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.BLOCKED,
            WorkInfo.State.RUNNING -> WorkerState.IN_PROGRESS
            WorkInfo.State.SUCCEEDED -> WorkerState.DONE
            WorkInfo.State.CANCELLED,
            WorkInfo.State.FAILED -> WorkerState.ERROR
        }
    }
}

enum class WorkerState {
    IN_PROGRESS, DONE, ERROR
}
