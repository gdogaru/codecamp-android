package com.gdogaru.codecamp;

import android.util.Log;

import com.gdogaru.codecamp.model.json.DateTypeAdapter;
import com.gdogaru.codecamp.model.json.LocalTimeTypeAdapter;
import com.gdogaru.codecamp.svc.AppPreferences;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.LocalTime;

import java.util.Date;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by Gabriel on 10/2/2016.
 */
@Module
public class AppModule {

    @Provides
    AppPreferences appPreferences(App app) {
        return new AppPreferences(app);
    }

    @Provides
    public Gson createGson() {
        return new GsonBuilder()
                .setDateFormat(DateTypeAdapter.FORMAT)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .create();
    }

    @Provides
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    public CodecampClient getCodecampClient(App app, OkHttpClient client, Gson gson, AppPreferences appPreferences, EventBus eventBus) {
        return new CodecampClient(gson, app, client, appPreferences, eventBus);

    }

    @Provides
    public JobManager configureJobManager(App app) {
        Configuration configuration = new Configuration.Builder(app)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minute
                .build();
        return new JobManager(app, configuration);
    }

    @Provides
    public EventBus eventBus() {
        return EventBus.getDefault();
    }
}
