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

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import com.gdogaru.codecamp.R
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.util.*
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
abstract class BaseActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    private var allPermissionsGranted = false
    private var permissionsNeeded = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (actionBar != null) {
            actionBar!!.setDisplayShowTitleEnabled(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            navigateHome()
            overridePendingTransition(R.anim.hold, R.anim.act_slide_down)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateHome() {
        NavUtils.navigateUpFromSameTask(this)
    }

    fun checkAndRequestPermissions() {
        permissionsNeeded = getPermissionsNeeded()
        if (!permissionsNeeded.isEmpty()) {
            allPermissionsGranted = false
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
        } else {
            allPermissionsGranted = true
            onPermissionGranted()
        }
    }

    open fun onPermissionGranted() {}

    private fun getPermissionsNeeded(): ArrayList<String> {
        val permissionsNeeded = ArrayList<String>()
        for (permission in ALL_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add(permission)
            }
        }
        return permissionsNeeded
    }

    @Suppress("SameParameterValue")
    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener?) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            val notGranted = ArrayList(permissionsNeeded)
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    notGranted.remove(permissions[i])
                }
            }
            if (notGranted.size > 0) {
                if (getPermissionsNeeded().size > 0) {
                    showDialogOK("The app cannot function without these permissions.", null)
                }
                allPermissionsGranted = false
            } else {
                allPermissionsGranted = true
                onPermissionGranted()
            }
        }
    }


    override fun androidInjector() = dispatchingAndroidInjector

    companion object {
        val ALL_PERMISSIONS = Arrays.asList(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private val REQUEST_ID_MULTIPLE_PERMISSIONS = 999
    }

}
