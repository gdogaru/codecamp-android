package com.gdogaru.codecamp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.api.model.EventSummary
import com.gdogaru.codecamp.tasks.DataUpdater
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CodecampRepository @Inject constructor(
        val storage: InternalStorage,
        val dataUpdater: DataUpdater,
        val preferences: AppPreferences,
        val appExecutors: AppExecutors) {


    private val events: LiveData<List<EventSummary>> =
            Transformations.map(preferences.lastUpdatedLiveData) {
                storage.readEvents(it)
            }

    fun eventData(id: Long): LiveData<Codecamp> {
        dataUpdater.updateIfNecessary()
        return Transformations
                .map(preferences.lastUpdatedLiveData) {
                    storage.readEvent(it, id)
                }
    }

    fun events(): LiveData<List<EventSummary>> {
        dataUpdater.updateIfNecessary()
        return events
    }

}
