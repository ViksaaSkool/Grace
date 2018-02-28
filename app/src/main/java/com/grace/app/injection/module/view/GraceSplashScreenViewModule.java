package com.grace.app.injection.module.view;

import android.support.annotation.NonNull;

import com.grace.app.interactor.GraceSplashScreenInteractor;
import com.grace.app.interactor.impl.GraceSplashScreenInteractorImpl;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.presenter.GraceSplashScreenPresenter;
import com.grace.app.presenter.impl.GraceSplashScreenPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public final class GraceSplashScreenViewModule {
    @Provides
    public GraceSplashScreenInteractor provideInteractor() {
        return new GraceSplashScreenInteractorImpl();
    }

    @Provides
    public PresenterFactory<GraceSplashScreenPresenter> providePresenterFactory(@NonNull final GraceSplashScreenInteractor interactor) {
        return new PresenterFactory<GraceSplashScreenPresenter>() {
            @NonNull
            @Override
            public GraceSplashScreenPresenter create() {
                return new GraceSplashScreenPresenterImpl(interactor);
            }
        };
    }
}
