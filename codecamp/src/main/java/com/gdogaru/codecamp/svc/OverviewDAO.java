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

package com.gdogaru.codecamp.svc;

import android.content.Context;
import android.util.Log;

import com.gdogaru.codecamp.Logging;
import com.gdogaru.codecamp.model.Codecamp;
import com.gdogaru.codecamp.model.Overview;
import com.gdogaru.codecamp.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;
import java.io.InputStream;


public class OverviewDAO {

    private static final String FILENAME = "codecampoverview.json";

    public static Overview readOverview(Context context) throws Exception {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm").create();
        InputStream i = context.getApplicationContext().openFileInput(FILENAME);
        String json = IOUtils.toString(i);
        return gson.fromJson(json, Overview.class);
    }

    public static void saveOverview(Codecamp codecamp, Gson gson, Context context) {
        Overview ov = new Overview();
        ov.setId(codecamp.getId());
        ov.setTitle(codecamp.getTitle());
        ov.setShortName(codecamp.getShortName());
        ov.setDescription(codecamp.getDescription());
        ov.setStartDate(codecamp.getStartDate());
        ov.setEndDate(codecamp.getEndDate());
        ov.setLocation(codecamp.getLocation());

        String jsonOv = gson.toJson(ov);
        FileOutputStream fos = null;
        try {
            fos = context.getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(jsonOv.getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e(Logging.TAG, "Error saving overview.", e);
        }
    }

}
