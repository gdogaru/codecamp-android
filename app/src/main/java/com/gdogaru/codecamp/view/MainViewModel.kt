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

package com.gdogaru.codecamp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.CodecampRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import javax.inject.Inject

class MainViewModel
@Inject constructor(
    val repository: CodecampRepository,
    val preferences: AppPreferences
) : ViewModel() {

    val currentEvent: LiveData<Codecamp> =
        Transformations.switchMap(preferences.activeEventLiveData) { repository.eventData(it) }

    fun loadingProgress() = preferences.updateProgressLiveData

    /**
     * Cancel all coroutines when the ViewModel is cleared.
     */
    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun startUpdate() {
        repository.startUpdate()
    }
}
