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

package com.gdogaru.codecamp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.svc.AppPreferences;

import javax.inject.Inject;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class SplashScreenActivity extends BaseActivity {
    public static final int TWO_WEEKS_MILLIS = 1209600000;
    private static final long ONE_DAY_MILLIS = 86400000;
    private static final double SPLASH_TIME = 0.3; //in seconds
    private static final int USER_DATA_CODE = 1;


    @Inject
    AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getDiComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        triggerNextActivity();
    }

    private void triggerNextActivity() {
        if (appPreferences.isUpdating()) {
            LoadingDataActivity.startTop(this);
            finish();
            return;
        }
        new Handler().postDelayed(() -> {
            long lastUpdated = appPreferences.getLastUpdated();
            if (lastUpdated == 0 || System.currentTimeMillis() - lastUpdated > ONE_DAY_MILLIS) {
                LoadingDataActivity.startUpdate(SplashScreenActivity.this);
            } else {
                startMainActivity();
            }
        }, 10);
    }

    private void startMainActivity() {
        MainActivity.start(this);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == USER_DATA_CODE) {
            startMainActivity();
        }
    }
}
