package com.gdogaru.codecamp;

import android.support.multidex.MultiDexApplication;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 */
public class App extends MultiDexApplication {

    private static App instance;

    DiComponent diComponent;

    public static App instance() {
        return instance;
    }

    public static DiComponent getDiComponent() {
        return instance.diComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        diComponent = DaggerDiComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.font_roboto_regular))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
