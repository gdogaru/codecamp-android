package com.gdogaru.codecamp.view.agenda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.ToggleButton;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.svc.AppPreferences;
import com.gdogaru.codecamp.util.AnalyticsHelper;
import com.gdogaru.codecamp.view.BaseActivity;
import com.gdogaru.codecamp.view.calendar.CalendarFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * Created by Gabriel on 10/23/2014.
 */
public class AgendaActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_switch)
    ToggleButton viewSwitch;
    @Inject
    AppPreferences appPreferences;
    @Inject
    FirebaseAnalytics firebaseAnalytics;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, AgendaActivity.class));
        activity.overridePendingTransition(R.anim.act_right_left, R.anim.act_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getDiComponent().inject(this);

        setContentView(R.layout.agenda_activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportFragmentManager().findFragmentById(R.id.content) == null) {
            showList();
        }

        viewSwitch.setChecked(appPreferences.getListViewList());
    }

    @OnCheckedChanged(R.id.view_switch)
    public void onViewTypeChange() {
        if (appPreferences.getListViewList() != viewSwitch.isChecked()) {
            appPreferences.setListViewList(viewSwitch.isChecked());
            showList();
        }
    }

    private void showList() {
        Bundle bundle = new Bundle();
        String value = appPreferences.getListViewList() ? "list" : "calendar";
        bundle.putString("view_type", value);
        firebaseAnalytics.logEvent(AnalyticsHelper.normalize("agenda_view_" + value), bundle);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out); //, 0, R.anim.hold);
        if (appPreferences.getListViewList()) {
            transaction.replace(R.id.content, new SessionsListFragment(), "session_list");
        } else {
            transaction.replace(R.id.content, new CalendarFragment(), "calendar");
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.hold, R.anim.act_slide_down);
    }
}
