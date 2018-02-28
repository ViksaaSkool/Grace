package com.grace.app;

import android.app.Application;
import android.support.annotation.NonNull;

import com.grace.app.constants.Constants;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.componenet.DaggerAppComponent;
import com.grace.app.injection.module.AppModule;
import com.grace.app.injection.module.GraceApiModule;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public final class GraceApplication extends Application {
    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .graceApiModule(new GraceApiModule())
                .build();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(Constants.CABIN_REGULAR)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @NonNull
    public static AppComponent getAppComponent() {
        return mAppComponent;
    }
}