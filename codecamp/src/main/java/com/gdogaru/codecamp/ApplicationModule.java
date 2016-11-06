package com.gdogaru.codecamp;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public class ApplicationModule {
    App application;

    public ApplicationModule(App application) {
        this.application = application;
    }

    @Provides
    App app() {
        return application;
    }

    @Provides
    Application application() {
        return application;
    }

    @Provides
    Context context() {
        return application;
    }
}
