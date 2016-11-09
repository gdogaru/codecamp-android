package com.gdogaru.codecamp.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;


public class RatingHelper {

    public static final String NEVER = "never";
    public static final String VIEWS = "views";
    public static final String MY_RATING_PREFS = "MyRatingPrefs";
    public static final int VIEWS_UNTIL = 4;
    private static SharedPreferences prefs;

    public static SharedPreferences prefs() {
        if (prefs == null) {
            prefs = App.instance().getSharedPreferences(MY_RATING_PREFS, Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public static void logUsage(Context context) {
        int usage = prefs().getInt(VIEWS, 0);
        setTimes(usage);
    }

    private static void setTimes(int usage) {
        SharedPreferences.Editor editor = prefs().edit();
        editor.putInt(VIEWS, ++usage);
        editor.apply();
    }

    public static void tryToRate(Context context) {
        if (!prefs().getBoolean(NEVER, false) && prefs().getInt(VIEWS, 0) >= VIEWS_UNTIL) {
            showRateDialog(context);
            setTimes(0);
        }
    }

    public static void showRateDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Codecamp_AlertDialogThemeCompat);
        View view = View.inflate(context, R.layout.rate_dialog, null);
        Button rateButton = (Button) view.findViewById(R.id.rateButton);
        Button laterButton = (Button) view.findViewById(R.id.laterButton);
        Button neverButton = (Button) view.findViewById(R.id.neverButton);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(view, 1, 1, 1, 1);
        setRateButtonClickListener(dialog, rateButton, context);
        setLaterButtonClickListener(dialog, laterButton);
        setNeverButtonClickListener(dialog, neverButton);
        dialog.show();
    }

    public static void goToPlay(Context context) {
        prefs().edit().putBoolean(NEVER, true).apply();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));

        PackageManager manager = App.instance().getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Google Play not found", Toast.LENGTH_SHORT).show();
        }
    }

    private static void setRateButtonClickListener(final AlertDialog dialog, Button rateButton, final Context context) {
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logEvent("rate_now");
                dialog.dismiss();
                goToPlay(context);
            }
        });
    }

    private static void setLaterButtonClickListener(final AlertDialog dialog, Button laterButton) {
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logEvent("rate_later");
                dialog.dismiss();
                prefs().edit().putInt(VIEWS, 0).apply();
            }
        });
    }

    private static void setNeverButtonClickListener(final AlertDialog dialog, Button neverButton) {
        neverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logEvent("rate_never");
                prefs().edit().putBoolean(NEVER, true).apply();
                dialog.dismiss();
            }
        });
    }

    private static void logEvent(String name) {
        Bundle bundle = new Bundle();
        bundle.putInt("value", 1);
        FirebaseAnalytics.getInstance(App.instance()).logEvent(name, bundle);
    }
}
