package com.gdogaru.codecamp.util;

import android.content.Context;
import android.util.Log;
import com.gdogaru.codecamp.Logging;

/**
 * User: morariuoana
 */
public class CommonUtilities {
    public static void displayMessage(Context context, String string) {
//        AlertDialog dialog = new AlertDialog.Builder(context).create();
//        dialog.setMessage(string);
//        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        dialog.show();
        Log.i(Logging.TAG, string);
    }
}
