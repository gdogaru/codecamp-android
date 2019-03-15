package com.gdogaru.codecamp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.CodecampRepository
import javax.inject.Inject


class AgendaViewModel @Inject constructor(
        val repository: CodecampRepository,
        val preferences: AppPreferences)
    : ViewModel() {

    val currentEvent: LiveData<Codecamp> = Transformations.switchMap(preferences.activeEventLiveData) { repository.eventData(it) }

}