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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gdogaru.codecamp.databinding.SplashBinding
import com.gdogaru.codecamp.view.BaseActivity
import com.gdogaru.codecamp.view.MainActivity
import com.gdogaru.codecamp.view.util.autoCleared
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import javax.inject.Inject


/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
class SplashActivity : BaseActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: SplashViewModel by viewModels { viewModelFactory }
    private var binding by autoCleared<SplashBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.gdogaru.codecamp.R.layout.splash)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        actionBar?.hide()

        viewModel.jobState.observe(
            this,
            Observer { if (it == WorkerState.DONE) startMainActivity() })
    }

    override fun onResume() {
        super.onResume()
        checkPlayServices()
    }

    private fun checkPlayServices() {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                val errorDialog =
                    googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST)
                errorDialog.setOnDismissListener { this@SplashActivity.finish() }
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
        if (viewModel.shouldReload().not()) {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
        overridePendingTransition(
            com.gdogaru.codecamp.R.anim.act_fade_in,
            com.gdogaru.codecamp.R.anim.act_fade_out
        )
    }

    companion object {
        private const val USER_DATA_CODE = 1
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 923
    }
}
