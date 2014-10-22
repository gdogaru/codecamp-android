package com.gdogaru.codecamp.gcm;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.gdogaru.codecamp.Logging;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.prefs.UpdatePrefsUtil;
import com.gdogaru.codecamp.view.SplashScreenActivity;

import static com.gdogaru.codecamp.util.CommonUtilities.displayMessage;


public class CodecampGCMService extends GCMBaseIntentService {

    public CodecampGCMService() {
        super();
    }

    public CodecampGCMService(String... senderIds) {
        super(senderIds);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private void generateNotification(String message) {

        Intent notifyIntent = SplashScreenActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP).get();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_alert)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(message)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setContentText(message)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

        UpdatePrefsUtil.setLatestUpdates(this, System.currentTimeMillis());
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(Logging.TAG, "Device registered: regId = " + registrationId);
        boolean registered = ServerUtilities.register(context, registrationId);
        if (!registered) {
            Log.w(Logging.TAG, "Unable to register with server");
        }
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(Logging.TAG, "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(Logging.TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(Logging.TAG, "Received message");
        String message = intent.getStringExtra("message");
        generateNotification(message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(Logging.TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
//        generateNotification("No notification");
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(Logging.TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(Logging.TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }

    @Override
    protected String[] getSenderIds(Context context) {
        return new String[]{ServerUtilities.SENDER_ID};
    }
}
