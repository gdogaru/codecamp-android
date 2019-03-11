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
import android.os.Handler
import com.gdogaru.codecamp.svc.AppPreferences
import com.gdogaru.codecamp.svc.CodecampClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import javax.inject.Inject

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
class SplashScreenActivity : BaseActivity() {
    @Inject
    lateinit var appPreferences: AppPreferences
    @Inject
    lateinit var codecampClient: CodecampClient

    override fun onResume() {
        super.onResume()
        if (!checkPlayServices()) return

        triggerNextActivity()
    }

    private fun checkPlayServices(): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                val errorDialog = googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST)
                errorDialog.setOnDismissListener { this@SplashScreenActivity.finish() }
                errorDialog.show()
            }
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USER_DATA_CODE) {
            startMainActivity()
        }

        when (requestCode) {
            PLAY_SERVICES_RESOLUTION_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                triggerNextActivity()
            } else {
                finish()
            }
        }
    }


    private fun triggerNextActivity() {
        if (appPreferences.isUpdating) {
            LoadingDataActivity.startTop(this)
            finish()
            return
        }
        Handler().postDelayed({
            val lastUpdated = appPreferences.lastUpdated
            if (lastUpdated == 0L || System.currentTimeMillis() - lastUpdated > ONE_DAY_MILLIS) {
                LoadingDataActivity.startUpdate(this@SplashScreenActivity)
            } else {
                startMainActivity()
            }
        }, 10)
    }

    private fun startMainActivity() {
        if (System.currentTimeMillis() - appPreferences.lastAutoselect > ONE_DAY_MILLIS) {
            codecampClient.eventsSummary?.find { org.joda.time.LocalDate.now() == it.startDate?.toLocalDate() }
                    ?.run { appPreferences.activeEvent = refId }
        }

        val intent = Intent(this, com.gdogaru.codecamp.view.MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val ONE_DAY_MILLIS: Long = 8640000
        private const val USER_DATA_CODE = 1
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 923
    }
}
