package com.gdogaru.codecamp.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import org.threeten.bp.Instant
import timber.log.Timber

/**
 * Created by Gabriel on 10/22/2014.
 */
class AppPreferences(private val app: Context) {

    private val pref: SharedPreferences
        get() = app.getSharedPreferences(CODECAMP, Context.MODE_PRIVATE)

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

    private fun clear(context: Context) {
        val settings = app.getSharedPreferences(DATA_UPDATE_SETTINGS, Context.MODE_PRIVATE)
        settings.edit()
                .remove(LAST_UPDATE)
                .remove(LAST_AUTO_SELECT)
                .apply()
    }


    fun setLastAutoSelect(value: Long?) {
        Timber.i("Last autoselect set to %s", value)
        pref.edit()
                .putLong(LAST_AUTO_SELECT, value!!)
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
        private val getter: () -> T)
    : LiveData<T>() {

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