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

package com.gdogaru.codecamp.svc.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.repository.AppPreferences;
import com.gdogaru.codecamp.svc.CodecampClient;

import javax.inject.Inject;

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
public class UpdateDataJob { //extends Job {
    public static final byte LOADED_FROM_ASSETS = 10;
    public static final byte ERROR_LOADING = -1;
    public static final byte SUCCESS = 0;
    public static final String TAG = "UpdateDataJob";

    @Inject
    ObjectMapper objectMapper;
    @Inject
    CodecampClient codecampClient;
    @Inject
    App app;
    @Inject
    AppPreferences appPreferences;

    @Inject
    public UpdateDataJob(ObjectMapper mapper, CodecampClient codecampClient, App app, AppPreferences appPreferences) {
        this.codecampClient = codecampClient;
        this.app = app;
        this.appPreferences = appPreferences;
    }
//
//    public static void schedule() {
//        new JobRequest.Builder(TAG)
//                .setExecutionWindow(1, 600_000)
//                .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
//                .setRequiresCharging(false)
//                .setRequiresDeviceIdle(false)
//                .build()
//                .schedule();
//    }

//    @NonNull
//    @Override
//    protected Result onRunJob(@NonNull Params params) {
//        Timber.i("Refreshing data....");
//        try {
//            signalStart();
//
//            downloadData();
//            signalEnd();
//        } catch (Throwable t) {
//            Timber.w(t, "Could not get data");
//            signalError();
//            return Result.FAILURE;
//        }
//        return Result.SUCCESS;
//    }


    public void downloadData() throws Exception {
//        codecampClient.fetchAllData();
    }

    private void signalStart() {
//        eventBus.post(new DataLoadingEvent(0));
//        appPreferences.setUpdating(true);
    }

    void signalEnd() {
//        appPreferences.setUpdating(false);
//        eventBus.post(new DataLoadingEvent(100));
    }

    void signalError() {
//        appPreferences.setUpdating(false);
//        eventBus.post(new DataLoadingEvent(-1));
    }
}
