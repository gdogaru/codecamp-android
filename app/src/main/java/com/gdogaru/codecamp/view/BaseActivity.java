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

package com.gdogaru.codecamp.view;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.gdogaru.codecamp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
public abstract class BaseActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    public static final List<String> ALL_PERMISSIONS = Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 999;

    boolean allPermissionsGranted = false;
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    private ArrayList<String> permissionsNeeded = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //    public void onEventMainThread(NoInternetEvent event) {
//        Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
//    }
    public void setChildActionBar(int textid) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(textid);
            actionBar.setIcon(R.drawable.icon_transparent);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateHome();
                overridePendingTransition(R.anim.hold, R.anim.act_slide_down);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void navigateHome() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void checkAndRequestPermissions() {
        permissionsNeeded = getPermissionsNeeded();
        if (false && !permissionsNeeded.isEmpty()) {
            allPermissionsGranted = false;
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        } else {
            allPermissionsGranted = true;
            onPermissionGranted();
        }
    }

    public void onPermissionGranted() {
    }

    private ArrayList<String> getPermissionsNeeded() {
        List<String> permissionsToCheck = ALL_PERMISSIONS;
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissionsToCheck) {
            int permissionSendMessage = ContextCompat.checkSelfPermission(this, permission);
            if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }
        return permissionsNeeded;
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                List<String> notGranted = new ArrayList<>(permissionsNeeded);
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        notGranted.remove(permissions[i]);
                    }
                }
                if (notGranted.size() > 0) {
                    if (getPermissionsNeeded().size() > 0) {
                        showDialogOK("The app cannot function without these permissions.",
                                (dialog, which) -> {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
//                                            checkAndRequestPermissions();
                                            break;
                                    }
                                });
                    }
                    allPermissionsGranted = false;
                } else {
                    allPermissionsGranted = true;
                    onPermissionGranted();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
