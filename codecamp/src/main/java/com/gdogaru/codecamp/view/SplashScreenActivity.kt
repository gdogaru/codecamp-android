/*
 * Copyright (C) 2008 Gabriel Dogaru (gdogaru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
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
