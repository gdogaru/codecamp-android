package com.gdogaru.codecamp;


import com.gdogaru.codecamp.svc.jobs.UpdateDataJob;
import com.gdogaru.codecamp.view.LoadingDataActivity;
import com.gdogaru.codecamp.view.MainActivity;
import com.gdogaru.codecamp.view.SpeakersActivity;
import com.gdogaru.codecamp.view.SplashScreenActivity;
import com.gdogaru.codecamp.view.SponsorsActivity;
import com.gdogaru.codecamp.view.calendar.CalendarFragment;
import com.gdogaru.codecamp.view.session.SessionExpandedActivity;
import com.gdogaru.codecamp.view.session.SessionInfoFragment;
import com.gdogaru.codecamp.view.agenda.AgendaActivity;
import com.gdogaru.codecamp.view.agenda.SessionsListFragment;
import com.gdogaru.codecamp.view.speaker.SpeakerExpandedActivity;
import com.gdogaru.codecamp.view.speaker.SpeakerInfoFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 *
 */
@Singleton
@Component(modules = {AppModule.class, ApplicationModule.class})
public interface DiComponent {


    void inject(MainActivity activity);

    void inject(SplashScreenActivity activity);

    void inject(UpdateDataJob updateDataJob);

    void inject(AgendaActivity eventListActivity);

    void inject(LoadingDataActivity loadingDataActivity);

    void inject(SessionExpandedActivity sessionExpandedActivity);

    void inject(SessionsListFragment sessionsTabFragment);

    void inject(SessionInfoFragment sessionInfoFragment);

    void inject(CalendarFragment calendarFragment);

    void inject(SponsorsActivity sponsorsActivity);

    void inject(SpeakersActivity speakersActivity);

    void inject(SpeakerExpandedActivity speakerExpandedActivity);

    void inject(SpeakerInfoFragment speakerInfoFragment);
}
