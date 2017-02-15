package com.gdogaru.codecamp.view;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.gdogaru.codecamp.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by Gabriel on 10/22/2014.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static final List<String> ALL_PERMISSIONS = Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 999;
    @Inject
    EventBus eventBus;

    boolean allPermissionsGranted = false;
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
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected void checkAndRequestPermissions() {
        permissionsNeeded = getPermissionsNeeded();
        if (!permissionsNeeded.isEmpty()) {
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
                                            checkAndRequestPermissions();
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
        overridePendingTransition(R.anim.hold,R.anim.act_slide_down);
    }
}
