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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gdogaru.codecamp.CodecampApplication;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.prefs.UpdatePrefsUtil;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.svc.DataProcessorTask;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class SplashScreenActivity extends Activity {
    public static final int TWO_WEEKS_MILLIS = 1209600000;
    private static final long ONE_DAY_MILLIS = 86400000;
    private static final double SPLASH_TIME = 0.5; //in seconds
    private static final int USER_DATA_CODE = 1;

    CodecampClient codecampClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        codecampClient = ((CodecampApplication)getApplication()).getCodecampClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    public void init() {
        triggerNextActivity();
    }

    private void triggerNextActivity() {
        new Handler().postDelayed(new Runnable() {
            public void run() {

                SplashScreenActivity context = SplashScreenActivity.this;
                long lastUpdated = UpdatePrefsUtil.getLastUpdated(context);
                if (lastUpdated == 0
                        || System.currentTimeMillis() - lastUpdated > ONE_DAY_MILLIS) {
                    new DataProcessorTask(context, codecampClient).execute();
                } else {
                    startNextActivity();
                }
            }
        }, (long) (SPLASH_TIME * 1000));
    }

    private void startNextActivity() {
        startMainActivity();
//        if (!userPrefs.name().get().isEmpty()) {
//            startMainActivity();
//        } else {
//            Intent intent = UserDataActivity_.intent(this).get();
//            startActivityForResult(intent, USER_DATA_CODE);
//        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
