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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.svc.events.DataLoadingEvent;
import com.gdogaru.codecamp.svc.jobs.UpdateDataJob;
import com.gdogaru.codecamp.view.home.MainActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class LoadingDataActivity extends BaseActivity implements HasSupportFragmentInjector {

    private static final String START_UPDATE = "start_update";

    @BindView(R.id.progress)
    ProgressBar progressBar;
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    public static void startTop(Context context) {
        Intent intent = new Intent(context, LoadingDataActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startUpdate(Activity activity) {
        Intent intent = new Intent(activity, LoadingDataActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(START_UPDATE, true);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_data);
        ButterKnife.bind(this);

        if (getActionBar() != null) {
            getActionBar().hide();
        }
        if (getIntent().hasExtra(START_UPDATE)) {
            UpdateDataJob.schedule();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataLoading(DataLoadingEvent event) {
        if (event.progress < 0) {
            Toast.makeText(this, "Could not update", Toast.LENGTH_SHORT).show();
            MainActivity.Companion.start(this);
            finish();
        } else if (event.progress >= 100) {
            MainActivity.Companion.start(this);
            finish();
        } else {
            progressBar.setProgress(event.progress);
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
