package com.grace.app;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.componenet.DaggerAppComponent;
import com.grace.app.injection.module.AppModule;


public final class GraceApplication extends Application {
    private static AppComponent mAppComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        // Calligraphy 2.x is not compatible with modern AppCompat; keep fonts default.
    }

    @NonNull
    public static AppComponent getAppComponent() {
        return mAppComponent;
    }
}
