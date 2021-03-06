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

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import org.threeten.bp.Instant
import timber.log.Timber

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
class AppPreferences(private val context: Context) {

    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences(CODECAMP, Context.MODE_PRIVATE)
    }

    val lastAutoselect: Long
        get() = pref.getLong(LAST_AUTO_SELECT, 0)

    var activeJob: String
        get() = pref.getString(ACTIVE_JOB, "")!!
        set(id) = pref.edit()
            .putString(ACTIVE_JOB, id)
            .apply()
    val activeJobLiveData: LiveData<String>
        get() = PreferencesLiveData(pref, ACTIVE_JOB) { activeJob }

    var activeSchedule: Int
        get() = pref.getInt(ACTIVE_SCHEDULE, 0)
        set(position) = pref.edit()
            .putInt(ACTIVE_SCHEDULE, position)
            .apply()

    val activeScheduleLiveData: LiveData<Int>
        get() = PreferencesLiveData(pref, ACTIVE_SCHEDULE) { activeSchedule }

    var listViewList: Boolean
        get() = pref.getBoolean(LIST_VIEW, true)
        set(b) = pref.edit()
            .putBoolean(LIST_VIEW, b)
            .apply()

    var lastUpdated: Instant
        get() = Instant.ofEpochMilli(pref.getLong(LAST_UPDATE, 0))
        set(v) = pref.edit()
            .putLong(LAST_UPDATE, v.toEpochMilli())
            .apply()

    val lastUpdatedLiveData: LiveData<Instant>
        get() = PreferencesLiveData(pref, LAST_UPDATE) { lastUpdated }


    var activeEvent: Long
        get() = pref.getLong(ACTIVE_EVENT, 0)
        set(id) = pref.edit()
            .putLong(ACTIVE_EVENT, id)
            .apply()

    val activeEventLiveData: LiveData<Long>
        get() = PreferencesLiveData(pref, ACTIVE_EVENT) { activeEvent }

    var updateProgress: Float
        get() = pref.getFloat(UPDATE_STATUS, 0F)
        set(v) = pref.edit()
            .putFloat(UPDATE_STATUS, v)
            .apply()
    val updateProgressLiveData: LiveData<Float>
        get() = PreferencesLiveData(pref, UPDATE_STATUS) { updateProgress }

    private fun clear() {
        val settings = context.getSharedPreferences(DATA_UPDATE_SETTINGS, Context.MODE_PRIVATE)
        settings.edit()
            .remove(LAST_UPDATE)
            .remove(LAST_AUTO_SELECT)
            .apply()
    }


    fun setLastAutoSelect(value: Long) {
        Timber.i("Last autoselect set to %s", value)
        pref.edit()
            .putLong(LAST_AUTO_SELECT, value)
            .apply()
    }

    fun hasUpdated(): Boolean {
        return pref.contains(LAST_UPDATE)
    }


    companion object {

        private const val DATA_UPDATE_SETTINGS = "data_update_settings"
        private const val LAST_UPDATE = "LAST_UPDATE"
        private const val LAST_AUTO_SELECT = "LAST_AUTOSELECT"
        private const val LAST_VERSION = "LAST_VERSION"
        private const val ACTIVE_EVENT = "ACTIVE_EVENT"
        private const val UPDATE_STATUS = "UPDATE_STATUS"
        private const val ACTIVE_SCHEDULE = "ACTIVE_SCHEDULE"
        private const val ACTIVE_JOB = "ACTIVE_JOB"
        private const val LIST_VIEW = "LIST_VIEW"
        val CODECAMP = "Codecamp"
    }


}

class PreferencesLiveData<T>(
    private val pref: SharedPreferences,
    private val key: String,
    private val getter: () -> T
) : LiveData<T>() {

    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun onActive() {
        super.onActive()
        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
            if (key == k) {
                value = getter.invoke()
            }
        }
        value = getter.invoke()
        pref.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onInactive() {
        if (listener != null) {
            pref.unregisterOnSharedPreferenceChangeListener(listener)
        }
        super.onInactive()
    }
}
