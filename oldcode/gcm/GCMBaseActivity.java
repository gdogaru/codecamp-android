package com.gdogaru.codecamp.gcm;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import com.gdogaru.codecamp.Logging;
import com.gdogaru.codecamp.util.Preconditions;

public abstract class GCMBaseActivity extends FragmentActivity {

    AsyncTask<Void, Void, Void> mRegisterTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preconditions.checkNotNull(ServerUtilities.SERVER_URL, "SERVER_URL");
        Preconditions.checkNotNull(ServerUtilities.SENDER_ID, "SENDER_ID");
        // Make sure the device has the proper dependencies.
        final Context applicationContext = this.getApplicationContext();
        GCMRegistrar.checkDevice(applicationContext);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(applicationContext);
        final String regId = GCMRegistrar.getRegistrationId(applicationContext);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(applicationContext, ServerUtilities.SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(applicationContext)) {
                // Skips registration.
                Log.i(Logging.TAG, "Already registered with GCM");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered = ServerUtilities.register(applicationContext, regId);

                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(applicationContext);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        Context applicationContext = this.getApplicationContext();
        try {
            if (GCMRegistrar.isRegistered(applicationContext)) {
                GCMRegistrar.onDestroy(applicationContext);
            }
        } catch (Throwable e) {
            Log.e(Logging.TAG, "Error main on destroy", e);
        }

        super.onDestroy();
    }


}
