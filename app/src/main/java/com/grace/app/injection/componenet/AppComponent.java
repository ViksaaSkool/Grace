package com.grace.app.injection.componenet;

import android.content.Context;
import android.content.SharedPreferences;

import com.bumptech.glide.RequestManager;
import com.grace.app.GraceApplication;
import com.grace.app.injection.module.AppModule;
import com.grace.app.injection.module.GraceApiModule;
import com.grace.app.interactor.impl.LoadingInteractorImpl;
import com.grace.app.model.GraceApiCall;
import com.grace.app.view.dialog.DisclaimerTermsAndConditionsDialogFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, GraceApiModule.class})
public interface AppComponent {
    Context getAppContext();

    GraceApplication getApp();

    SharedPreferences sharedPreferences();

    RequestManager glide();

    GraceApiCall graceApiCall();

    GraceApiModule.GraceInterface graceInterface();

    void inject(DisclaimerTermsAndConditionsDialogFragment disclaimerTermsAndConditionsDialogFragment);

    void inject(LoadingInteractorImpl loadingInteractor);
}