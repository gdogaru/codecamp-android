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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.repository.AppPreferences
import com.gdogaru.codecamp.repository.InternalStorage
import com.gdogaru.codecamp.tasks.DataUpdater
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
class SplashScreenActivity : BaseActivity() {
    @BindView(R.id.progress)
    lateinit var progressBar: ProgressBar
    @BindView(R.id.loadingGroup)
    lateinit var loadingGroup: Group
    @BindView(R.id.errorGroup)
    lateinit var errorGroup: Group

    @Inject
    lateinit var dataUpdater: DataUpdater
    @Inject
    lateinit var preferences: AppPreferences
    @Inject
    lateinit var internalStorage: InternalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_data)
        ButterKnife.bind(this)
        actionBar?.hide()

        dataUpdater.lastJobStatus().observe(this, Observer { onWorkerStatus(it.state) })
        preferences.updateProgressLiveData.observe(this, Observer { onProgress(it) })
    }

    override fun onResume() {
        super.onResume()
        checkPlayServices()
    }

    private fun onWorkerStatus(state: WorkInfo.State) {
        when (state) {
            WorkInfo.State.ENQUEUED,
            WorkInfo.State.BLOCKED,
            WorkInfo.State.RUNNING -> onDataLoading()
            WorkInfo.State.SUCCEEDED -> onReady()
            WorkInfo.State.CANCELLED,
            WorkInfo.State.FAILED -> onDataError()
        }
    }

    private fun onReady() {
        startMainActivity()
    }

    @OnClick(R.id.retry)
    fun onRetry() {
        reloadData()
    }

    private fun reloadData() {
        dataUpdater.update()
    }

    private fun onDataError() {
        loadingGroup.visibility = View.GONE
        errorGroup.visibility = View.VISIBLE
    }

    private fun onDataLoading() {
        loadingGroup.visibility = View.VISIBLE
        errorGroup.visibility = View.GONE
    }

    private fun onProgress(progress: Float) {
        progressBar.progress = (progress * 100).toInt()
    }

    private fun checkPlayServices() {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                val errorDialog = googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST)
                errorDialog.setOnDismissListener { this@SplashScreenActivity.finish() }
                errorDialog.show()
            }
            finish()
        } else {
            onPlayServicesAvailable()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USER_DATA_CODE) {
            startMainActivity()
        }

        when (requestCode) {
            PLAY_SERVICES_RESOLUTION_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                onPlayServicesAvailable()
            } else {
                finish()
            }
        }
    }

    private fun onPlayServicesAvailable() {
        if (preferences.hasUpdated().not()) {
            reloadData()
        } else {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, com.gdogaru.codecamp.view.MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.act_fade_in, R.anim.act_fade_out)
    }

    companion object {
        private const val ONE_DAY_MILLIS: Long = 8640000
        private const val USER_DATA_CODE = 1
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 923
    }
}
