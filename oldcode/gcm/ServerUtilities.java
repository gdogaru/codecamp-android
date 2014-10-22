/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gdogaru.codecamp.gcm;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.gdogaru.codecamp.Logging;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.util.CommonUtilities;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

    public static final String ZUMO_APP_ID = "rvFDWxpvbJyobzmGuNFmUghdIxSqwQ70";
    public static final String ZUMO_HEADER = "X-ZUMO-APPLICATION";
    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    static final String SERVER_URL = "https://codecampevents.azure-mobile.net/tables/Devices";
    /**
     * Google API project id registered to use GCM.
     */
    static final String SENDER_ID = "771082214216";
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static final String PREFS_NAME = "GCM_PREFS";
    private static final String SERVER_ID = "SERVER_ID";

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    static boolean register(final Context context, final String regId) {
        Log.i(Logging.TAG, "registering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL;
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(Logging.TAG, "Attempt #" + i + " to register");
            try {
                CommonUtilities.displayMessage(context, context.getString(R.string.server_registering, i, MAX_ATTEMPTS));
                RegisterResponse response = svcregister(new RegisterOperation(regId));
                saveRegistration(context, response);
                GCMRegistrar.setRegisteredOnServer(context, true);
                String message = context.getString(R.string.server_registered);
                CommonUtilities.displayMessage(context, message);
                return true;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(Logging.TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(Logging.TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(Logging.TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        String message = context.getString(R.string.server_register_error, MAX_ATTEMPTS);
        CommonUtilities.displayMessage(context, message);
        return false;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    static void unregister(final Context context) {
        Log.i(Logging.TAG, "unregistering device");
        try {
            svcunregister(getRegistration(context));
            GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            CommonUtilities.displayMessage(context, message);
        } catch (Exception e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            String message = context.getString(R.string.server_unregister_error, e.getMessage());
            CommonUtilities.displayMessage(context, message);
        }
    }

    private static void svcunregister(int serverRegId) throws IOException {
        URL url;
        try {
            url = new URL(SERVER_URL + "/" + serverRegId);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + SERVER_URL);
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty(ZUMO_HEADER, ZUMO_APP_ID);
            conn.connect();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 204) {
                Log.e(Logging.TAG, "Server responded with " + conn.getResponseMessage());
                throw new IOException("Delete failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Issue a POST request to the server.
     *
     * @param operation RegisterOperation
     * @throws IOException propagated from POST.
     */
    private static RegisterResponse svcregister(RegisterOperation operation) throws IOException {
        URL url;
        try {
            url = new URL(SERVER_URL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + SERVER_URL);
        }
        Gson gson = new Gson();
        String body = gson.toJson(operation);
        Log.v(Logging.TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes("UTF-8");
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-ZUMO-APPLICATION", ZUMO_APP_ID);
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 201 && status != 200) {
                Log.e(Logging.TAG, "Server responded with " + conn.getResponseMessage());
                throw new IOException("Post failed with error code " + status);
            } else {
                String stringResponse = IOUtils.toString(conn.getInputStream());
                return gson.fromJson(stringResponse, RegisterResponse.class);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static void saveRegistration(Context context, RegisterResponse response) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putInt(SERVER_ID, response.getId()).commit();
    }

    private static int getRegistration(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(SERVER_ID, -1);
    }

}
