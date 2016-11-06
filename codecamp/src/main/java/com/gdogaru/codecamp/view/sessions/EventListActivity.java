package com.gdogaru.codecamp.view.sessions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.ToggleButton;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.svc.AppPreferences;
import com.gdogaru.codecamp.view.BaseActivity;
import com.gdogaru.codecamp.view.calendar.CalendarFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * Created by Gabriel on 10/23/2014.
 */
public class EventListActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_switch)
    ToggleButton viewSwitch;
    @Inject
    AppPreferences appPreferences;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, EventListActivity.class));
        activity.overridePendingTransition(R.anim.act_right_left, R.anim.act_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getDiComponent().inject(this);

        setContentView(R.layout.event_list_activity);
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
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out); //, 0, R.anim.hold);
        if (appPreferences.getListViewList()) {
            transaction.replace(R.id.content, new SessionsListFragment());
        } else {
            transaction.replace(R.id.content, new CalendarFragment());
        }
        transaction.commit();
    }

}
