package com.grace.app.injection.module.view;

import androidx.annotation.NonNull;

import com.grace.app.interactor.GraceSplashScreenInteractor;
import com.grace.app.interactor.impl.GraceSplashScreenInteractorImpl;
import com.grace.app.presenter.GraceSplashScreenPresenter;
import com.grace.app.presenter.impl.GraceSplashScreenPresenterImpl;
import com.grace.app.presenter.loader.PresenterFactory;

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
        return () -> new GraceSplashScreenPresenterImpl(interactor);
    }
}
