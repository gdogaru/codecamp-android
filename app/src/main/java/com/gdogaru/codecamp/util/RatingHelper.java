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

package com.gdogaru.codecamp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

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
            prefs = App.Companion.instance().getSharedPreferences(MY_RATING_PREFS, Context.MODE_PRIVATE);
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

    public static void tryToRate(Activity activity) {
        if (!prefs().getBoolean(NEVER, false) && prefs().getInt(VIEWS, 0) >= VIEWS_UNTIL) {
            showRateDialog(activity);
            setTimes(0);
        }
    }

    public static void showRateDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Codecamp_AlertDialogThemeCompat);
        View view = View.inflate(activity, R.layout.rate_dialog, null);
        Button rateButton = view.findViewById(R.id.rateButton);
        Button laterButton = view.findViewById(R.id.laterButton);
        Button neverButton = view.findViewById(R.id.neverButton);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setView(view, 1, 1, 1, 1);
        setRateButtonClickListener(dialog, rateButton, activity);
        setLaterButtonClickListener(dialog, laterButton);
        setNeverButtonClickListener(dialog, neverButton);
        if (!activity.isFinishing()) dialog.show();
    }

    public static void goToPlay(Context context) {
        prefs().edit().putBoolean(NEVER, true).apply();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));

        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Google Play not found", Toast.LENGTH_SHORT).show();
        }
    }

    private static void setRateButtonClickListener(final AlertDialog dialog, Button rateButton, final Context context) {
        rateButton.setOnClickListener(v -> {
            logEvent("rate_now");
            dialog.dismiss();
            goToPlay(context);
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
        FirebaseAnalytics.getInstance(App.Companion.instance()).logEvent(AnalyticsHelper.INSTANCE.normalize(name), bundle);
    }
}
