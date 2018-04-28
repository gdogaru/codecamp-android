package com.gdogaru.codecamp.view.agenda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Codecamp;
import com.gdogaru.codecamp.model.Schedule;
import com.gdogaru.codecamp.svc.AppPreferences;
import com.gdogaru.codecamp.svc.BookmarkingService;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.AnalyticsHelper;
import com.gdogaru.codecamp.util.DateUtil;
import com.gdogaru.codecamp.view.BaseActivity;
import com.gdogaru.codecamp.view.agenda.calendar.CalendarFragment;
import com.gdogaru.codecamp.view.agenda.list.SessionsListFragment;
import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import icepick.State;

/**
 * Created by Gabriel on 10/23/2014.
 */
public class AgendaActivity extends BaseActivity {

    public static final String FAVORITES_ONLY = "FAVORITES_ONLY";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_switch)
    CheckBox viewSwitch;
    @BindView(R.id.favorite_switch)
    CheckBox favoriteSwitch;
    @BindView(R.id.title)
    TextView titleView;

    @Inject
    AppPreferences appPreferences;
    @Inject
    FirebaseAnalytics firebaseAnalytics;
    @Inject
    CodecampClient codecampClient;
    @State
    boolean favoritesOnly = false;
    @Inject
    BookmarkingService bookmarkingService;
    private Codecamp event;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, AgendaActivity.class));
        activity.overridePendingTransition(R.anim.act_right_left, R.anim.act_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.agenda_activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewSwitch.setChecked(appPreferences.getListViewList());

        favoriteSwitch.setChecked(favoritesOnly);

        Schedule schedule = codecampClient.getSchedule();
        event = codecampClient.getEvent();
        titleView.setText(String.format("%s - %s", DateUtil.formatDay(schedule.getDate()), event.getVenue().getCity()));

        SessionsFragment fragmentById = (SessionsFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragmentById == null) {
            showList();
        } else {
            fragmentById.setFavoritesOnly(favoritesOnly);
        }
    }

    @OnCheckedChanged(R.id.view_switch)
    public void onViewTypeChange(boolean checked) {
        if (appPreferences.getListViewList() != viewSwitch.isChecked()) {
            appPreferences.setListViewList(viewSwitch.isChecked());
            showList();
        }
    }

    @OnCheckedChanged(R.id.favorite_switch)
    public void onFavoriteChecked(boolean checked) {
        if (checked != favoritesOnly) {
            if (checked && bookmarkingService.getBookmarked(event.getTitle()).isEmpty()) {
                favoritesOnly = false;
                favoriteSwitch.setChecked(false);
                Toast.makeText(this, R.string.no_favorites_yet, Toast.LENGTH_SHORT).show();
                return;
            }

            favoritesOnly = checked;
            SessionsFragment f = (SessionsFragment) getSupportFragmentManager().findFragmentById(R.id.content);
            if (f != null) {
                f.setFavoritesOnly(favoritesOnly);
            }
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
        SessionsFragment sessionsFragment;
        if (appPreferences.getListViewList()) {
            sessionsFragment = new SessionsListFragment();
        } else {
            sessionsFragment = new CalendarFragment();
        }
        transaction.replace(R.id.content, sessionsFragment, sessionsFragment.getClass().getName());
        transaction.commit();
    }
}
