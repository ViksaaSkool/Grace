package com.grace.app.injection.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.grace.app.GraceApplication;
import com.grace.app.model.GraceApiCall;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class AppModule {
    @NonNull
    private final GraceApplication mGraceApplication;

    public AppModule(@NonNull GraceApplication graceApplication) {
        mGraceApplication = graceApplication;
    }

    @Provides
    public Context provideAppContext() {
        return mGraceApplication;
    }

    @Provides
    public GraceApplication provideApp() {
        return mGraceApplication;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(GraceApplication application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    RequestManager provideGlide(GraceApplication application) {
        return Glide.with(application);
    }


    @Provides
    @Singleton
    GraceApiCall provideGraceApiCall() {
        return new GraceApiCall();
    }

}
