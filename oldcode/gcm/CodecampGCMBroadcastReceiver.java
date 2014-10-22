package com.gdogaru.codecamp.gcm;

import com.google.android.gcm.GCMBroadcastReceiver;


public class CodecampGCMBroadcastReceiver extends GCMBroadcastReceiver {

    private static boolean mReceiverSet = false;

    public String getGCMIntentServiceClassName(android.content.Context context) {
        return "com.tagonsoft.codecamp.gcm.CodecampGCMService";
    }
}
